package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Re-Auth-Request-Type AVP.
 * <p>
 * This interface provides default implementations for handling the Re-Auth-Request-Type AVP
 * as defined in RFC 6733. The Re-Auth-Request-Type AVP is used to inform the client of the action expected upon expiration of the Authorization-Lifetime.
 * </p>
 */
public interface HasReAuthRequestTypeAVP<T extends HasReAuthRequestTypeAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Re-Auth-Request-Type AVP.
     *
     * @param reAuthRequestType The re-auth request type to set.
     */
    default T setReAuthRequestType(final int reAuthRequestType) {
        setAVP(AVP.create(DiameterConstants.AVP_RE_AUTH_REQUEST_TYPE, reAuthRequestType));
        return self();
    }

    /**
     * Gets the Re-Auth-Request-Type from this message.
     *
     * @return The re-auth request type, or -1 if not found.
     */
    default int getReAuthRequestType() {
        final AVP reAuthRequestTypeAVP = findAVP(DiameterConstants.AVP_RE_AUTH_REQUEST_TYPE);
        if (reAuthRequestTypeAVP != null) {
            return reAuthRequestTypeAVP.getDataAsInt();
        }
        return -1;
    }
}
