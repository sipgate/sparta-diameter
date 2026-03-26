package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Record-Type AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Record-Type AVP
 * as defined in RFC 6733. The Accounting-Record-Type AVP contains the type of accounting record being sent.
 * </p>
 */
public interface HasAccountingRecordTypeAVP<T extends HasAccountingRecordTypeAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Accounting-Record-Type AVP.
     *
     * @param accountingRecordType The accounting record type to set.
     */
    default T setAccountingRecordType(final int accountingRecordType) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE, accountingRecordType));
        return self();
    }

    /**
     * Gets the Accounting-Record-Type from this message.
     *
     * @return The accounting record type, or -1 if not found.
     */
    default int getAccountingRecordType() {
        final AVP accountingRecordTypeAVP = findAVP(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE);
        if (accountingRecordTypeAVP != null) {
            return accountingRecordTypeAVP.getDataAsInt();
        }
        return -1;
    }
}
