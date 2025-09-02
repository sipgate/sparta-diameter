package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Class AVP.
 * <p>
 * This interface provides default implementations for handling the Class AVP
 * as defined in RFC 6733. The Class AVP is used to provide additional information to authorization/accounting servers.
 * </p>
 */
public interface HasClassAVP<T extends HasClassAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Class AVP.
     *
     * @param classValue The class value to set.
     */
    default T setClassAVP(final byte[] classValue) {
        setAVP(AVP.create(DiameterConstants.AVP_CLASS, classValue));
        return self();
    }

    /**
     * Gets the Class from this message.
     *
     * @return The class value, or null if not found.
     */
    default byte[] getClassAVP() {
        final AVP classAVP = findAVP(DiameterConstants.AVP_CLASS);
        if (classAVP != null) {
            return classAVP.getData();
        }
        return null;
    }
}
