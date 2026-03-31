package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Diameter session for the initiator (I-) side of a connection.
 */
public final class DiameterInitiatorSession extends DiameterSession {

    private final Runnable reconnect;
    private Future<?> tcTimer;

    public DiameterInitiatorSession(final DiameterNodeConfig config, final Runnable reconnect) {
        super(config);
        this.reconnect = reconnect;
    }

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
    public void onMessage(final DiameterPeer peer, final IncomingCommand command) {
        if (peerState == PeerState.I_OPEN) {
            handleWatchdog(command);
            if (command instanceof final DisconnectPeerRequest.In dpr) {
                handleInboundDpr(dpr);
                return;
            }
            if (command instanceof final IncomingRequest<?, ?> request) {
                dispatchInboundRequest(request);
                return;
            }
        }

        if (command instanceof final IncomingAnswer<?> answer) {
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

    private void handleCea(final CapabilitiesExchangeAnswer.In cea) {
        if (cea.getResultCode() == DiameterConstants.RES_DIAMETER_SUCCESS) {
            peerState = PeerState.I_OPEN;
            startWatchdog();
        } else {
            peerState = PeerState.CLOSED;
            peer.close();
        }
    }

    private CapabilitiesExchangeRequest.Out buildCer() {
        final CapabilitiesExchangeRequest.Out cer =
                new CapabilitiesExchangeRequest.Out();
        populateCapabilityAvps(cer);
        return cer;
    }
}
