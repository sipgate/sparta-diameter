package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasFailedAVP;

/**
 * Disconnect Peer Answer (DPA) message.
 * <p>
 * This interface represents the Disconnect Peer Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.4.2">RFC 6733, Section 5.4.2</a>.
 * The DPA message is used to respond to a DPR message when gracefully disconnecting a Diameter peer connection.
 * </p>
 */
public interface DisconnectPeerAnswer<T extends DisconnectPeerAnswer<T>>
        extends HasErrorMessageAVP<T>, HasFailedAVP<T> {

    @DiameterResponse(DiameterConstants.CMD_DISCONNECT_PEER)
    final class In extends IncomingAnswer<In>
            implements DisconnectPeerAnswer<In> {

        private In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements DisconnectPeerAnswer<Out> {

        private Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DISCONNECT_PEER, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
