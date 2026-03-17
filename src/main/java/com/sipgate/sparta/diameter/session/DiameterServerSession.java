package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

/**
 * Diameter session for the responder (R-) side of a connection.
 * <p>
 * Created fresh for each accepted inbound connection. Handles the RFC 6733
 * R-Open state machine and RFC 3539 watchdog. Does not reconnect — if the
 * transport drops, the instance is done; the server waits for a new inbound
 * connection.
 * </p>
 */
public final class DiameterServerSession implements DiameterConnectionListener {

    private final DiameterNodeConfig config;

    private PeerState peerState;
    private WatchdogState watchdogState;
    private DiameterPeer peer;

    public DiameterServerSession(final DiameterNodeConfig config) {
        this.config = config;
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
    }

    PeerState getPeerState() {
        return peerState;
    }

    WatchdogState getWatchdogState() {
        return watchdogState;
    }
}
