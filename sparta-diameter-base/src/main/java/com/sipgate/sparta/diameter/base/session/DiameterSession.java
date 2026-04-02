package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.Answer;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
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
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

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
 */
abstract class DiameterSession implements DiameterConnectionListener {

    protected final DiameterNodeConfig config;
    protected final CapabilityNegotiator negotiator;
    protected final DiameterIdentifiers identifiers;
    protected final DiameterSessionMeters meters;

    protected PeerState peerState;
    protected WatchdogState watchdogState;
    protected DiameterPeer peer;

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
    public <R extends OutgoingRequest<R, A>, A extends Answer<A>> CompletableFuture<A> send(
            final R request) {
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

    /**
     * Registers a handler for inbound requests of the given type.
     */
    public <R extends IncomingRequest<R, A>, A extends OutgoingAnswer<A>> void setHandler(
            final Class<R> requestClass,
            final DiameterRequestHandler<R, A> handler) {
        if (handlers.containsKey(requestClass)) {
            throw new IllegalStateException(
                    "Handler for " + requestClass.getSimpleName() + " is already registered");
        }
        handlers.put(requestClass, handler);
    }

    /**
     * Initiates a graceful disconnection by sending a DPR.
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
     * Handles a peer-initiated DPR: sends a DPA and closes the channel.
     */
    protected void handleInboundDpr(final DisconnectPeerRequest.In dpr) {
        stopWatchdog();
        peerState = PeerState.CLOSING;
        peer.send(DiameterMessageFactory.createAnswer(dpr, DiameterConstants.RES_DIAMETER_SUCCESS));
        peer.close();
    }

    /**
     * Dispatches an inbound request to the registered handler, or sends
     * {@code DIAMETER_COMMAND_UNSUPPORTED} if no handler is registered.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void dispatchInboundRequest(final IncomingRequest<?, ? extends OutgoingAnswer> request) {
        final int commandCode = request.getCommandCode();
        final int applicationId = request.getApplicationId();
        final DiameterRequestHandler handler = handlers.get(request.getClass());
        if (handler == null) {
            peer.send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED));
            return;
        }
        final Timer.Sample handlerSample = meters.startTimer();
        final CompletableFuture<? extends OutgoingAnswer> future = handler.handle(request);
        future.whenComplete((answer, err) -> {
            meters.stopHandlerTimer(handlerSample, commandCode, applicationId);
            // Keep this if even though nobody throws DiameterErrorAnswerException inside this lib. This is a way for
            // applications to indicate a diameter business error.
            if (err instanceof final DiameterErrorAnswerException e && e.getAnswer() instanceof final ErrorAnswer.Out out) {
                peer.send(out);
                meters.recordError(commandCode, applicationId, DiameterSessionMeters.ERROR_TYPE_HANDLER_ERROR_ANSWER);
            } else if (err != null) {
                peer.send(DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
                meters.recordError(commandCode, applicationId, DiameterSessionMeters.ERROR_TYPE_HANDLER_EXCEPTION);
            } else {
                peer.send(answer);
            }
        });
    }

    protected <R extends OutgoingRequest<R, A>, A extends Answer<A>> CompletableFuture<A> sendAndTrack(
            final R request) {
        final HopByHopId hopByHop = identifiers.nextHopByHop();
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();
        return sendAndTrack(request, hopByHop, endToEnd, config.getRequestTimeout());
    }

    private <R extends OutgoingRequest<R, A>, A extends Answer<A>> CompletableFuture<A> sendAndTrack(
            final R request, final HopByHopId hopByHop, final EndToEndId endToEnd,
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
                    () -> timeout(hopByHop, timeoutMs, commandCode, applicationId),
                    timeoutMs, TimeUnit.MILLISECONDS);
        }

        final Timer.Sample timerSample = meters.startTimer();
        pendingRequests.put(hopByHop, new PendingRequest<>(future, timeoutTask, timerSample, commandCode, applicationId));

        peer.send(request, hopByHop, endToEnd).addListener(writeResult -> {
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
     * Matches an incoming answer to a pending request by hop-by-hop identifier
     * and completes the corresponding future.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void complete(final IncomingAnswer answer) {
        final PendingRequest pending = pendingRequests.remove(answer.hopByHopId());
        if (pending == null) {
            return;
        }
        pending.timeoutTask.cancel(false);
        meters.stopRequestTimer(pending.timerSample, pending.commandCode, pending.applicationId);
        if (answer instanceof final ErrorAnswer.In errorAnswer) {
            meters.recordError(pending.commandCode, pending.applicationId, DiameterSessionMeters.ERROR_TYPE_ERROR_ANSWER);
            pending.future.completeExceptionally(new DiameterErrorAnswerException(errorAnswer));
        } else {
            pending.future.complete(answer);
        }
    }

    private void timeout(final HopByHopId hopByHop, final long timeoutMs,
                         final int commandCode, final int applicationId) {
        meters.recordError(commandCode, applicationId, DiameterSessionMeters.ERROR_TYPE_TIMEOUT);
        fail(hopByHop, new TimeoutException(
                "Request " + hopByHop.value() + " timed out after " + timeoutMs + " ms"));
    }

    private void fail(final HopByHopId hopByHop, final Throwable cause) {
        final PendingRequest<?> pending = pendingRequests.remove(hopByHop);
        if (pending == null) {
            return;
        }
        pending.timeoutTask.cancel(false);
        pending.future.completeExceptionally(cause);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CER.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeRequest.Out cer) {
        cer.setOriginHost(config.getOriginHost());
        cer.setOriginRealm(config.getOriginRealm());
        cer.setVendorId(config.getVendorId());
        cer.setProductName(config.getProductName());
        cer.addAllHostIpAddresses(config.getHostIpAddresses());
        cer.addAllSupportedVendorIds(config.getCapabilities().supportedVendorIds());
        cer.addAllAuthApplicationIds(config.getCapabilities().authApplicationIds());
        cer.addAllAcctApplicationIds(config.getCapabilities().acctApplicationIds());
        for (final DiameterNodeConfig.VendorSpecificApp app : config.getCapabilities().vendorSpecificApplications()) {
            cer.addVendorSpecificApplicationId(buildVendorSpecificAppIdAVP(app));
        }
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CEA.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeAnswer.Out cea) {
        cea.setOriginHost(config.getOriginHost());
        cea.setOriginRealm(config.getOriginRealm());
        cea.setVendorId(config.getVendorId());
        cea.setProductName(config.getProductName());
        cea.addAllHostIpAddresses(config.getHostIpAddresses());
        cea.addAllSupportedVendorIds(config.getCapabilities().supportedVendorIds());
        cea.addAllAuthApplicationIds(config.getCapabilities().authApplicationIds());
        cea.addAllAcctApplicationIds(config.getCapabilities().acctApplicationIds());
        for (final DiameterNodeConfig.VendorSpecificApp app : config.getCapabilities().vendorSpecificApplications()) {
            cea.addVendorSpecificApplicationId(buildVendorSpecificAppIdAVP(app));
        }
    }

    private static GroupedAVP buildVendorSpecificAppIdAVP(final DiameterNodeConfig.VendorSpecificApp app) {
        return (GroupedAVP) AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0), List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), app.vendorId()),
                AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), app.authApplicationId())
        ));
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
                peer.close();
            }
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

    private DisconnectPeerRequest.Out buildDpr() {
        return new DisconnectPeerRequest.Out()
                .setDisconnectCause(DiameterConstants.DCC_DO_NOT_WANT_TO_TALK_TO_YOU);
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
