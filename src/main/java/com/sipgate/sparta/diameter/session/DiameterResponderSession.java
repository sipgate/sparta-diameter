package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.core.IncomingAnswer;
import com.sipgate.sparta.diameter.core.IncomingCommand;
import com.sipgate.sparta.diameter.core.IncomingRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.util.List;

/**
 * Diameter session for the responder (R-) side of a connection.
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
    public void onMessage(final DiameterPeer peer, final IncomingCommand command) {
        if (command instanceof final CapabilitiesExchangeRequest.In cer) {
            handleCer(cer);
            return;
        }

        if (peerState == PeerState.R_OPEN) {
            handleWatchdog(command);
            if (command instanceof final DisconnectPeerRequest.In dpr) {
                handleInboundDpr(dpr);
                return;
            }
            if (command instanceof final IncomingRequest request) {
                dispatchInboundRequest(request);
                return;
            }
        }

        if (command instanceof final IncomingAnswer answer) {
            complete(answer);
        }
    }

    private void handleCer(final CapabilitiesExchangeRequest.In cer) {
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

    private CapabilitiesExchangeAnswer.Out buildCea(final CapabilitiesExchangeRequest.In cer,
                                                    final long resultCode) {
        final CapabilitiesExchangeAnswer.Out cea = DiameterMessageFactory.createAnswer(cer, resultCode);
        populateCapabilityAvps(cea);
        return cea;
    }
}
