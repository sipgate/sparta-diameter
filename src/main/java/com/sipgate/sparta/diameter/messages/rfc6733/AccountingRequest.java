package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Accounting Request (ACR) message.
 * <p>
 * This class represents the Accounting Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-9.7.1">RFC 6733, Section 9.7.1</a>.
 * The ACR message is used to send accounting information for a user session.
 * </p>
 */
@DiameterRequest(DiameterConstants.CMD_ACCOUNTING)
public final class AccountingRequest extends Request<AccountingRequest, AccountingAnswer> implements
        HasSessionIdAVP<AccountingRequest>,
        HasAccountingRecordTypeAVP<AccountingRequest>,
        HasAccountingRecordNumberAVP<AccountingRequest>,
        HasAcctApplicationIdAVP<AccountingRequest>,
        HasVendorSpecificApplicationIdAVP<AccountingRequest>,
        HasUserNameAVP<AccountingRequest>,
        HasAccountingSubSessionIdAVP<AccountingRequest>,
        HasAcctSessionIdAVP<AccountingRequest>,
        HasAcctMultiSessionIdAVP<AccountingRequest>,
        HasAcctInterimIntervalAVP<AccountingRequest>,
        HasAccountingRealtimeRequiredAVP<AccountingRequest>,
        HasOriginStateIdAVP<AccountingRequest>,
        HasEventTimestampAVP<AccountingRequest>,
        HasProxyInfoAVP<AccountingRequest>,
        HasRouteRecordAVP<AccountingRequest>
{

    /**
     * Constructs an Accounting Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private AccountingRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_ACCOUNTING, true, retransmitted,
              DiameterConstants.APP_DIAMETER_BASE_ACCOUNTING, hopByHopIdentifier, endToEndIdentifier);
    }
}
