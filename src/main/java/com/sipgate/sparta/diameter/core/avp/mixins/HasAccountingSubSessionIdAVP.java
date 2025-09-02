package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

import java.math.BigInteger;

/**
 * Interface for Diameter messages that include Accounting-Sub-Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Sub-Session-Id AVP
 * as defined in RFC 6733. The Accounting-Sub-Session-Id AVP contains the accounting sub-session identifier.
 * </p>
 */
public interface HasAccountingSubSessionIdAVP<T extends HasAccountingSubSessionIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Accounting-Sub-Session-Id AVP.
     *
     * @param accountingSubSessionId The accounting sub-session identifier to set.
     */
    default T setAccountingSubSessionId(final BigInteger accountingSubSessionId) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, accountingSubSessionId));
        return self();
    }

    /**
     * Gets the Accounting-Sub-Session-Id from this message.
     *
     * @return The accounting sub-session identifier, or null if not found.
     */
    default BigInteger getAccountingSubSessionId() {
        final AVP accountingSubSessionIdAVP = findAVP(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID);
        if (accountingSubSessionIdAVP != null) {
            return accountingSubSessionIdAVP.getDataAsUnsignedLong();
        }
        return null;
    }
}
