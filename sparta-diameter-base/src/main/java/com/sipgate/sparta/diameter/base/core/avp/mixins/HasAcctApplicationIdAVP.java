package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Application-Id AVP
 * as defined in RFC 6733. The Acct-Application-Id AVP is used to advertise support of the Accounting portion of an application.
 * </p>
 */
public interface HasAcctApplicationIdAVP<T extends HasAcctApplicationIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Acct-Application-Id AVP.
     *
     * @param acctApplicationId The accounting application identifier to set.
     */
    default T setAcctApplicationId(final long acctApplicationId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0), acctApplicationId));
        return self();
    }

    /**
     * Gets the Acct-Application-Id from this message.
     *
     * @return The accounting application identifier, or -1 if not found.
     */
    default long getAcctApplicationId() {
        final AVP acctApplicationIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0));
        if (acctApplicationIdAVP != null) {
            return acctApplicationIdAVP.getDataAsLong();
        }
        return -1;
    }
}
