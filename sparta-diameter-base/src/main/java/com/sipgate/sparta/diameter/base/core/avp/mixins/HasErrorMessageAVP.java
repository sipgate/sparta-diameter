package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Error-Message AVP.
 * <p>
 * This interface provides default implementations for handling the Error-Message AVP
 * as defined in RFC 6733. The Error-Message AVP contains a human-readable error message.
 * </p>
 */
public interface HasErrorMessageAVP extends AVPContainer {

    /**
     * Sets the Error-Message AVP.
     *
     * @param errorMessage The error message to set.
     */
    default void setErrorMessage(final String errorMessage) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ERROR_MESSAGE, 0), errorMessage));
    }

    /**
     * Gets the Error-Message from this message.
     *
     * @return The error message, or null if not found.
     */
    default String getErrorMessage() {
        final AVP errorMessageAVP = findAVP(new AVPKey(DiameterConstants.AVP_ERROR_MESSAGE, 0));
        if (errorMessageAVP != null) {
            return errorMessageAVP.getDataAsString();
        }
        return null;
    }
}
