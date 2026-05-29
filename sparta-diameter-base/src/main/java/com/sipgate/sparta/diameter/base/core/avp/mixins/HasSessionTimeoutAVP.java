package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Session-Timeout AVP.
 * <p>
 * This interface provides default implementations for handling the Session-Timeout AVP
 * as defined in RFC 6733. The Session-Timeout AVP contains the maximum number of seconds of service to be provided.
 * </p>
 */
public interface HasSessionTimeoutAVP extends AVPContainer {

    /**
     * Sets the Session-Timeout AVP.
     *
     * @param sessionTimeout The session timeout to set.
     */
    default void setSessionTimeout(final long sessionTimeout) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_TIMEOUT, 0), sessionTimeout));
    }

    /**
     * Gets the Session-Timeout from this message.
     *
     * @return The session timeout, or -1 if not found.
     */
    default long getSessionTimeout() {
        final AVP sessionTimeoutAVP = findAVP(new AVPKey(DiameterConstants.AVP_SESSION_TIMEOUT, 0));
        if (sessionTimeoutAVP != null) {
            return sessionTimeoutAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
