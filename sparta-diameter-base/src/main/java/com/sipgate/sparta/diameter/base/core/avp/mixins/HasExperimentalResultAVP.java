package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Experimental-Result AVP.
 * <p>
 * This interface provides default implementations for handling the Experimental-Result AVP
 * as defined in RFC 6733. The Experimental-Result AVP indicates that an error occurred for which there is no appropriate Result-Code value.
 * </p>
 */
public interface HasExperimentalResultAVP extends AVPContainer {

    /**
     * Sets the Experimental-Result AVP.
     *
     * @param experimentalResult The experimental result to set.
     */
    default void setExperimentalResult(final GroupedAVP experimentalResult) {
        setAVP(experimentalResult);
    }

    /**
     * Gets the Experimental-Result from this message.
     *
     * @return The experimental result, or null if not found.
     */
    default GroupedAVP getExperimentalResult() {
        final AVP experimentalResultAVP = findAVP(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT, 0));
        if (experimentalResultAVP != null) {
            return (GroupedAVP) experimentalResultAVP;
        }
        return null;
    }
}
