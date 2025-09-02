package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Session-Timeout AVP.
 * <p>
 * This interface provides default implementations for handling the Session-Timeout AVP
 * as defined in RFC 6733. The Session-Timeout AVP contains the maximum number of seconds of service to be provided.
 * </p>
 */
public interface HasSessionTimeoutAVP<T extends HasSessionTimeoutAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Session-Timeout AVP.
     *
     * @param sessionTimeout The session timeout to set.
     */
    default T setSessionTimeout(final long sessionTimeout) {
        setAVP(AVP.create(DiameterConstants.AVP_SESSION_TIMEOUT, sessionTimeout));
        return self();
    }

    /**
     * Gets the Session-Timeout from this message.
     *
     * @return The session timeout, or -1 if not found.
     */
    default long getSessionTimeout() {
        final AVP sessionTimeoutAVP = findAVP(DiameterConstants.AVP_SESSION_TIMEOUT);
        if (sessionTimeoutAVP != null) {
            return sessionTimeoutAVP.getDataAsLong();
        }
        return -1;
    }
}
