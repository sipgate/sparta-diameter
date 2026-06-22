package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the ANID AVP (3GPP TS 29.273 §8.2.3.10, code 1504). */
public interface HasAnidAVP extends AVPContainer {

    /**
     * Sets the ANID AVP.
     *
     * @param value the ANID UTF8String value to set.
     */
    default void setAnid(final String value) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_ANID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    /**
     * Gets the ANID value from this message.
     *
     * @return the ANID UTF8String value, or null if not present.
     */
    default String getAnid() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_ANID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
