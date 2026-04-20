package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Record-Type AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Record-Type AVP
 * as defined in RFC 6733. The Accounting-Record-Type AVP contains the type of accounting record being sent.
 * </p>
 */
public interface HasAccountingRecordTypeAVP extends AVPContainer {

    /**
     * Sets the Accounting-Record-Type AVP.
     *
     * @param accountingRecordType The accounting record type to set.
     */
    default void setAccountingRecordType(final int accountingRecordType) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE, 0), accountingRecordType));
    }

    /**
     * Gets the Accounting-Record-Type from this message.
     *
     * @return The accounting record type, or -1 if not found.
     */
    default int getAccountingRecordType() {
        final AVP accountingRecordTypeAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE, 0));
        if (accountingRecordTypeAVP != null) {
            return accountingRecordTypeAVP.getDataAsInt();
        }
        return -1;
    }
}
