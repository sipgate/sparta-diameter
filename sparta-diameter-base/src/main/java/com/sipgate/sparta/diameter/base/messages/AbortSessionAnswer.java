package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Abort Session Answer (ASA) message.
 * <p>
 * This interface represents the Abort Session Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.5.2">RFC 6733, Section 8.5.2</a>.
 * The ASA message is used to respond to an ASR message for session abort requests.
 * </p>
 */
public interface AbortSessionAnswer<T extends AbortSessionAnswer<T>>
        extends HasSessionIdAVP<T>, HasUserNameAVP<T>, HasErrorMessageAVP<T>,
                HasErrorReportingHostAVP<T>, HasFailedAVP<T>, HasRedirectHostAVP<T>,
                HasRedirectHostUsageAVP<T>, HasRedirectMaxCacheTimeAVP<T>, HasProxyInfoAVP<T> {

    @DiameterResponse(DiameterConstants.CMD_ABORT_SESSION)
    final class In extends IncomingAnswer<In>
            implements AbortSessionAnswer<In> {

        private In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ABORT_SESSION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements AbortSessionAnswer<Out> {

        private Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ABORT_SESSION, true,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
