package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Redirect-Max-Cache-Time AVP.
 * <p>
 * This interface provides default implementations for handling the Redirect-Max-Cache-Time AVP
 * as defined in RFC 6733. The Redirect-Max-Cache-Time AVP contains the maximum number of seconds the routing entry may be cached.
 * </p>
 */
public interface HasRedirectMaxCacheTimeAVP<T extends HasRedirectMaxCacheTimeAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Redirect-Max-Cache-Time AVP.
     *
     * @param redirectMaxCacheTime The redirect max cache time to set.
     */
    default T setRedirectMaxCacheTime(final long redirectMaxCacheTime) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_REDIRECT_MAX_CACHE_TIME, 0), redirectMaxCacheTime));
        return self();
    }

    /**
     * Gets the Redirect-Max-Cache-Time from this message.
     *
     * @return The redirect max cache time, or -1 if not found.
     */
    default long getRedirectMaxCacheTime() {
        final AVP redirectMaxCacheTimeAVP = findAVP(new AVPKey(DiameterConstants.AVP_REDIRECT_MAX_CACHE_TIME, 0));
        if (redirectMaxCacheTimeAVP != null) {
            return redirectMaxCacheTimeAVP.getDataAsLong();
        }
        return -1;
    }
}
