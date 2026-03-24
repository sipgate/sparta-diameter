package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.Request;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerRequest;
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
        // We must handle a CER separately from the usual setHandler pattern,
        // because we want to process normal requests only when the
        // state of the connection is already R_OPEN.
        if (command instanceof final CapabilitiesExchangeRequest cer) {
            handleCer(cer);
            return;
        }

        // We want to handle incoming commands only when the connection is ready,
        // so rogue commands don't interfere with an application before the CEA has been sent.
        if (peerState == PeerState.R_OPEN) {
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
