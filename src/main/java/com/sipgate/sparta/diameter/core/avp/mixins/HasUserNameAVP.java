package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include User-Name AVP.
 * <p>
 * This interface provides default implementations for handling the User-Name AVP
 * as defined in RFC 6733. The User-Name AVP contains the username in a format consistent with the NAI specification.
 * </p>
 */
public interface HasUserNameAVP<T extends HasUserNameAVP<T>> extends AVPContainer {

    /**
     * Sets the User-Name AVP.
     *
     * @param userName The username to set.
     */
    default T setUserName(final String userName) {
        setAVP(AVP.create(DiameterConstants.AVP_USER_NAME, userName));
        return self();
    }

    /**
     * Gets the User-Name from this message.
     *
     * @return The username, or null if not found.
     */
    default String getUserName() {
        final AVP userNameAVP = findAVP(DiameterConstants.AVP_USER_NAME);
        if (userNameAVP != null) {
            return userNameAVP.getDataAsString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
