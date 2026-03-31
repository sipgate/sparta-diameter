package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Session-Id AVP
 * as defined in RFC 6733. The Acct-Session-Id AVP is only used when RADIUS/Diameter translation occurs.
 * </p>
 */
public interface HasAcctSessionIdAVP<T extends HasAcctSessionIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Acct-Session-Id AVP.
     *
     * @param acctSessionId The accounting session identifier to set.
     */
    default T setAcctSessionId(final byte[] acctSessionId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_SESSION_ID, 0), acctSessionId));
        return self();
    }

    /**
     * Gets the Acct-Session-Id from this message.
     *
     * @return The accounting session identifier, or null if not found.
     */
    default byte[] getAcctSessionId() {
        final AVP acctSessionIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_SESSION_ID, 0));
        if (acctSessionIdAVP != null) {
            return acctSessionIdAVP.getData();
        }
        return null;
    }

}
