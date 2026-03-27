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
public interface AccountingAnswer<T extends AccountingAnswer<T>>
        extends HasSessionIdAVP<T>, HasAccountingRecordTypeAVP<T>, HasAccountingRecordNumberAVP<T>,
                HasAcctApplicationIdAVP<T>, HasVendorSpecificApplicationIdAVP<T>, HasUserNameAVP<T>,
                HasAccountingSubSessionIdAVP<T>, HasAcctMultiSessionIdAVP<T>,
                HasErrorMessageAVP<T>, HasErrorReportingHostAVP<T>, HasFailedAVP<T>,
                HasAcctInterimIntervalAVP<T>, HasAccountingRealtimeRequiredAVP<T>,
                HasOriginStateIdAVP<T>, HasEventTimestampAVP<T>, HasProxyInfoAVP<T> {

    final class In extends IncomingAnswer<In>
            implements AccountingAnswer<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ACCOUNTING, true,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements AccountingAnswer<Out> {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_ACCOUNTING, true,
                  DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHop, endToEnd);
        }
    }
}
