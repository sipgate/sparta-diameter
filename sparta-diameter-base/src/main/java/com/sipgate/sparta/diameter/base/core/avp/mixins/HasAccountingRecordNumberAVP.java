package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Record-Number AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Record-Number AVP
 * as defined in RFC 6733. The Accounting-Record-Number AVP identifies this record within one session.
 * </p>
 */
public interface HasAccountingRecordNumberAVP extends AVPContainer {

    /**
     * Sets the Accounting-Record-Number AVP.
     *
     * @param accountingRecordNumber The accounting record number to set.
     */
    default void setAccountingRecordNumber(final long accountingRecordNumber) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER, 0), accountingRecordNumber));
    }

    /**
     * Gets the Accounting-Record-Number from this message.
     *
     * @return The accounting record number, or -1 if not found.
     */
    default long getAccountingRecordNumber() {
        final AVP accountingRecordNumberAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER, 0));
        if (accountingRecordNumberAVP != null) {
            return accountingRecordNumberAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
