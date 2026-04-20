package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Class AVP.
 * <p>
 * This interface provides default implementations for handling the Class AVP
 * as defined in RFC 6733. The Class AVP is used to provide additional information to authorization/accounting servers.
 * </p>
 */
public interface HasClassAVP extends AVPContainer {

    /**
     * Sets the Class AVP.
     *
     * @param classValue The class value to set.
     */
    default void setClassAVP(final byte[] classValue) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_CLASS, 0), classValue));
    }

    /**
     * Gets the Class from this message.
     *
     * @return The class value, or null if not found.
     */
    default byte[] getClassAVP() {
        final AVP classAVP = findAVP(new AVPKey(DiameterConstants.AVP_CLASS, 0));
        if (classAVP != null) {
            return classAVP.getData();
        }
        return null;
    }
}
