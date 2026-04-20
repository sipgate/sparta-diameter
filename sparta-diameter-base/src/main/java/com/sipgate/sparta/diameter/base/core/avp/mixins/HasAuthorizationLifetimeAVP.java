package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Authorization-Lifetime AVP.
 * <p>
 * This interface provides default implementations for handling the Authorization-Lifetime AVP
 * as defined in RFC 6733. The Authorization-Lifetime AVP contains the maximum number of seconds of service to be provided.
 * </p>
 */
public interface HasAuthorizationLifetimeAVP extends AVPContainer {

    /**
     * Sets the Authorization-Lifetime AVP.
     *
     * @param authorizationLifetime The authorization lifetime to set.
     */
    default void setAuthorizationLifetime(final long authorizationLifetime) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTHORIZATION_LIFETIME, 0), authorizationLifetime));
    }

    /**
     * Gets the Authorization-Lifetime from this message.
     *
     * @return The authorization lifetime, or -1 if not found.
     */
    default long getAuthorizationLifetime() {
        final AVP authorizationLifetimeAVP = findAVP(new AVPKey(DiameterConstants.AVP_AUTHORIZATION_LIFETIME, 0));
        if (authorizationLifetimeAVP != null) {
            return authorizationLifetimeAVP.getDataAsLong();
        }
        return -1;
    }
}
