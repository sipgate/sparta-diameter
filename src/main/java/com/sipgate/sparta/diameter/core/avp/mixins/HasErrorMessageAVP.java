package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Error-Message AVP.
 * <p>
 * This interface provides default implementations for handling the Error-Message AVP
 * as defined in RFC 6733. The Error-Message AVP contains a human-readable error message.
 * </p>
 */
public interface HasErrorMessageAVP<T extends HasErrorMessageAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Error-Message AVP.
     *
     * @param errorMessage The error message to set.
     */
    default T setErrorMessage(final String errorMessage) {
        setAVP(AVP.create(DiameterConstants.AVP_ERROR_MESSAGE, errorMessage));
        return self();
    }

    /**
     * Gets the Error-Message from this message.
     *
     * @return The error message, or null if not found.
     */
    default String getErrorMessage() {
        final AVP errorMessageAVP = findAVP(DiameterConstants.AVP_ERROR_MESSAGE);
        if (errorMessageAVP != null) {
            return errorMessageAVP.getDataAsString();
        }
        return null;
    }
}
