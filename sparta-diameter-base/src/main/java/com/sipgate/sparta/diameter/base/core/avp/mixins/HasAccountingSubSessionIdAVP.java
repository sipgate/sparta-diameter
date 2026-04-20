package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.math.BigInteger;

/**
 * Interface for Diameter messages that include Accounting-Sub-Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Sub-Session-Id AVP
 * as defined in RFC 6733. The Accounting-Sub-Session-Id AVP contains the accounting sub-session identifier.
 * </p>
 */
public interface HasAccountingSubSessionIdAVP extends AVPContainer {

    /**
     * Sets the Accounting-Sub-Session-Id AVP.
     *
     * @param accountingSubSessionId The accounting sub-session identifier to set.
     */
    default void setAccountingSubSessionId(final BigInteger accountingSubSessionId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, 0), accountingSubSessionId));
    }

    /**
     * Gets the Accounting-Sub-Session-Id from this message.
     *
     * @return The accounting sub-session identifier, or null if not found.
     */
    default BigInteger getAccountingSubSessionId() {
        final AVP accountingSubSessionIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, 0));
        if (accountingSubSessionIdAVP != null) {
            return accountingSubSessionIdAVP.getDataAsUnsignedLong();
        }
        return null;
    }
}
