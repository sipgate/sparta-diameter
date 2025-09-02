package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Record-Number AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Record-Number AVP
 * as defined in RFC 6733. The Accounting-Record-Number AVP identifies this record within one session.
 * </p>
 */
public interface HasAccountingRecordNumberAVP<T extends HasAccountingRecordNumberAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Accounting-Record-Number AVP.
     *
     * @param accountingRecordNumber The accounting record number to set.
     */
    default T setAccountingRecordNumber(final long accountingRecordNumber) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER, accountingRecordNumber));
        return self();
    }

    /**
     * Gets the Accounting-Record-Number from this message.
     *
     * @return The accounting record number, or -1 if not found.
     */
    default long getAccountingRecordNumber() {
        final AVP accountingRecordNumberAVP = findAVP(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER);
        if (accountingRecordNumberAVP != null) {
            return accountingRecordNumberAVP.getDataAsLong();
        }
        return -1;
    }
}
