package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Session Termination Answer (STA) message.
 * <p>
 * This interface represents the Session Termination Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.4.2">RFC 6733, Section 8.4.2</a>.
 * The STA message is used to respond to an STR message for session termination requests.
 * </p>
 */
public interface SessionTerminationAnswer<T extends SessionTerminationAnswer<T>>
        extends HasSessionIdAVP<T>, HasUserNameAVP<T>, HasClassAVP<T>,
                HasErrorMessageAVP<T>, HasErrorReportingHostAVP<T>, HasFailedAVP<T>,
                HasOriginStateIdAVP<T>, HasRedirectHostAVP<T>,
                HasRedirectHostUsageAVP<T>, HasProxyInfoAVP<T> {

    final class In extends IncomingAnswer<In>
            implements SessionTerminationAnswer<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_SESSION_TERMINATION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements SessionTerminationAnswer<Out> {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_SESSION_TERMINATION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
