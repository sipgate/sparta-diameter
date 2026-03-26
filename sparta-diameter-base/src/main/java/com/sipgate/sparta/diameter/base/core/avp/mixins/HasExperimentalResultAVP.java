package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
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
public interface HasExperimentalResultAVP<T extends HasExperimentalResultAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Experimental-Result AVP.
     *
     * @param experimentalResult The experimental result to set.
     */
    default T setExperimentalResult(final GroupedAVP experimentalResult) {
        setAVP(experimentalResult);
        return self();
    }

    /**
     * Gets the Experimental-Result from this message.
     *
     * @return The experimental result, or null if not found.
     */
    default GroupedAVP getExperimentalResult() {
        final AVP experimentalResultAVP = findAVP(DiameterConstants.AVP_EXPERIMENTAL_RESULT);
        if (experimentalResultAVP != null) {
            return (GroupedAVP) experimentalResultAVP;
        }
        return null;
    }
}
