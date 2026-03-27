package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Re-Auth Answer (RAA) message.
 * <p>
 * This interface represents the Re-Auth Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.3.2">RFC 6733, Section 8.3.2</a>.
 * The RAA message is used to respond to a RAR message for re-authentication requests.
 * </p>
 */
public interface ReAuthAnswer<T extends ReAuthAnswer<T>>
        extends HasUserNameAVP<T>, HasOriginStateIdAVP<T>, HasErrorMessageAVP<T>,
                HasErrorReportingHostAVP<T>, HasRedirectHostAVP<T>,
                HasRedirectHostUsageAVP<T>, HasRedirectMaxCacheTimeAVP<T>, HasProxyInfoAVP<T> {

    final class In extends IncomingAnswer<In>
            implements ReAuthAnswer<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_RE_AUTH, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements ReAuthAnswer<Out> {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_RE_AUTH, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
