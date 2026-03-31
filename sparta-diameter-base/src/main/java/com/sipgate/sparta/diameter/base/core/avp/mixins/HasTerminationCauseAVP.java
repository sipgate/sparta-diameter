package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Termination-Cause AVP.
 * <p>
 * This interface provides default implementations for handling the Termination-Cause AVP
 * as defined in RFC 6733. The Termination-Cause AVP is used to indicate the reason why a session is being terminated.
 * </p>
 */
public interface HasTerminationCauseAVP<T extends HasTerminationCauseAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Termination-Cause AVP.
     *
     * @param terminationCause The termination cause to set.
     */
    default T setTerminationCause(final int terminationCause) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_TERMINATION_CAUSE, 0), terminationCause));
        return self();
    }

    /**
     * Gets the Termination-Cause from this message.
     *
     * @return The termination cause, or -1 if not found.
     */
    default int getTerminationCause() {
        final AVP terminationCauseAVP = findAVP(new AVPKey(DiameterConstants.AVP_TERMINATION_CAUSE, 0));
        if (terminationCauseAVP != null) {
            return terminationCauseAVP.getDataAsInt();
        }
        return -1;
    }
}
