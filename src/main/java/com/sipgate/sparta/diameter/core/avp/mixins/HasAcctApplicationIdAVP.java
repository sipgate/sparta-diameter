package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Application-Id AVP
 * as defined in RFC 6733. The Acct-Application-Id AVP is used to advertise support of the Accounting portion of an application.
 * </p>
 */
public interface HasAcctApplicationIdAVP<T extends HasAcctApplicationIdAVP<T>> extends AVPContainer {

    /**
     * Sets the Acct-Application-Id AVP.
     *
     * @param acctApplicationId The accounting application identifier to set.
     */
    default T setAcctApplicationId(final long acctApplicationId) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCT_APPLICATION_ID, acctApplicationId));
        return self();
    }

    /**
     * Gets the Acct-Application-Id from this message.
     *
     * @return The accounting application identifier, or -1 if not found.
     */
    default long getAcctApplicationId() {
        final AVP acctApplicationIdAVP = findAVP(DiameterConstants.AVP_ACCT_APPLICATION_ID);
        if (acctApplicationIdAVP != null) {
            return acctApplicationIdAVP.getDataAsLong();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
