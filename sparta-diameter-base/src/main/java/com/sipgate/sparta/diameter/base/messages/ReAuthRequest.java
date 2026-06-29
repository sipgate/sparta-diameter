package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Re-Auth Request (RAR) message.
 * <p>
 * This interface represents the Re-Auth Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.3.1">RFC 6733, Section 8.3.1</a>.
 * The RAR message is used to request re-authentication of a user session.
 * </p>
 */
public interface ReAuthRequest
        extends DiameterRequest,
                HasSessionIdAVP, HasAuthApplicationIdAVP, HasReAuthRequestTypeAVP,
                HasUserNameAVP, HasOriginStateIdAVP {

    final class In extends IncomingRequest<ReAuthAnswer.Out>
            implements ReAuthRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_RE_AUTH, true, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Re-Auth Request";
        }
    }

    final class Out extends OutgoingRequest<ReAuthAnswer.In>
            implements ReAuthRequest {

        public Out() {
            super(DiameterConstants.CMD_RE_AUTH, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }

        @Override
        public String getCommandName() {
            return "Re-Auth Request";
        }
    }
}
