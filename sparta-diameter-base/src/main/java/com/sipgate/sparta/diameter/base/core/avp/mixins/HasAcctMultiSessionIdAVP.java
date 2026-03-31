package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Multi-Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Multi-Session-Id AVP
 * as defined in RFC 6733. The Acct-Multi-Session-Id AVP is used to link together multiple related accounting sessions.
 * </p>
 */
public interface HasAcctMultiSessionIdAVP<T extends HasAcctMultiSessionIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Acct-Multi-Session-Id AVP.
     *
     * @param acctMultiSessionId The accounting multi-session identifier to set.
     */
    default T setAcctMultiSessionId(final String acctMultiSessionId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID, 0), acctMultiSessionId));
        return self();
    }

    /**
     * Gets the Acct-Multi-Session-Id from this message.
     *
     * @return The accounting multi-session identifier, or null if not found.
     */
    default String getAcctMultiSessionId() {
        final AVP acctMultiSessionIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID, 0));
        if (acctMultiSessionIdAVP != null) {
            return acctMultiSessionIdAVP.getDataAsString();
        }
        return null;
    }
}
