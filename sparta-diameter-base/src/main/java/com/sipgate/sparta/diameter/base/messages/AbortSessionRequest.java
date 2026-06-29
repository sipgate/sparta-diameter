package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Abort Session Request (ASR) message.
 * <p>
 * This interface represents the Abort Session Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.5.1">RFC 6733, Section 8.5.1</a>.
 * The ASR message is used to request immediate termination of a user session.
 * </p>
 */
public interface AbortSessionRequest
        extends DiameterRequest,
                HasSessionIdAVP, HasAuthApplicationIdAVP, HasUserNameAVP,
                HasOriginStateIdAVP {

    final class In extends IncomingRequest<AbortSessionAnswer.Out>
            implements AbortSessionRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_ABORT_SESSION, true, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Abort-Session Request";
        }
    }

    final class Out extends OutgoingRequest<AbortSessionAnswer.In>
            implements AbortSessionRequest {

        public Out() {
            super(DiameterConstants.CMD_ABORT_SESSION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }

        @Override
        public String getCommandName() {
            return "Abort-Session Request";
        }
    }
}
