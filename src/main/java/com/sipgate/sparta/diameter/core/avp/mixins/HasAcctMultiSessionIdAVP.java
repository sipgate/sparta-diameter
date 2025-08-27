package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Multi-Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Multi-Session-Id AVP
 * as defined in RFC 6733. The Acct-Multi-Session-Id AVP is used to link together multiple related accounting sessions.
 * </p>
 */
public interface HasAcctMultiSessionIdAVP<T extends HasAcctMultiSessionIdAVP<T>> extends AVPContainer {

    /**
     * Sets the Acct-Multi-Session-Id AVP.
     *
     * @param acctMultiSessionId The accounting multi-session identifier to set.
     */
    default T setAcctMultiSessionId(final String acctMultiSessionId) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID, acctMultiSessionId));
        return self();
    }

    /**
     * Gets the Acct-Multi-Session-Id from this message.
     *
     * @return The accounting multi-session identifier, or null if not found.
     */
    default String getAcctMultiSessionId() {
        final AVP acctMultiSessionIdAVP = findAVP(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID);
        if (acctMultiSessionIdAVP != null) {
            return acctMultiSessionIdAVP.getDataAsString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
