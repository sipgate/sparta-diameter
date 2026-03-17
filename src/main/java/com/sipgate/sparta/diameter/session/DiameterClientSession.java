package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

/**
 * Diameter session for the initiator (I-) side of a connection.
 * <p>
 * Created fresh for each connection attempt. Handles the RFC 6733 I-Open
 * state machine and RFC 3539 watchdog. Calls the provided {@code reconnect}
 * runnable when the transport drops unexpectedly, which creates a new instance
 * for the next attempt.
 * </p>
 */
public final class DiameterClientSession implements DiameterConnectionListener {

    private final DiameterNodeConfig config;
    private final Runnable reconnect;

    private PeerState peerState;
    private WatchdogState watchdogState;
    private DiameterPeer peer;

    public DiameterClientSession(final DiameterNodeConfig config, final Runnable reconnect) {
        this.config = config;
        this.reconnect = reconnect;
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.INITIAL;
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        this.peer = peer;
        // CER/CEA exchange drives the state transition — implemented in step 3
    }

    @Override
    public void onMessage(final DiameterPeer peer, final Command<?> command) {
        // routing and base-protocol handling — implemented in steps 3–5
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.DOWN;
        // reconnect scheduling via Tc timer — implemented in step 9
    }

    PeerState getPeerState() {
        return peerState;
    }

    WatchdogState getWatchdogState() {
        return watchdogState;
    }
}
