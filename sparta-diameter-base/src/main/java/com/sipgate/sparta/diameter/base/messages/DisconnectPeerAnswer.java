package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Disconnect Peer Answer (DPA) message.
 * <p>
 * This interface represents the Disconnect Peer Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.4.2">RFC 6733, Section 5.4.2</a>.
 * The DPA message is used to respond to a DPR message when gracefully disconnecting a Diameter peer connection.
 * </p>
 */
public interface DisconnectPeerAnswer
        extends HasErrorMessageAVP, HasFailedAVP {

    final class In extends IncomingAnswer
            implements DisconnectPeerAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Disconnect-Peer Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements DisconnectPeerAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Disconnect-Peer Answer";
        }
    }
}
