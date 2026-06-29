package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDisconnectCauseAVP;

/**
 * Disconnect Peer Request (DPR) message.
 * <p>
 * This interface represents the Disconnect Peer Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.4.1">RFC 6733, Section 5.4.1</a>.
 * The DPR message is used to gracefully disconnect a Diameter peer connection.
 * </p>
 */
public interface DisconnectPeerRequest
        extends HasDisconnectCauseAVP {

    final class In extends IncomingRequest<DisconnectPeerAnswer.Out>
            implements DisconnectPeerRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Disconnect-Peer Request";
        }
    }

    final class Out extends OutgoingRequest<DisconnectPeerAnswer.In>
            implements DisconnectPeerRequest {

        public Out() {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }

        @Override
        public String getCommandName() {
            return "Disconnect-Peer Request";
        }
    }
}
