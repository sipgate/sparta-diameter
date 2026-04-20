package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Accounting Request (ACR) message.
 * <p>
 * This interface represents the Accounting Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-9.7.1">RFC 6733, Section 9.7.1</a>.
 * The ACR message is used to send accounting information for a user session.
 * </p>
 */
public interface AccountingRequest
        extends HasSessionIdAVP, HasAccountingRecordTypeAVP, HasAccountingRecordNumberAVP,
                HasAcctApplicationIdAVP, HasVendorSpecificApplicationIdAVP, HasUserNameAVP,
                HasAccountingSubSessionIdAVP, HasAcctSessionIdAVP, HasAcctMultiSessionIdAVP,
                HasAcctInterimIntervalAVP, HasAccountingRealtimeRequiredAVP,
                HasOriginStateIdAVP, HasEventTimestampAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<AccountingAnswer.Out>
            implements AccountingRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_ACCOUNTING, true, retransmitted,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<AccountingAnswer.In>
            implements AccountingRequest {

        public Out() {
            super(DiameterConstants.CMD_ACCOUNTING, true,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING);
        }
    }
}
