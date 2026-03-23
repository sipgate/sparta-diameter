package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;
import java.util.List;

/**
 * Diameter session for the responder (R-) side of a connection.
 * <p>
 * Created fresh for each accepted inbound connection. Handles the RFC 6733
 * R-Open state machine and RFC 3539 watchdog. Does not reconnect — if the
 * transport drops, the instance is done; the server waits for a new inbound
 * connection.
 * </p>
 */
public final class DiameterResponderSession extends DiameterSession {

    public DiameterResponderSession(final DiameterNodeConfig config) {
        super(config);
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        this.peer = peer;
    }

    @Override
    public void onMessage(final DiameterPeer peer, final Command<?> command) {
        if (command instanceof final CapabilitiesExchangeRequest cer) {
            handleCer(cer);
        } else if (peerState == PeerState.R_OPEN) {
            handleWatchdog(command);
            if (command instanceof final Request<?, ?> request) {
                dispatchInboundRequest(request);
            } else {
                tryCompleteFromPendingMap(command);
            }
        }
    }

    private void handleCer(final CapabilitiesExchangeRequest cer) {
        final List<Long> remoteAuthIds =
                extractUnsignedInts(cer.findAVPs(DiameterConstants.AVP_AUTH_APPLICATION_ID));
        final List<Long> remoteAcctIds =
                extractUnsignedInts(cer.findAVPs(DiameterConstants.AVP_ACCT_APPLICATION_ID));

        if (negotiator.hasCommonApplication(config.getCapabilities(), remoteAuthIds, remoteAcctIds)) {
            peer.send(buildCea(cer, DiameterConstants.RES_DIAMETER_SUCCESS));
            peerState = PeerState.R_OPEN;
            startWatchdog();
        } else {
            peer.send(buildCea(cer, DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION));
            peerState = PeerState.CLOSED;
            peer.close();
        }
    }

    private CapabilitiesExchangeAnswer buildCea(final CapabilitiesExchangeRequest cer, final long resultCode) {
        final CapabilitiesExchangeAnswer cea = cer.createAnswer(resultCode);
        populateCapabilityAvps(cea);
        return cea;
    }
}