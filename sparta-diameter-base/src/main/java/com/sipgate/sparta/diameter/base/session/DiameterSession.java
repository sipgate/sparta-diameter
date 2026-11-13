package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.Answer;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.DiameterResultCodeException;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.ErrorAnswer;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import com.sipgate.sparta.diameter.base.session.relay.RelayAnswerHook;
import com.sipgate.sparta.diameter.base.session.relay.RelayedAnswer;
import com.sipgate.sparta.diameter.base.session.relay.RelayFailedException;
import com.sipgate.sparta.diameter.base.session.relay.RelayTranslation;
import com.sipgate.sparta.diameter.base.session.relay.RelayTranslationStore;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Shared state and helpers for initiator- and responder-side Diameter sessions.
 * <p>
 * The relay path (RFC 6733 §6.1.9 request forwarding and §6.2.2 answer forwarding) is
 * implemented here instead of in a dedicated {@code DiameterRelaySession} subtype:
 * relay state is inherently per-connection state (fresh hop-by-hop identifiers, the
 * pending-request map, and the answer dispatch all live on the session that owns the
 * egress connection), so every session can act as the egress side of a relay hop.
 * See {@link com.sipgate.sparta.diameter.base.session.relay} for the relay-specific
 * types ({@code RelayTranslation}, {@code RelayTranslationStore}, {@code LoopDetector}).
 * </p>
 */
