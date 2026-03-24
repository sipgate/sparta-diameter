package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Shared state and helpers for initiator- and responder-side Diameter sessions.
 * <p>
 * Holds the fields common to both sides (config, peer, peer state, watchdog state,
 * capability negotiator) and provides the public {@link #send} API backed by a
 * hop-by-hop-keyed pending-request map. Concrete subclasses implement the
 * side-specific connection lifecycle.
 * </p>
 */
abstract class DiameterSession implements DiameterConnectionListener {

    protected final DiameterNodeConfig config;
    protected final CapabilityNegotiator negotiator;
    protected final DiameterIdentifiers identifiers;

    protected PeerState peerState;
    protected WatchdogState watchdogState;
    protected DiameterPeer peer;

    protected boolean shuttingDown = false;

    private Integer pendingDwrHopByHop;
    private Future<?> twTimer;

    private static final Future<Void> NO_TIMEOUT_TASK = CompletableFuture.completedFuture(null);

    private final ConcurrentHashMap<Integer, PendingRequest<?>> pendingRequests =
            new ConcurrentHashMap<>();

    private final Map<Integer, DiameterRequestHandler<?, ?>> handlers = new HashMap<>();

    DiameterSession(final DiameterNodeConfig config) {
        this.config = config;
        this.negotiator = new CapabilityNegotiator();
        this.identifiers = new DiameterIdentifiers();
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.INITIAL;
    }

    /**
     * Sends a Diameter request and returns a future that completes when the
     * matching answer arrives (correlated by hop-by-hop identifier).
     *
     * <p>Returns an already-failed future if the session is not in an OPEN state.
     * Fails the future if the underlying channel write fails.
     *
     * @param request the request to send
     * @param <A>     the answer type
     * @return a future completed with the answer, or completed exceptionally on error
     */
    public <A extends Answer<A>> CompletableFuture<A> send(final Request<?, A> request) {
        if (peerState != PeerState.I_OPEN && peerState != PeerState.R_OPEN) {
            final CompletableFuture<A> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("Cannot send in state: " + peerState));
            return failed;
        }
        return sendAndTrack(request, config.getRequestTimeout());
    }

    /**
     * Registers a handler for inbound requests of the given type.
     *
     * <p>The command code is read from the {@link DiameterRequest} annotation on
     * {@code requestClass}. A second registration for the same command code throws
     * {@link IllegalStateException}.
     *
     * @param requestClass the annotated request class; must carry {@code @DiameterRequest}
     * @param handler      the handler to invoke when a matching request arrives
     * @param <R>          the request type
     * @param <A>          the answer type
     * @throws IllegalArgumentException if {@code requestClass} lacks the annotation
     * @throws IllegalStateException    if a handler for that command code is already registered
     */
    public <R extends Request<R, A>, A extends Answer<A>> void setHandler(
            final Class<R> requestClass,
            final DiameterRequestHandler<R, A> handler) {
        final DiameterRequest annotation = requestClass.getAnnotation(DiameterRequest.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    requestClass.getName() + " is not annotated with @DiameterRequest");
        }
        final int commandCode = annotation.value();
        if (handlers.containsKey(commandCode)) {
            throw new IllegalStateException(
                    "Handler for command code " + commandCode + " is already registered");
        }
        handlers.put(commandCode, handler);
    }

    /**
     * Initiates a graceful disconnection by sending a DPR with
     * {@code DO_NOT_WANT_TO_TALK_TO_YOU} and transitioning to {@code CLOSING}.
     * The connection is closed once the DPA is received (or the request times out).
     * Has no effect if the session is not in an OPEN state.
     */
    public void stop() {
        if (peerState != PeerState.I_OPEN && peerState != PeerState.R_OPEN) {
            return;
        }
        shuttingDown = true;
        stopWatchdog();
        peerState = PeerState.CLOSING;
        sendAndTrack(buildDpr()).whenComplete((dpa, err) -> {
            peerState = PeerState.CLOSED;
            peer.close();
        });
    }

    /**
     * Handles a peer-initiated DPR: sends a DPA, transitions to {@code CLOSING},
     * and closes the channel. Invisible to the application.
     */
    protected void handleInboundDpr(final DisconnectPeerRequest dpr) {
        stopWatchdog();
        peerState = PeerState.CLOSING;
        peer.send(dpr.createAnswer(DiameterConstants.RES_DIAMETER_SUCCESS));
        peer.close();
    }

    /**
     * Dispatches an inbound request to the registered handler, or sends
     * {@code DIAMETER_COMMAND_UNSUPPORTED} if no handler is registered.
     * When the handler future completes exceptionally, sends
     * {@code DIAMETER_UNABLE_TO_COMPLY}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void dispatchInboundRequest(final Request<?, ?> request) {
        final DiameterRequestHandler handler = handlers.get(request.getCommandCode());
        if (handler == null) {
            peer.send(request.createAnswer(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED));
            return;
        }
        final CompletableFuture<?> future = handler.handle(request);
        future.whenComplete((answer, err) -> {
            if (err != null) {
                peer.send(request.createAnswer(DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
            } else {
                peer.send((Command<?>) answer);
            }
        });
    }

    protected <A extends Answer<A>> CompletableFuture<A> sendAndTrack(
            final Request<?, A> request, final Duration timeout) {
        final CompletableFuture<A> future = new CompletableFuture<>();
        final int hopByHop = request.getHopByHopIdentifier();

        final Future<?> timeoutTask;
        if (timeout.isZero()) {
            timeoutTask = NO_TIMEOUT_TASK;
        } else {
            final long timeoutMs = timeout.toMillis();
            timeoutTask = peer.eventLoop().schedule(
                    () -> timeout(hopByHop, timeoutMs), timeoutMs, TimeUnit.MILLISECONDS);
        }
        pendingRequests.put(hopByHop, new PendingRequest<>(future, timeoutTask));

        peer.send(request).addListener(writeResult -> {
            if (!writeResult.isSuccess()) {
                fail(hopByHop, writeResult.cause());
            }
        });
        return future;
    }

    protected <A extends Answer<A>> CompletableFuture<A> sendAndTrack(final Request<?, A> request) {
        return sendAndTrack(request, config.getRequestTimeout());
    }

    protected void cancel(final int hopByHop) {
        fail(hopByHop, new CancellationException());
    }

    protected void startWatchdog() {
        watchdogState = WatchdogState.OKAY;
        // Register the DWR handler internally. This also prevents application code
        // from registering a competing handler via setHandler(), which would throw
        // IllegalStateException due to the existing duplicate-check.
        final DiameterRequestHandler<DeviceWatchdogRequest, DeviceWatchdogAnswer> dwrHandler =
                dwr -> CompletableFuture.completedFuture(
                        dwr.createAnswer(DiameterConstants.RES_DIAMETER_SUCCESS));
        handlers.put(DiameterConstants.CMD_DEVICE_WATCHDOG, dwrHandler);
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
     * the message arriving is not its matching DWA (proving the link alive via a
     * different message).
     *
     * @param command the inbound command
     */
    protected void handleWatchdog(final Command<?> command) {
        resetTwTimer();
        if (pendingDwrHopByHop != null && pendingRequests.containsKey(pendingDwrHopByHop)) {
            final boolean isMatchingDwa = command instanceof DeviceWatchdogAnswer
                    && command.getHopByHopIdentifier() == pendingDwrHopByHop;
            if (!isMatchingDwa) {
                cancel(pendingDwrHopByHop);
            }
        }
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        stopWatchdog();
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.DOWN;
        failAllPending(new IllegalStateException("Connection lost"));
    }

    PeerState getPeerState() {
        return peerState;
    }

    WatchdogState getWatchdogState() {
        return watchdogState;
    }

    /**
     * Looks up the hop-by-hop identifier of an incoming answer in the pending-request
     * map and completes the corresponding future. Does nothing for answers
     * with no pending entry.
     *
     * @param answer the incoming answer
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void complete(final Answer<?> answer) {
        final PendingRequest pending = pendingRequests.remove(answer.getHopByHopIdentifier());
        if (pending == null) {
            return;
        }
        pending.timeoutTask.cancel(false);
        pending.future.complete(answer);
    }

    private void timeout(final int hopByHop, final long timeoutMs) {
        fail(hopByHop, new TimeoutException("Request " + hopByHop + " timed out after " + timeoutMs + " ms"));
    }

    private void fail(final int hopByHop, final Throwable cause) {
        final PendingRequest<?> pending = pendingRequests.remove(hopByHop);
        if (pending == null) {
            return;
        }
        pending.timeoutTask.cancel(false);
        pending.future.completeExceptionally(cause);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CER
     * from the local node config.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeRequest cer) {
        cer.setOriginHost(config.getOriginHost());
        cer.setOriginRealm(config.getOriginRealm());
        cer.setVendorId(config.getVendorId());
        cer.setProductName(config.getProductName());
        addMultiValueAvps(cer);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CEA
     * from the local node config.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeAnswer cea) {
        cea.setOriginHost(config.getOriginHost());
        cea.setOriginRealm(config.getOriginRealm());
        cea.setVendorId(config.getVendorId());
        cea.setProductName(config.getProductName());
        addMultiValueAvps(cea);
    }

    private void addMultiValueAvps(final AVPContainer<?> msg) {
        for (final InetAddress addr : config.getHostIpAddresses()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, addr));
        }

        for (final Long vendorId : config.getCapabilities().supportedVendorIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, vendorId));
        }

        for (final Integer authId : config.getCapabilities().authApplicationIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, Integer.toUnsignedLong(authId)));
        }

        for (final Integer acctId : config.getCapabilities().acctApplicationIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_ACCT_APPLICATION_ID, Integer.toUnsignedLong(acctId)));
        }
    }

    /**
     * Reads all AVPs with the given code and returns their values as unsigned 32-bit integers.
     */
    protected static List<Long> extractUnsignedInts(final List<AVP> avps) {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : avps) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    private void failAllPending(final Throwable cause) {
        for (final Integer hopByHop : new ArrayList<>(pendingRequests.keySet())) {
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
                peer.close();
            }
            return;
        }
        final DeviceWatchdogRequest dwr = buildDwr();
        pendingDwrHopByHop = dwr.getHopByHopIdentifier();
        sendAndTrack(dwr, Duration.ZERO).whenComplete((dwa, err) -> {
            pendingDwrHopByHop = null;
            if (err == null || err instanceof CancellationException) {
                if (watchdogState == WatchdogState.SUSPECT) {
                    watchdogState = WatchdogState.OKAY;
                }
            }
        });
        scheduleTwTimer();
    }

    private DeviceWatchdogRequest buildDwr() {
        return DeviceWatchdogRequest.create(
                identifiers.nextHopByHop(),
                DiameterIdentifiers.nextEndToEnd());
    }

    private DisconnectPeerRequest buildDpr() {
        return DisconnectPeerRequest.create(
                        identifiers.nextHopByHop(),
                        DiameterIdentifiers.nextEndToEnd())
                .setDisconnectCause(DiameterConstants.DCC_DO_NOT_WANT_TO_TALK_TO_YOU);
    }

    private static final class PendingRequest<A> {

        final CompletableFuture<A> future;
        final Future<?> timeoutTask;

        PendingRequest(final CompletableFuture<A> future, final Future<?> timeoutTask) {
            this.future = future;
            this.timeoutTask = timeoutTask;
        }
    }
}
