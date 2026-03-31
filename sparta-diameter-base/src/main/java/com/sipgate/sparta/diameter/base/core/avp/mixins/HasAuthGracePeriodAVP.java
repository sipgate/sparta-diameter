package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Auth-Grace-Period AVP.
 * <p>
 * This interface provides default implementations for handling the Auth-Grace-Period AVP
 * as defined in RFC 6733. The Auth-Grace-Period AVP contains the grace period between authentications.
 * </p>
 */
public interface HasAuthGracePeriodAVP<T extends HasAuthGracePeriodAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Auth-Grace-Period AVP.
     *
     * @param authGracePeriod The authentication grace period to set.
     */
    default T setAuthGracePeriod(final long authGracePeriod) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_GRACE_PERIOD, 0), authGracePeriod));
        return self();
    }

    /**
     * Gets the Auth-Grace-Period from this message.
     *
     * @return The authentication grace period, or -1 if not found.
     */
    default long getAuthGracePeriod() {
        final AVP authGracePeriodAVP = findAVP(new AVPKey(DiameterConstants.AVP_AUTH_GRACE_PERIOD, 0));
        if (authGracePeriodAVP != null) {
            return authGracePeriodAVP.getDataAsLong();
        }
        return -1;
    }
}
