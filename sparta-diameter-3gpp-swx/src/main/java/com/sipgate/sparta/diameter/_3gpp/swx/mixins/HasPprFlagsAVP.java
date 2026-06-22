package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the PPR-Flags AVP (3GPP TS 29.273 §8.2.3.14, code 1508). */
public interface HasPprFlagsAVP extends AVPContainer {

    /**
     * Sets the PPR-Flags AVP.
     *
     * @param value the PPR-Flags Unsigned32 value to set.
     */
    default void setPprFlags(final long value) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_PPR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    /**
     * Gets the PPR-Flags value from this message.
     *
     * @return the PPR-Flags Unsigned32 value, or -1L if not present.
     */
    default long getPprFlags() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_PPR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
