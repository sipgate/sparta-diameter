package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the AN-Trusted AVP (3GPP TS 29.273 §8.2.3.9, code 1503). */
public interface HasAnTrustedAVP extends AVPContainer {

    /**
     * Sets the AN-Trusted AVP.
     *
     * @param value the AN-Trusted Enumerated value to set.
     */
    default void setAnTrusted(final int value) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_AN_TRUSTED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    /**
     * Gets the AN-Trusted value from this message.
     *
     * @return the AN-Trusted Enumerated value, or -1 if not present.
     */
    default int getAnTrusted() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_AN_TRUSTED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
