package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Redirect-Host-Usage AVP.
 * <p>
 * This interface provides default implementations for handling the Redirect-Host-Usage AVP
 * as defined in RFC 6733. The Redirect-Host-Usage AVP dictates how the routing entry resulting from a Redirect indication should be used.
 * </p>
 */
public interface HasRedirectHostUsageAVP<T extends HasRedirectHostUsageAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Redirect-Host-Usage AVP.
     *
     * @param redirectHostUsage The redirect host usage to set.
     */
    default T setRedirectHostUsage(final int redirectHostUsage) {
        setAVP(AVP.create(DiameterConstants.AVP_REDIRECT_HOST_USAGE, redirectHostUsage));
        return self();
    }

    /**
     * Gets the Redirect-Host-Usage from this message.
     *
     * @return The redirect host usage, or -1 if not found.
     */
    default int getRedirectHostUsage() {
        final AVP redirectHostUsageAVP = findAVP(DiameterConstants.AVP_REDIRECT_HOST_USAGE);
        if (redirectHostUsageAVP != null) {
            return redirectHostUsageAVP.getDataAsInt();
        }
        return -1;
    }
}
