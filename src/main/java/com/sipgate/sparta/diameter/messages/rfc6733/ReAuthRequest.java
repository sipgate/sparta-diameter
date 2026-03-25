package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Re-Auth Request (RAR) message.
 * <p>
 * This interface represents the Re-Auth Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.3.1">RFC 6733, Section 8.3.1</a>.
 * The RAR message is used to request re-authentication of a user session.
 * </p>
 */
public interface ReAuthRequest<T extends ReAuthRequest<T>>
        extends HasAuthApplicationIdAVP<T>, HasReAuthRequestTypeAVP<T>, HasUserNameAVP<T>,
                HasOriginStateIdAVP<T>, HasProxyInfoAVP<T>, HasRouteRecordAVP<T> {

    @DiameterRequest(DiameterConstants.CMD_RE_AUTH)
    final class In extends IncomingRequest<In, ReAuthAnswer.Out>
            implements ReAuthRequest<In> {

        private In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_RE_AUTH, true, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<Out, ReAuthAnswer.In>
            implements ReAuthRequest<Out> {

        public Out() {
            super(DiameterConstants.CMD_RE_AUTH, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }
    }
}
