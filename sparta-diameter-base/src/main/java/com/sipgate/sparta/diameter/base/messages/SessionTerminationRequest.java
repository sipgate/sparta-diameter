package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.annotations.DiameterRequest;

/**
 * Session Termination Request (STR) message.
 * <p>
 * This interface represents the Session Termination Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.4.1">RFC 6733, Section 8.4.1</a>.
 * The STR message is used to request termination of a user session.
 * </p>
 */
public interface SessionTerminationRequest<T extends SessionTerminationRequest<T>> {

    @DiameterRequest(DiameterConstants.CMD_SESSION_TERMINATION)
    final class In extends IncomingRequest<In, SessionTerminationAnswer.Out>
            implements SessionTerminationRequest<In> {

        private In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_SESSION_TERMINATION, true, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<Out, SessionTerminationAnswer.In>
            implements SessionTerminationRequest<Out> {

        public Out() {
            super(DiameterConstants.CMD_SESSION_TERMINATION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }
    }
}
