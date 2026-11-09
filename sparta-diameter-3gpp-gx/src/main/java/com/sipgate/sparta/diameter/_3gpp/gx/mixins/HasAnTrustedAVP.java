package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the AN-Trusted AVP (3GPP TS 29.212, code 1503). */
public interface HasAnTrustedAVP extends AVPContainer {

    default void setAnTrusted(final int value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_AN_TRUSTED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getAnTrusted() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_AN_TRUSTED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
