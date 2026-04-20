package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Experimental-Result-Code AVP.
 * <p>
 * This interface provides default implementations for handling the Experimental-Result-Code AVP
 * as defined in RFC 6733. The Experimental-Result-Code AVP contains a vendor-assigned value representing the result of processing the request.
 * </p>
 */
public interface HasExperimentalResultCodeAVP extends AVPContainer {

    /**
     * Sets the Experimental-Result-Code AVP.
     *
     * @param experimentalResultCode The experimental result code to set.
     */
    default void setExperimentalResultCode(final long experimentalResultCode) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0), experimentalResultCode));
    }

    /**
     * Gets the Experimental-Result-Code from this message.
     *
     * @return The experimental result code, or -1 if not found.
     */
    default long getExperimentalResultCode() {
        final AVP experimentalResultCodeAVP = findAVP(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0));
        if (experimentalResultCodeAVP != null) {
            return experimentalResultCodeAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
