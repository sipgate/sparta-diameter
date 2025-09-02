package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Accounting Answer (ACA) message.
 * <p>
 * This class represents the Accounting Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-9.7.2">RFC 6733, Section 9.7.2</a>.
 * The ACA message is used to respond to an ACR message for accounting requests.
 * </p>
 */
public final class AccountingAnswer extends Answer<AccountingAnswer> implements
        HasSessionIdAVP<AccountingAnswer>,
        HasAccountingRecordTypeAVP<AccountingAnswer>,
        HasAccountingRecordNumberAVP<AccountingAnswer>,
        HasAcctApplicationIdAVP<AccountingAnswer>,
        HasVendorSpecificApplicationIdAVP<AccountingAnswer>,
        HasUserNameAVP<AccountingAnswer>,
        HasAccountingSubSessionIdAVP<AccountingAnswer>,
        HasAcctMultiSessionIdAVP<AccountingAnswer>,
        HasErrorMessageAVP<AccountingAnswer>,
        HasErrorReportingHostAVP<AccountingAnswer>,
        HasFailedAVP<AccountingAnswer>,
        HasAcctInterimIntervalAVP<AccountingAnswer>,
        HasAccountingRealtimeRequiredAVP<AccountingAnswer>,
        HasOriginStateIdAVP<AccountingAnswer>,
        HasEventTimestampAVP<AccountingAnswer>,
        HasProxyInfoAVP<AccountingAnswer> {

    /**
     * Constructs an Accounting Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private AccountingAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_ACCOUNTING, true, false,
              DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an Accounting Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new AccountingAnswer instance.
     */
    public static AccountingAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new AccountingAnswer(hopByHopIdentifier, endToEndIdentifier);
    }
}
