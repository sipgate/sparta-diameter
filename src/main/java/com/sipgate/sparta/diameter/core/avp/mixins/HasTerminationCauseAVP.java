package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Termination-Cause AVP.
 * <p>
 * This interface provides default implementations for handling the Termination-Cause AVP
 * as defined in RFC 6733. The Termination-Cause AVP is used to indicate the reason why a session is being terminated.
 * </p>
 */
public interface HasTerminationCauseAVP<T extends HasTerminationCauseAVP<T>> extends AVPContainer {

    /**
     * Sets the Termination-Cause AVP.
     *
     * @param terminationCause The termination cause to set.
     */
    default T setTerminationCause(final int terminationCause) {
        setAVP(AVP.create(DiameterConstants.AVP_TERMINATION_CAUSE, terminationCause));
        return self();
    }

    /**
     * Gets the Termination-Cause from this message.
     *
     * @return The termination cause, or -1 if not found.
     */
    default int getTerminationCause() {
        final AVP terminationCauseAVP = findAVP(DiameterConstants.AVP_TERMINATION_CAUSE);
        if (terminationCauseAVP != null) {
            return terminationCauseAVP.getDataAsInt();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
