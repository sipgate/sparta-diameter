package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include User-Name AVP.
 * <p>
 * This interface provides default implementations for handling the User-Name AVP
 * as defined in RFC 6733. The User-Name AVP contains the username in a format consistent with the NAI specification.
 * </p>
 */
public interface HasUserNameAVP extends AVPContainer {

    /**
     * Sets the User-Name AVP.
     *
     * @param userName The username to set.
     */
    default void setUserName(final String userName) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_USER_NAME, 0), userName));
    }

    /**
     * Gets the User-Name from this message.
     *
     * @return The username, or null if not found.
     */
    default String getUserName() {
        final AVP userNameAVP = findAVP(new AVPKey(DiameterConstants.AVP_USER_NAME, 0));
        if (userNameAVP != null) {
            return userNameAVP.getDataAsString();
        }
        return null;
    }
}
