package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Diameter session for the initiator (I-) side of a connection.
 * <p>
 * Created fresh for each connection attempt. Handles the RFC 6733 I-Open
 * state machine and RFC 3539 watchdog. Calls the provided {@code reconnect}
 * runnable when the transport drops unexpectedly, which creates a new instance
 * for the next attempt.
 * </p>
 */
public final class DiameterInitiatorSession extends DiameterSession {

    private final Runnable reconnect;
    private Future<?> tcTimer;

    public DiameterInitiatorSession(final DiameterNodeConfig config, final Runnable reconnect) {
        super(config);
        this.reconnect = reconnect;
    }

    /**
     * Stops the session gracefully. Also cancels any pending Tc reconnect timer,
     * regardless of the current peer state.
     */
    @Override
    public void stop() {
        shuttingDown = true;
        stopTcTimer();
        super.stop();
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        this.peer = peer;
        this.peerState = PeerState.WAIT_I_CEA;
        sendAndTrack(buildCer()).whenComplete((cea, err) -> {
            if (err == null) {
                handleCea(cea);
                return;
            }

            if (peerState == PeerState.WAIT_I_CEA) {
                peerState = PeerState.CLOSED;
                peer.close();
            }
        });
    }

    @Override
    public void onMessage(final DiameterPeer peer, final Command<?> command) {
        // We want to handle incoming commands only when the connection is ready,
        // so rogue commands don't interfere with an application before the CER has been received.
        if (peerState == PeerState.I_OPEN) {
            handleWatchdog(command);
            if (command instanceof final DisconnectPeerRequest dpr) {
                handleInboundDpr(dpr);
                return;
            }
            if (command instanceof final Request<?, ?> request) {
                dispatchInboundRequest(request);
                return;
            }
        }

        if (command instanceof final Answer<?> answer) {
            complete(answer);
        }
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        super.onDisconnected(peer);
        if (!shuttingDown && this.peer != null) {
            final long tcMs = config.getTc().toMillis();
            tcTimer = this.peer.eventLoop().schedule(reconnect, tcMs, TimeUnit.MILLISECONDS);
        }
    }

    private void stopTcTimer() {
        if (tcTimer != null) {
            tcTimer.cancel(false);
            tcTimer = null;
        }
    }

    private void handleCea(final CapabilitiesExchangeAnswer cea) {
        if (cea.getResultCode() == DiameterConstants.RES_DIAMETER_SUCCESS) {
            peerState = PeerState.I_OPEN;
            startWatchdog();
        } else {
            peerState = PeerState.CLOSED;
            peer.close();
        }
    }

    private CapabilitiesExchangeRequest buildCer() {
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(
                identifiers.nextHopByHop(),
                DiameterIdentifiers.nextEndToEnd());
        populateCapabilityAvps(cer);
        return cer;
    }
}