public abstract class DiameterSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterSession.class);

    /**
     * Last known Origin-State-Id per remote Origin-Host (RFC 6733 §8.6/§8.16).
     * <p>
     * Sessions are per-connection instances and are never reused across reconnects
     * (ADR-0004), so the memory needed to detect a peer restart across a reconnect
     * cannot live on the session. It is keyed by the remote Origin-Host and therefore
     * valid process-wide: a given remote host can only be reachable through one node
     * identity. See {@link #recordRemoteOriginStateId(String, long)}.
     * </p>
     */
    private static final ConcurrentHashMap<String, Long> REMOTE_ORIGIN_STATE_IDS = new ConcurrentHashMap<>();

    public void onConnected(final DiameterPeer peer) {
        this.peer = peer;
    }

    public abstract void onMessage(IncomingCommand command);

    protected final DiameterNodeConfig config;
    protected final CapabilityNegotiator negotiator;
    protected final DiameterIdentifiers identifiers;
    protected final DiameterSessionMeters meters;

    protected PeerState peerState;
    protected WatchdogState watchdogState;
    private DiameterPeer peer;

    protected boolean shuttingDown = false;

    private HopByHopId pendingDwrHopByHop;
    private Future<?> twTimer;

    private static final Future<Void> NO_TIMEOUT_TASK = CompletableFuture.completedFuture(null);
    private final ConcurrentHashMap<HopByHopId, PendingRequest<?>> pendingRequests =
            new ConcurrentHashMap<>();

    /**
     * Relay translations for requests forwarded through this session's connection,
     * keyed by the outbound hop-by-hop identifier this session minted. See
     * {@link RelayTranslationStore} for the cleanup contract: every translation is
     * paired with a pending request under the same key, so the per-request timeout
     * already scheduled on the peer's event loop (plus write failures and connection
     * loss) removes both halves — no separate cleanup timer is needed.
     */
    private final RelayTranslationStore relayTranslations = new RelayTranslationStore();

    private final Map<Class<? extends IncomingCommand>, DiameterRequestHandler<?, ?>> handlers = new HashMap<>();
    private DiameterRequestHandler<?, ?> fallbackHandler;
    private RelayAnswerHook relayAnswerHook;

    DiameterSession(final DiameterNodeConfig config, final MeterRegistry meterRegistry) {
        this.config = config;
        this.negotiator = new CapabilityNegotiator();
        this.identifiers = new DiameterIdentifiers();
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.INITIAL;
        this.meters = new DiameterSessionMeters(meterRegistry);
    }

    /**
     * Sends an outgoing request and returns a future that completes when the matching
     * answer arrives (correlated by hop-by-hop identifier).
     */
    public <A extends Answer> CompletableFuture<A> send(
            final OutgoingRequest<A> request) {
        if (peerState != PeerState.I_OPEN && peerState != PeerState.R_OPEN) {
            final CompletableFuture<A> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new IllegalStateException("Cannot send in state: " + peerState));
            return failed;
        }
        final HopByHopId hopByHop = identifiers.nextHopByHop();
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();
        return sendAndTrack(request, hopByHop, endToEnd, config.getRequestTimeout());
    }

    ChannelFuture send(final OutgoingAnswer answer) {
        return send(peer, answer);
    }

    private ChannelFuture send(final DiameterPeer peer, final OutgoingAnswer answer) {
        if (answer.getOriginHost() == null) answer.setOriginHost(config.getOriginHost());
        if (answer.getOriginRealm() == null) answer.setOriginRealm(config.getOriginRealm());
        return peer.send(answer);
    }

    ChannelFuture send(final OutgoingRequest<?> request, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (request.getOriginHost() == null) request.setOriginHost(config.getOriginHost());
        if (request.getOriginRealm() == null) request.setOriginRealm(config.getOriginRealm());
        return peer.send(request, hopByHop, endToEnd);
    }

    /**
     * The peer this session is connected to; {@code null} before {@code onConnected}.
     *
     * <p>Exposed because relay orchestration spans two sessions: the session that
     * received a request passes its own peer as {@code upstreamPeer} to
     * {@link #relay(DiameterSession, IncomingRequest, OutgoingRequest)} on the
     * egress session.</p>
     */
    public DiameterPeer peer() {
        return peer;
    }

    /**
     * Forwards a received request to the peer of {@code outboundSession} as a relay hop
     * (RFC 6733 §6.1.9) and returns a future that completes when the downstream answer
     * arrives.
     *
     * <p><strong>Placement note (why this lives on {@code DiameterSession}):</strong>
     * relay state is inherently per-connection — the outbound hop-by-hop identifier must
     * be unique on the egress connection (RFC 6733 §3), and the answer dispatch
     * ({@code complete}) runs on the session that owns that connection. A dedicated
     * {@code DiameterRelaySession} subtype would either duplicate the
     * initiator/responder capability state machines or be an empty marker class.
     * Every session can therefore act as the egress side of a relay hop; the
     * relay-specific types live in
     * {@link com.sipgate.sparta.diameter.base.session.relay}.</p>
     *
     * <p><strong>Identifier semantics:</strong> a fresh hop-by-hop identifier is minted
     * from the egress session's {@link DiameterIdentifiers}; the end-to-end identifier
     * of the original request is preserved on the wire unchanged (RFC 6733 §3). When
     * the downstream answer arrives, it is forwarded to the upstream peer with the
     * original hop-by-hop identifier restored (RFC 6733 §6.2.2) via
     * {@link #sendAnswerRestored(IncomingAnswer)}, and the returned future completes
     * with the downstream answer.</p>
     *
     * <p><strong>Non-mutation guarantee:</strong> the forwarded request object is never
     * modified. In particular, {@code relay} bypasses the fill-if-absent Origin-Host /
     * Origin-Realm stamping that {@code send(OutgoingRequest, ...)} performs: forwarded
     * requests already carry the original Origin AVPs per RFC 6733 §6.1.9 (all other
     * AVPs are forwarded unchanged as well), so stamping our own identity would be a
     * protocol violation. The application is responsible for appending a
     * Route-Record AVP before calling {@code relay} (see ADR-0015).</p>
     *
     * @param upstreamSession the session on whose connection {@code received} arrived;
                        the restored answer is sent to its peer
     * @param received       the original inbound request; source of the preserved
                        end-to-end identifier and the hop-by-hop identifier to restore
     * @param forward        the outbound request built by the application; never mutated
     * @return a future that completes with the downstream answer (including E-bit error
            answers) after it was forwarded upstream, or exceptionally with a
            {@link RelayFailedException} when no answer was forwarded; its cause is an
            {@code IllegalStateException} (not open, connection lost), a
            {@code TimeoutException}, or the write failure cause
     * @throws NullPointerException if any argument is {@code null}
     */
    public CompletableFuture<IncomingAnswer> relay(
            final DiameterSession upstreamSession,
            final IncomingRequest<?> received,
            final OutgoingRequest<?> forward) {
        Objects.requireNonNull(upstreamSession, "upstreamSession must not be null");
        Objects.requireNonNull(received, "received must not be null");
        Objects.requireNonNull(forward, "forward must not be null");

        final CompletableFuture<IncomingAnswer> future = new CompletableFuture<>();
        if (peerState != PeerState.I_OPEN && peerState != PeerState.R_OPEN) {
            future.completeExceptionally(new RelayFailedException("relay failed",
                    new IllegalStateException("Cannot relay in state: " + peerState)));
            return future;
        }

        final HopByHopId outboundHopByHop = identifiers.nextHopByHop();
        // RFC 6733 §3: the end-to-end identifier of the original request is preserved.
        final EndToEndId endToEnd = received.endToEndId();
        final int commandCode = forward.getCommandCode();
        final int applicationId = forward.getApplicationId();

        // Translation first, then the pending entry: fail() only removes a translation when
        // it finds the pending entry, so the reverse order lets a connection loss between
        // the two calls leak the translation forever.
        relayTranslations.put(outboundHopByHop, new RelayTranslation(
                upstreamSession.peer(),
                received.hopByHopId(),
                endToEnd,
                System.currentTimeMillis(),
                received));
        startTracking(future, commandCode, applicationId, outboundHopByHop, config.getRequestTimeout());

        // Deliberately NOT via send(request, hopByHop, endToEnd): that helper stamps
        // absent Origin-Host/Origin-Realm with our own identity, which would mutate the
        // forwarded request and misrepresent its origin (RFC 6733 §6.1.9).
        peer.send(forward, outboundHopByHop, endToEnd).addListener(writeResult -> {
            if (!writeResult.isSuccess()) {
                fail(outboundHopByHop, writeResult.cause());
            }
        });
        return future;
    }

    /**
     * Forwards a downstream answer upstream with the original hop-by-hop identifier
     * restored (RFC 6733 §6.2.2). Looks up the relay translation by the answer's
     * hop-by-hop identifier; on a hit, removes the translation, sends the answer —
     * with all AVPs unchanged, including the downstream origin's Origin-Host and
     * Origin-Realm — to the upstream peer. The future returned by
     * {@link #relay(DiameterSession, IncomingRequest, OutgoingRequest)} is completed
     * by the answer dispatch ({@code complete}).
     *
     * <p>Before the restored answer is sent, the {@link RelayAnswerHook} of this session (if
     * one is set, see {@link #setRelayAnswerHook}) may append AVPs to it.</p>
     *
     * <p>Called automatically from the answer dispatch ({@code complete}). Public so applications that intercept answers
     * themselves can trigger the same restoration.</p>
     *
     * @param downstreamAnswer the answer received from the downstream peer
     * @return {@code true} if a relay translation matched and the answer was forwarded
     */
    public boolean sendAnswerRestored(final IncomingAnswer downstreamAnswer) {
        Objects.requireNonNull(downstreamAnswer, "downstreamAnswer must not be null");
        final RelayTranslation translation = relayTranslations.remove(downstreamAnswer.hopByHopId());
        if (translation == null) {
            return false;
        }
        final RelayedAnswer restored = RelayedAnswer.of(downstreamAnswer, translation.originalHopByHop());
        final RelayAnswerHook hook = relayAnswerHook;
        if (hook != null) {
            try {
                hook.beforeForward(downstreamAnswer, translation.originalRequest(), restored);
            } catch (final RuntimeException e) {
                // The answer is still forwarded: losing it would turn a hook bug into a
                // request timeout for the upstream peer.
                LOGGER.error("relay answer hook failed (hbh {}), forwarding the answer anyway",
                        translation.originalHopByHop(), e);
            }
        }
        // Fill-if-absent origin stamping is intentionally skipped: a relayed answer
        // carries the answering endpoint's identity (RFC 6733 §6.2.2), not ours.
        translation.upstreamPeer().send(restored).addListener(future -> {
            if (!future.isSuccess()) {
                LOGGER.warn("Failed to send restored answer upstream (hbh {}): {}",
                        translation.originalHopByHop(), future.cause());
            }
        });
        return true;
    }

    /**
     * Registers a handler for inbound requests of the given type.
     */
    public <R extends IncomingRequest<A>, A extends OutgoingAnswer> void setHandler(
            final Class<R> requestClass,
            final DiameterRequestHandler<R, A> handler) {
        if (handlers.containsKey(requestClass)) {
            throw new IllegalStateException(
                    "Handler for " + requestClass.getSimpleName() + " is already registered");
        }
        handlers.put(requestClass, handler);
        LOGGER.debug("registered handler for {}", requestClass.getName());
    }

    /**
     * Registers a handler for every inbound request without a handler of its own type,
     * including {@link com.sipgate.sparta.diameter.base.core.GenericRequest}s of commands no
     * factory knows. Without it such requests are answered {@code DIAMETER_COMMAND_UNSUPPORTED}.
     */
    public void setFallbackHandler(
            final DiameterRequestHandler<IncomingRequest<OutgoingAnswer>, OutgoingAnswer> handler) {
        this.fallbackHandler = Objects.requireNonNull(handler, "handler must not be null");
    }

    /**
     * Sets the hook invoked for every relayed answer this session forwards upstream (the
     * egress side of {@link #relay}), right before the restored answer is sent. The hook
     * may append AVPs to the outgoing answer (e.g. DOIC); {@code null} removes it.
     * Default: no hook.
     */
    public void setRelayAnswerHook(final RelayAnswerHook hook) {
        this.relayAnswerHook = hook;
    }

    /**
     * Closes the connection immediately without sending a Disconnect-Peer-Request.
     * Sets {@code shuttingDown} to suppress reconnect on the initiator side.
     *
     * <p>Use for protocol errors (e.g. unsupported Diameter version) where continued
     * communication with this peer is impossible.
     */
    public void stop() {
        LOGGER.info("stopping forcefully");
        if (prepareDisconnect()) {
            shuttingDown = true;
            closePeer();
        }
    }

    /**
     * Initiates a graceful disconnection by sending a Disconnect-Peer-Request with
     * {@code Disconnect-Cause = DO_NOT_WANT_TO_TALK_TO_YOU}.
     * Sets {@code shuttingDown} to suppress reconnect on the initiator side.
     *
     * <p>Use for operator-initiated shutdown where the node does not intend to reconnect.
     */
    public void stopGracefully() {
        LOGGER.info("stopping gracefully");
        if (prepareDisconnect()) {
            shuttingDown = true;
            gracefulDisconnect(buildDpr(DiameterConstants.DCC_DO_NOT_WANT_TO_TALK_TO_YOU));
        }
    }

    /**
     * Initiates a graceful disconnection by sending a Disconnect-Peer-Request with
     * {@code Disconnect-Cause = REBOOTING}.
     * Does <em>not</em> set {@code shuttingDown}, so the Tc timer will schedule a
     * reconnect after the connection closes.
     *
     * <p>Use when the node needs to close and reconnect (e.g. after a configuration reload).
     */
    public void closeGracefully() {
        LOGGER.info("closing gracefully");
        if (prepareDisconnect()) {
            gracefulDisconnect(buildDpr(DiameterConstants.DCC_REBOOTING));
        }
    }

    private boolean prepareDisconnect() {
        if (peerState == PeerState.I_OPEN || peerState == PeerState.R_OPEN) {
            stopWatchdog();
            return true;
        }
        LOGGER.debug("not open");
        return false;
    }

    private void gracefulDisconnect(final DisconnectPeerRequest.Out dpr) {
        peerState = PeerState.CLOSING;
        sendAndTrack(dpr).whenComplete((dpa, err) -> closePeer());
    }

    /**
     * Handles a peer-initiated DPR: sends a DPA and, if
     * {@link #closeTransportAfterInboundDpa()} returns {@code true} (the default), closes the
     * channel once the DPA is written. Otherwise the channel stays open (state
     * {@link PeerState#CLOSING}) until the peer closes it.
     */
    protected void handleInboundDpr(final DisconnectPeerRequest.In dpr) {
        stopWatchdog();
        peerState = PeerState.CLOSING;
        final ChannelFuture sent = send(DiameterMessageFactory.createAnswer(dpr, DiameterConstants.RES_DIAMETER_SUCCESS));
        if (closeTransportAfterInboundDpa()) {
            sent.addListener(ignored -> closePeer());
        }
    }

    /**
     * Whether the transport is closed after the DPA to a peer-initiated DPR has been written.
     * <p>
     * The default {@code true} is the RFC 6733 §5.4 behaviour. A subclass may return
     * {@code false} to send the DPA and leave the transport open until the peer closes it,
     * for wire compatibility with peers that expect to close the connection themselves.
     * </p>
     *
     * @return {@code true} to close the transport after the DPA, {@code false} to keep it open
     */
    protected boolean closeTransportAfterInboundDpa() {
        return true;
    }

    protected void closePeer() {
        LOGGER.debug("closing peer");
        peerState = PeerState.CLOSED;
        peer.close();
    }

    /**
     * Dispatches an inbound request to the handler registered for its type, else to the
     * fallback handler, or sends {@code DIAMETER_COMMAND_UNSUPPORTED} if neither exists.
     *
     * <p>A handler that returns the future of {@link #relay} completes with the
     * downstream {@link IncomingAnswer}, which the relay answer path has already
     * forwarded upstream; nothing further is sent for it. If that future fails with a
     * {@link RelayFailedException} (nothing was forwarded), the request is answered with
     * the protocol error {@code DIAMETER_UNABLE_TO_DELIVER} (3002, E-bit, RFC 6733 §7.1.3).
     * Any other failure, including a handler that throws synchronously, is answered with
     * {@code DIAMETER_UNABLE_TO_COMPLY}.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void dispatchInboundRequest(final IncomingRequest<? extends OutgoingAnswer> request) {
        final int commandCode = request.getCommandCode();
        final int applicationId = request.getApplicationId();
        final DiameterRequestHandler typed = handlers.get(request.getClass());
        final DiameterRequestHandler handler = typed != null ? typed : fallbackHandler;
        if (handler == null) {
            LOGGER.warn("request unsupported: {}", request.getCommandName());
            // 3001 is a protocol error (RFC 6733 §7.1.3) → E-bit answer
            send(DiameterMessageFactory.createErrorAnswer(request, DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED));
            return;
        }
        final Timer.Sample handlerSample = meters.startTimer();
        final CompletableFuture<?> handled = invokeHandler(handler, request);
        handled.whenComplete((answer, err) -> {
            meters.stopHandlerTimer(handlerSample, commandCode, applicationId);
            // Keep this if even though nobody throws DiameterErrorAnswerException inside this lib. This is a way for
            // applications to indicate a diameter business error.
            err = extractEffectiveCause(err);
            // The carried answer is an ErrorAnswer.Out for protocol errors and a regular typed
            // answer for an Experimental-Result (RFC 6733 §7.6 - no E-bit on those).
            if (err instanceof final DiameterErrorAnswerException e && e.getAnswer() instanceof final OutgoingAnswer out) {
                send(out);
                meters.recordHandlerError(commandCode, applicationId, err);
            } else if (err instanceof RelayFailedException) {
                // 3002 is a protocol error (RFC 6733 §7.1.3) → E-bit answer
                send(DiameterMessageFactory.createErrorAnswer(request, DiameterConstants.RES_DIAMETER_UNABLE_TO_DELIVER));
                meters.recordHandlerError(commandCode, applicationId, err);
            } else if (err != null) {
                send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
                meters.recordHandlerError(commandCode, applicationId, err);
            } else if (answer instanceof final IncomingAnswer relayed) {
                // relay() preserves the end-to-end identifier; anything else was never forwarded.
                if (!relayed.endToEndId().equals(request.endToEndId())) {
                    LOGGER.warn("handler for {} returned a received answer that was not relayed for this request",
                            request.getCommandName());
                    send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
                }
            } else {
                send((OutgoingAnswer) answer);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static CompletableFuture<?> invokeHandler(final DiameterRequestHandler handler,
                                                      final IncomingRequest<?> request) {
        try {
            return handler.handle(request);
        } catch (final RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private static Throwable extractEffectiveCause(final Throwable wrapper) {
        if (wrapper instanceof CompletionException || wrapper instanceof ExecutionException) {
            return wrapper.getCause() == null ? wrapper : wrapper.getCause();
        }
        return wrapper;
    }

    protected <A extends Answer> CompletableFuture<A> sendAndTrack(
            final OutgoingRequest<A> request) {
        final HopByHopId hopByHop = identifiers.nextHopByHop();
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();
        return sendAndTrack(request, hopByHop, endToEnd, config.getRequestTimeout());
    }

    private <A extends Answer> CompletableFuture<A> sendAndTrack(
            final OutgoingRequest<A> request, final HopByHopId hopByHop, final EndToEndId endToEnd,
            final Duration timeout) {
        final CompletableFuture<A> future = new CompletableFuture<>();
        startTracking(future, request.getCommandCode(), request.getApplicationId(), hopByHop, timeout);
        send(request, hopByHop, endToEnd).addListener(writeResult -> {
            if (!writeResult.isSuccess()) {
                fail(hopByHop, writeResult.cause());
            }
        });
        return future;
    }

    /**
     * Registers a pending request for the given hop-by-hop identifier: schedules the
     * request timeout (unless {@code timeout} is {@link Duration#ZERO}, mirroring the
     * DWR exception from ADR-0004) and starts the request duration timer sample.
     * Shared by {@code sendAndTrack} and {@code relay}.
     */
    private <A> void startTracking(final CompletableFuture<A> future, final int commandCode,
                                   final int applicationId, final HopByHopId hopByHop,
                                   final Duration timeout) {
        final Future<?> timeoutTask;
        if (timeout.isZero()) {
            timeoutTask = NO_TIMEOUT_TASK;
        } else {
            final long timeoutMs = timeout.toMillis();
            timeoutTask = peer.eventLoop().schedule(
                    () -> timeout(hopByHop, timeoutMs),
                    timeoutMs, TimeUnit.MILLISECONDS);
        }

        final Timer.Sample timerSample = meters.startTimer();
        pendingRequests.put(hopByHop, new PendingRequest<>(future, timeoutTask, timerSample, commandCode, applicationId));
    }

    protected void cancel(final HopByHopId hopByHop) {
        fail(hopByHop, new CancellationException());
    }

    /**
     * Enters watchdog state OKAY and (re)starts the Tw timer. Idempotent: a timer that is
     * already running (e.g. a repeated CER on an open connection) is cancelled first, so
     * there is never more than one DWR chain per connection.
     */
    protected void startWatchdog() {
        stopWatchdog();
        watchdogState = WatchdogState.OKAY;
        final DiameterRequestHandler<DeviceWatchdogRequest.In, DeviceWatchdogAnswer.Out> dwrHandler =
                dwr -> CompletableFuture.completedFuture(
                        DiameterMessageFactory.createAnswer(dwr, DiameterConstants.RES_DIAMETER_SUCCESS));
        handlers.put(DeviceWatchdogRequest.In.class, dwrHandler);
        scheduleTwTimer();
    }

    protected void stopWatchdog() {
        if (twTimer != null) {
            twTimer.cancel(false);
            twTimer = null;
        }
    }

    /**
     * Resets the Tw timer on any inbound message, and cancels a pending DWR if
     * a different message proves the link alive.
     */
    protected void handleWatchdog(final IncomingCommand command) {
        resetTwTimer();
        if (pendingDwrHopByHop != null && pendingRequests.containsKey(pendingDwrHopByHop)) {
            final boolean isMatchingDwa = command instanceof final DeviceWatchdogAnswer.In dwa
                    && dwa.hopByHopId().equals(pendingDwrHopByHop);
            if (!isMatchingDwa) {
                cancel(pendingDwrHopByHop);
            }
        }
    }

    public void onDisconnected(final DiameterPeer peer) {
        if (peer == null) {
            LOGGER.info("disconnected");
        } else {
            LOGGER.info("disconnected local {} and remote {}", peer.localAddressesForLog(), peer.remoteAddress());
        }

        stopWatchdog();
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.DOWN;
        failAllPending(new IllegalStateException("Connection lost"));
    }

    /**
     * Handles a parse error that occurred while decoding an inbound message.
     *
     * <ul>
     *   <li>An unparseable <em>answer</em> (R-bit clear) is never answered: it is dropped with a
     *       warning. A pending request it belonged to runs into its timeout (a relayed one is
     *       then answered upstream by the relay caller). A {@link DiameterResultCodeException}
     *       that is not an {@link AVPParseException} still closes the connection.</li>
     *   <li>{@link AVPParseException} — sends an answer with a {@code Failed-AVP} wrapping
     *       the offending AVP. The connection remains open.</li>
     *   <li>{@link DiameterResultCodeException} — sends an answer without
     *       {@code Failed-AVP}, then closes the connection immediately.</li>
     *   <li>{@link DiameterException} (base) — the byte stream is corrupt and cannot be
     *       recovered. Closes the connection; the Tc timer will schedule a reconnect on
     *       the initiator side.</li>
     * </ul>
     *
     * <p>The answer carries the E-bit only for protocol errors (3xxx); permanent failures
     * such as 5001/5014 are regular answers (RFC 6733 §7.1), see
     * {@link DiameterMessageFactory#createParseErrorAnswer}. Both request branches build
     * their answer through the overridable {@link #parseErrorAnswer(DiameterResultCodeException)}
     * hook.</p>
     */
    public void onParseError(final DiameterPeer peer, final DiameterException cause) {
        if (cause instanceof final DiameterResultCodeException rcEx && !rcEx.isRequest()) {
            LOGGER.warn("dropping unparseable answer (command {}, application {}, hop-by-hop {}): result code {}",
                    rcEx.getCommandCode(), rcEx.getApplicationId(), rcEx.getHopByHop(), rcEx.getResultCode());
            if (!(rcEx instanceof AVPParseException)) {
                stop();
            }
        } else if (cause instanceof final AVPParseException avpEx) {
            send(peer, parseErrorAnswer(avpEx));
        } else if (cause instanceof final DiameterResultCodeException rcEx) {
            send(peer, parseErrorAnswer(rcEx));
            stop();
        } else {
            closePeer();
        }
    }

    /**
     * Builds the answer sent for a request that failed to parse (see
     * {@link #onParseError(DiameterPeer, DiameterException)}).
     * <p>
     * The default returns the RFC 6733 answer of
     * {@link DiameterMessageFactory#createParseErrorAnswer(DiameterResultCodeException)}:
     * E-bit only for protocol errors (3xxx), {@code Failed-AVP} for an
     * {@link AVPParseException}. A subclass may return a different answer for wire
     * compatibility with a specific peer; the connection handling of
     * {@code onParseError} is unaffected.
     * </p>
     *
     * @param cause the parse failure of a request ({@link DiameterResultCodeException#isRequest()});
     *              an {@link AVPParseException} for a recoverable AVP error
     * @return the answer to send, never {@code null}
     */
    protected OutgoingAnswer parseErrorAnswer(final DiameterResultCodeException cause) {
        return DiameterMessageFactory.createParseErrorAnswer(cause);
    }

    public PeerState getPeerState() {
        return peerState;
    }

    /** RFC 3539 watchdog state; only {@link WatchdogState#OKAY} peers should be routed to. */
    public WatchdogState getWatchdogState() {
        return watchdogState;
    }

    /**
     * Matches an incoming answer to a pending request by hop-by-hop identifier
     * and completes the corresponding future.
     *
     * <p>A relayed answer (one with a relay translation) is first forwarded upstream
     * with its original hop-by-hop identifier restored (RFC 6733 §6.2.2), then the
     * future returned by {@code relay} completes normally with it — E-bit answers
     * included, since forwarding them is a successful relay. For plain requests an
     * E-bit answer fails the future with {@link DiameterErrorAnswerException}
     * (ADR-0009). An answer matching neither map is logged as unsolicited.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void complete(final IncomingAnswer answer) {
        final boolean relayed = sendAnswerRestored(answer);
        final PendingRequest pending = pendingRequests.remove(answer.hopByHopId());
        if (pending == null) {
            if (!relayed) {
                LOGGER.warn("found no pending request for answer with hop-by-hop id: {}", answer.hopByHopId());
            }
            return;
        }
        pending.timeoutTask.cancel(false);
        meters.stopRequestTimer(pending.timerSample, pending.commandCode, pending.applicationId);
        if (answer instanceof final ErrorAnswer.In errorAnswer) {
            final var cause = new DiameterErrorAnswerException(errorAnswer);
            meters.recordOutgoingRequestError(pending.commandCode, pending.applicationId, cause);
            if (!relayed) {
                pending.future.completeExceptionally(cause);
                return;
            }
        }
        pending.future.complete(answer);
    }

    private void timeout(final HopByHopId hopByHop, final long timeoutMs) {
        fail(hopByHop, new TimeoutException("Request " + hopByHop.value() + " timed out after " + timeoutMs + " ms"));
    }

    private void fail(final HopByHopId hopByHop, final Throwable cause) {
        Objects.requireNonNull(cause, "cause must not be null");
        final PendingRequest<?> pending = pendingRequests.remove(hopByHop);
        if (pending == null) {
            return;
        }
        // Also drop a relay translation under the same key (no-op for plain requests):
        // every relay entry is paired with a pending entry, so request timeout, write
        // failure, and connection loss clean up both halves together. A late answer
        // after that finds neither and is logged as unsolicited.
        final RelayTranslation translation = relayTranslations.remove(hopByHop);
        meters.recordOutgoingRequestError(pending.commandCode, pending.applicationId, cause);
        pending.timeoutTask.cancel(false);
        // A relay future fails with RelayFailedException so the upstream request is
        // answered DIAMETER_UNABLE_TO_DELIVER rather than a generic handler failure.
        pending.future.completeExceptionally(
                translation == null ? cause : new RelayFailedException("relay failed", cause));
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CER.
     * Stamps the configured Origin-State-Id (RFC 6733 §8.16) when the application
     * supplied one.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeRequest.Out cer) {
        cer.setVendorId(config.getVendorId());
        cer.setProductName(config.getProductName());
        cer.addAllHostIpAddresses(config.getHostIpAddresses());
        cer.addAllSupportedVendorIds(config.getCapabilities().supportedVendorIds());
        cer.addAllAuthApplicationIds(config.getCapabilities().authApplicationIds());
        cer.addAllAcctApplicationIds(config.getCapabilities().acctApplicationIds());
        for (final DiameterNodeConfig.VendorSpecificApp app : config.getCapabilities().vendorSpecificApplications()) {
            cer.addVendorSpecificApplicationId(buildVendorSpecificAppIdAVPs(app));
        }
        stampOriginStateId(cer);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CEA.
     * Stamps the configured Origin-State-Id (RFC 6733 §8.16) when the application
     * supplied one.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeAnswer.Out cea) {
        cea.setVendorId(config.getVendorId());
        cea.setProductName(config.getProductName());
        cea.addAllHostIpAddresses(config.getHostIpAddresses());
        cea.addAllSupportedVendorIds(config.getCapabilities().supportedVendorIds());
        cea.addAllAuthApplicationIds(config.getCapabilities().authApplicationIds());
        cea.addAllAcctApplicationIds(config.getCapabilities().acctApplicationIds());
        for (final DiameterNodeConfig.VendorSpecificApp app : config.getCapabilities().vendorSpecificApplications()) {
            cea.addVendorSpecificApplicationId(buildVendorSpecificAppIdAVPs(app));
        }
        stampOriginStateId(cea);
    }

    private void stampOriginStateId(final HasOriginStateIdAVP message) {
        if (config.getOriginStateId() != 0L) {
            message.setOriginStateId(config.getOriginStateId());
        }
    }

    /**
     * Records the Origin-State-Id a peer presented in an accepted CER or a successful
     * CEA and detects peer restarts (RFC 6733 §8.6/§8.16): a strictly higher value than the last known one
     * means the peer restarted with loss of state — sessions established before the
     * restart are no longer valid. Emits a WARN log on detection.
     *
     * <p>The last known values are kept per remote Origin-Host across session
     * instances (sessions are per-connection and never reused across reconnects,
     * ADR-0004). Absent values ({@code -1} from the AVP mixin) are ignored; a lower
     * value than previously seen (a misbehaving peer, RFC 6733 §8.16 mandates
     * monotonic increase) is recorded without a restart warning. Callers record only
     * after a successful capabilities exchange, so rejected peers never enter the
     * process-wide memory.</p>
     *
     * @param remoteOriginHost    the Origin-Host the peer presented
     * @param remoteOriginStateId the Origin-State-Id the peer presented, or -1 if absent
     */
    protected static void recordRemoteOriginStateId(final String remoteOriginHost, final long remoteOriginStateId) {
        if (remoteOriginHost == null || remoteOriginStateId < 0) {
            return;
        }
        REMOTE_ORIGIN_STATE_IDS.compute(remoteOriginHost, (host, previous) -> {
            if (previous != null && remoteOriginStateId > previous) {
                LOGGER.warn(
                        "peer restart detected for {}: Origin-State-Id increased from {} to {}; "
                                + "sessions established before the restart are no longer valid",
                        host, previous, remoteOriginStateId);
            }
            return remoteOriginStateId;
        });
    }

    /**
     * Last known Origin-State-Id for a remote Origin-Host, or -1 if none was recorded
     * yet. Test seam for the process-wide restart-detection memory.
     */
    static long lastKnownRemoteOriginStateId(final String remoteOriginHost) {
        final Long last = REMOTE_ORIGIN_STATE_IDS.get(remoteOriginHost);
        return last == null ? -1L : last;
    }

    /** Test seam: clears the process-wide peer restart memory (see {@link #recordRemoteOriginStateId}). */
    static void clearPeerRestartMemoryForTests() {
        REMOTE_ORIGIN_STATE_IDS.clear();
    }

    private static List<AVP> buildVendorSpecificAppIdAVPs(final DiameterNodeConfig.VendorSpecificApp app) {
        return List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), app.vendorId()),
                AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), app.authApplicationId())
        );
    }

    private void failAllPending(final Throwable cause) {
        for (final HopByHopId hopByHop : new ArrayList<>(pendingRequests.keySet())) {
            fail(hopByHop, cause);
        }
    }

    private void scheduleTwTimer() {
        final long jitter = ThreadLocalRandom.current().nextLong(-2000, 2001);
        final long delayMs = config.getTwinit().toMillis() + jitter;
        twTimer = peer.eventLoop().schedule(this::onTwExpiry, delayMs, TimeUnit.MILLISECONDS);
    }

    private void resetTwTimer() {
        if (twTimer != null) {
            twTimer.cancel(false);
        }
        scheduleTwTimer();
    }

    private void onTwExpiry() {
        if (peerState != PeerState.I_OPEN && peerState != PeerState.R_OPEN) {
            return;
        }
        if (pendingDwrHopByHop != null && pendingRequests.containsKey(pendingDwrHopByHop)) {
            if (watchdogState == WatchdogState.OKAY) {
                watchdogState = WatchdogState.SUSPECT;
                scheduleTwTimer();
            } else if (watchdogState == WatchdogState.SUSPECT) {
                watchdogState = WatchdogState.DOWN;
                closePeer();
            }
            LOGGER.warn("Tw expired, watchdog state = {}", watchdogState);
            return;
        }
        final HopByHopId hopByHop = identifiers.nextHopByHop();
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();
        pendingDwrHopByHop = hopByHop;
        sendAndTrack(buildDwr(), hopByHop, endToEnd, Duration.ZERO).whenComplete((dwa, err) -> {
            pendingDwrHopByHop = null;
            if (err == null || err instanceof CancellationException) {
                if (watchdogState == WatchdogState.SUSPECT) {
                    watchdogState = WatchdogState.OKAY;
                }
            }
        });
        scheduleTwTimer();
    }

    private DeviceWatchdogRequest.Out buildDwr() {
        return new DeviceWatchdogRequest.Out();
    }

    private DisconnectPeerRequest.Out buildDpr(final int disconnectCause) {
        final var dpr = new DisconnectPeerRequest.Out();
        dpr.setDisconnectCause(disconnectCause);
        return dpr;
    }

    private static final class PendingRequest<A> {

        final CompletableFuture<A> future;
        final Future<?> timeoutTask;
        final Timer.Sample timerSample;
        final int commandCode;
        final int applicationId;

        PendingRequest(final CompletableFuture<A> future, final Future<?> timeoutTask,
                       final Timer.Sample timerSample, final int commandCode, final int applicationId) {
            this.future = future;
            this.timeoutTask = timeoutTask;
            this.timerSample = timerSample;
            this.commandCode = commandCode;
            this.applicationId = applicationId;
        }
    }
}
