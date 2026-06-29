package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Accounting Answer (ACA) message.
 * <p>
 * This interface represents the Accounting Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-9.7.2">RFC 6733, Section 9.7.2</a>.
 * The ACA message is used to respond to an ACR message for accounting requests.
 * </p>
 */
public interface AccountingAnswer
        extends DiameterAnswer,
                HasSessionIdAVP, HasAccountingRecordTypeAVP, HasAccountingRecordNumberAVP,
                HasAcctApplicationIdAVP, HasVendorSpecificApplicationIdAVP, HasUserNameAVP,
                HasAccountingSubSessionIdAVP, HasAcctSessionIdAVP, HasAcctMultiSessionIdAVP,
                HasErrorReportingHostAVP,
                HasAcctInterimIntervalAVP, HasAccountingRealtimeRequiredAVP,
                HasOriginStateIdAVP, HasEventTimestampAVP {

    final class In extends IncomingAnswer
            implements AccountingAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ACCOUNTING, true,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Accounting Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements AccountingAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ACCOUNTING, true,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Accounting Answer";
        }
    }
}
