package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.util.concurrent.ThreadLocalRandom;

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

    public DiameterInitiatorSession(final DiameterNodeConfig config, final Runnable reconnect) {
        super(config);
        this.reconnect = reconnect;
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
        if (peerState == PeerState.I_OPEN) {
            handleWatchdog(command);
            if (command instanceof final Request<?, ?> request) {
                dispatchInboundRequest(request);
            } else {
                tryCompleteFromPendingMap(command);
            }
        } else {
            tryCompleteFromPendingMap(command);
        }
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        super.onDisconnected(peer);
        // reconnect scheduling via Tc timer — implemented in step 9
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
        final int hopByHop = ThreadLocalRandom.current().nextInt();
        final int endToEnd = ThreadLocalRandom.current().nextInt();
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(hopByHop, endToEnd);
        populateCapabilityAvps(cer);
        return cer;
    }
}
