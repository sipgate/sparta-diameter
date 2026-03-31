package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Auth-Request-Type AVP.
 * <p>
 * This interface provides default implementations for handling the Auth-Request-Type AVP
 * as defined in RFC 6733. The Auth-Request-Type AVP is used to determine session state.
 * </p>
 */
public interface HasAuthRequestTypeAVP<T extends HasAuthRequestTypeAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Auth-Request-Type AVP.
     *
     * @param authRequestType The authentication request type to set.
     */
    default T setAuthRequestType(final int authRequestType) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_REQUEST_TYPE, 0), authRequestType));
        return self();
    }

    /**
     * Gets the Auth-Request-Type from this message.
     *
     * @return The authentication request type, or -1 if not found.
     */
    default int getAuthRequestType() {
        final AVP authRequestTypeAVP = findAVP(new AVPKey(DiameterConstants.AVP_AUTH_REQUEST_TYPE, 0));
        if (authRequestTypeAVP != null) {
            return authRequestTypeAVP.getDataAsInt();
        }
        return -1;
    }
}
