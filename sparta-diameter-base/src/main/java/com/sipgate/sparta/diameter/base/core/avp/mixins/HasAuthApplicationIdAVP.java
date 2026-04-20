package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Auth-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Auth-Application-Id AVP
 * as defined in RFC 6733. The Auth-Application-Id AVP is used to advertise support of the Authentication and Authorization portion of an application.
 * </p>
 */
public interface HasAuthApplicationIdAVP extends AVPContainer {

    /**
     * Sets the Auth-Application-Id AVP.
     *
     * @param authApplicationId The authentication application identifier to set.
     */
    default void setAuthApplicationId(final long authApplicationId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), authApplicationId));
    }

    /**
     * Gets the Auth-Application-Id from this message.
     *
     * @return The authentication application identifier, or -1 if not found.
     */
    default long getAuthApplicationId() {
        final AVP authApplicationIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0));
        if (authApplicationIdAVP != null) {
            return authApplicationIdAVP.getDataAsLong();
        }
        return -1;
    }

}
