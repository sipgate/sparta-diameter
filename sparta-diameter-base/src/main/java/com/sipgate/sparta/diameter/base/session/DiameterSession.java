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
 */
public abstract class DiameterSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterSession.class);

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

    private final Map<Class<? extends IncomingCommand>, DiameterRequestHandler<?, ?>> handlers = new HashMap<>();

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
     * Handles a peer-initiated DPR: sends a DPA and closes the channel.
     */
    protected void handleInboundDpr(final DisconnectPeerRequest.In dpr) {
        stopWatchdog();
        peerState = PeerState.CLOSING;
        send(DiameterMessageFactory.createAnswer(dpr, DiameterConstants.RES_DIAMETER_SUCCESS))
            .addListener(ignored -> closePeer());
    }

    protected void closePeer() {
        LOGGER.debug("closing peer");
        peerState = PeerState.CLOSED;
        peer.close();
    }

    /**
     * Dispatches an inbound request to the registered handler, or sends
     * {@code DIAMETER_COMMAND_UNSUPPORTED} if no handler is registered.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void dispatchInboundRequest(final IncomingRequest<? extends OutgoingAnswer> request) {
        final int commandCode = request.getCommandCode();
        final int applicationId = request.getApplicationId();
        final DiameterRequestHandler handler = handlers.get(request.getClass());
        if (handler == null) {
            LOGGER.warn("request unsupported: {}", request.getCommandName());
            send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED));
            return;
        }
        final Timer.Sample handlerSample = meters.startTimer();
        final CompletableFuture<? extends OutgoingAnswer> future = handler.handle(request);
        future.whenComplete((answer, err) -> {
            meters.stopHandlerTimer(handlerSample, commandCode, applicationId);
            // Keep this if even though nobody throws DiameterErrorAnswerException inside this lib. This is a way for
            // applications to indicate a diameter business error.
            err = extractEffectiveCause(err);
            if (err instanceof final DiameterErrorAnswerException e && e.getAnswer() instanceof final ErrorAnswer.Out out) {
                send(out);
                meters.recordHandlerError(commandCode, applicationId, err);
            } else if (err != null) {
                send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
                meters.recordHandlerError(commandCode, applicationId, err);
            } else {
                send(answer);
            }
        });
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
        final int commandCode = request.getCommandCode();
        final int applicationId = request.getApplicationId();

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

        send(request, hopByHop, endToEnd).addListener(writeResult -> {
            if (!writeResult.isSuccess()) {
                fail(hopByHop, writeResult.cause());
            }
        });
        return future;
    }

    protected void cancel(final HopByHopId hopByHop) {
        fail(hopByHop, new CancellationException());
    }

    protected void startWatchdog() {
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
            LOGGER.info("disconnected local {} and remote {}", peer.localAddress(), peer.remoteAddress());
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
     *   <li>{@link AVPParseException} — sends an error answer with a
     *       {@code Failed-AVP} wrapping the offending AVP. The connection remains open.</li>
     *   <li>{@link DiameterResultCodeException} — sends an error answer without
     *       {@code Failed-AVP}, then closes the connection immediately.</li>
     *   <li>{@link DiameterException} (base) — the byte stream is corrupt and cannot be
     *       recovered. Closes the connection; the Tc timer will schedule a reconnect on
     *       the initiator side.</li>
     * </ul>
     */
    public void onParseError(final DiameterPeer peer, final DiameterException cause) {
        if (cause instanceof final AVPParseException avpEx) {
            final ErrorAnswer.Out answer = buildParseErrorAnswer(avpEx);
            answer.setFailedAVP(List.of(avpEx.getOffendingAvp()));
            send(peer, answer);
        } else if (cause instanceof final DiameterResultCodeException rcEx) {
            final ErrorAnswer.Out answer = buildParseErrorAnswer(rcEx);
            send(peer, answer);
            stop();
        } else {
            closePeer();
        }
    }

    private ErrorAnswer.Out buildParseErrorAnswer(final DiameterResultCodeException e) {
        return DiameterMessageFactory.createErrorAnswer(e);
    }

    public PeerState getPeerState() {
        return peerState;
    }

    WatchdogState getWatchdogState() {
        return watchdogState;
    }

    /**
     * Matches an incoming answer to a pending request by hop-by-hop identifier
     * and completes the corresponding future.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void complete(final IncomingAnswer answer) {

        final PendingRequest pending = pendingRequests.remove(answer.hopByHopId());
        if (pending == null) {
            LOGGER.warn("found no pending request for answer with hop-by-hop id: {}", answer.hopByHopId());
            return;
        }
        pending.timeoutTask.cancel(false);
        meters.stopRequestTimer(pending.timerSample, pending.commandCode, pending.applicationId);
        if (answer instanceof final ErrorAnswer.In errorAnswer) {
            final var cause = new DiameterErrorAnswerException(errorAnswer);
            meters.recordOutgoingRequestError(pending.commandCode, pending.applicationId, cause);
            pending.future.completeExceptionally(cause);
        } else {
            pending.future.complete(answer);
        }
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
        meters.recordOutgoingRequestError(pending.commandCode, pending.applicationId, cause);
        pending.timeoutTask.cancel(false);
        pending.future.completeExceptionally(cause);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CER.
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
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CEA.
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
