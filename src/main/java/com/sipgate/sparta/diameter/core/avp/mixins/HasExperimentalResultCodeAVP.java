package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Experimental-Result-Code AVP.
 * <p>
 * This interface provides default implementations for handling the Experimental-Result-Code AVP
 * as defined in RFC 6733. The Experimental-Result-Code AVP contains a vendor-assigned value representing the result of processing the request.
 * </p>
 */
public interface HasExperimentalResultCodeAVP<T extends HasExperimentalResultCodeAVP<T>> extends AVPContainer {

    /**
     * Sets the Experimental-Result-Code AVP.
     *
     * @param experimentalResultCode The experimental result code to set.
     */
    default T setExperimentalResultCode(final long experimentalResultCode) {
        setAVP(AVP.create(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, experimentalResultCode));
        return self();
    }

    /**
     * Gets the Experimental-Result-Code from this message.
     *
     * @return The experimental result code, or -1 if not found.
     */
    default long getExperimentalResultCode() {
        final AVP experimentalResultCodeAVP = findAVP(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE);
        if (experimentalResultCodeAVP != null) {
            return experimentalResultCodeAVP.getDataAsLong();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
