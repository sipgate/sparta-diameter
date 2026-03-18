package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
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

    protected PeerState peerState;
    protected WatchdogState watchdogState;
    protected DiameterPeer peer;

    private final ConcurrentHashMap<Integer, PendingRequest<?>> pendingRequests =
            new ConcurrentHashMap<>();

    DiameterSession(final DiameterNodeConfig config) {
        this.config = config;
        this.negotiator = new CapabilityNegotiator();
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
        return sendAndTrack(request);
    }

    protected <A extends Answer<A>> CompletableFuture<A> sendAndTrack(final Request<?, A> request) {
        final CompletableFuture<A> future = new CompletableFuture<>();
        final int hopByHop = request.getHopByHopIdentifier();

        final long timeoutMs = config.getRequestTimeout().toMillis();
        final Future<?> timeoutTask = peer.eventLoop().schedule(
                () -> timeout(hopByHop, timeoutMs), timeoutMs, TimeUnit.MILLISECONDS);
        pendingRequests.put(hopByHop, new PendingRequest<>(future, timeoutTask));

        peer.send(request).addListener(writeResult -> {
            if (!writeResult.isSuccess()) {
                fail(hopByHop, writeResult.cause());
            }
        });
        return future;
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
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
     * map and completes the corresponding future. Does nothing for requests or answers
     * with no pending entry.
     *
     * @param command the incoming message
     */
    protected void tryCompleteFromPendingMap(final Command<?> command) {
        if (command.isRequest()) {
            return;
        }
        complete(command.getHopByHopIdentifier(), command);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void complete(final int hopByHop, final Command<?> answer) {
        final PendingRequest pending = pendingRequests.remove(hopByHop);
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

        for (final Long vendorId : config.getCapabilities().getSupportedVendorIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, vendorId));
        }

        for (final Integer authId : config.getCapabilities().getAuthApplicationIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, Integer.toUnsignedLong(authId)));
        }

        for (final Integer acctId : config.getCapabilities().getAcctApplicationIds()) {
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

    private static final class PendingRequest<A> {

        final CompletableFuture<A> future;
        final Future<?> timeoutTask;

        PendingRequest(final CompletableFuture<A> future, final Future<?> timeoutTask) {
            this.future = future;
            this.timeoutTask = timeoutTask;
        }
    }
}
