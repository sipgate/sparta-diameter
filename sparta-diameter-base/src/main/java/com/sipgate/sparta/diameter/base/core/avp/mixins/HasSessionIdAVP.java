package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Session-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Session-Id AVP
 * as defined in RFC 6733. The Session-Id AVP is used to identify a specific session.
 * </p>
 */
public interface HasSessionIdAVP<T extends HasSessionIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Session-Id AVP.
     *
     * @param sessionId The session identifier to set.
     */
    default T setSessionId(final String sessionId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), sessionId));
        return self();
    }

    /**
     * Gets the Session-Id from this message.
     *
     * @return The session identifier, or null if not found.
     */
    default String getSessionId() {
        final AVP sessionIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0));
        if (sessionIdAVP != null) {
            return sessionIdAVP.getDataAsString();
        }
        return null;
    }
}
