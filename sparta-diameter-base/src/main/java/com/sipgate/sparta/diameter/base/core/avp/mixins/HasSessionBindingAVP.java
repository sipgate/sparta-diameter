package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Session-Binding AVP.
 * <p>
 * This interface provides default implementations for handling the Session-Binding AVP
 * as defined in RFC 6733. The Session-Binding AVP is used to indicate a hint to the server about the session binding.
 * </p>
 */
public interface HasSessionBindingAVP extends AVPContainer {

    /**
     * Sets the Session-Binding AVP.
     *
     * @param sessionBinding The session binding to set.
     */
    default void setSessionBinding(final long sessionBinding) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_BINDING, 0), sessionBinding));
    }

    /**
     * Gets the Session-Binding from this message.
     *
     * @return The session binding, or -1 if not found.
     */
    default long getSessionBinding() {
        final AVP sessionBindingAVP = findAVP(new AVPKey(DiameterConstants.AVP_SESSION_BINDING, 0));
        if (sessionBindingAVP != null) {
            return sessionBindingAVP.getDataAsLong();
        }
        return -1;
    }
}
