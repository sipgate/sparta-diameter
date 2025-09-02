package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Result-Code AVP.
 * <p>
 * This interface provides default implementations for handling the Result-Code AVP
 * as defined in RFC 6733. The Result-Code AVP indicates whether a particular request was completed successfully or whether an error occurred.
 * </p>
 */
public interface HasResultCodeAVP<T extends HasResultCodeAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Result-Code AVP.
     *
     * @param resultCode The result code to set.
     */
    default T setResultCode(final long resultCode) {
        setAVP(AVP.create(DiameterConstants.AVP_RESULT_CODE, resultCode));
        return self();
    }

    /**
     * Gets the Result-Code from this message.
     *
     * @return The result code, or -1 if not found.
     */
    default long getResultCode() {
        final AVP resultCodeAVP = findAVP(DiameterConstants.AVP_RESULT_CODE);
        if (resultCodeAVP != null) {
            return resultCodeAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
