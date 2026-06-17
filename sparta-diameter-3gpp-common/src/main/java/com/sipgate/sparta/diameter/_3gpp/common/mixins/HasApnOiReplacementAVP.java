package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the APN-OI-Replacement AVP (3GPP, code 1427). */
public interface HasApnOiReplacementAVP extends AVPContainer {

    default void setApnOiReplacement(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_APN_OI_REPLACEMENT, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getApnOiReplacement() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_APN_OI_REPLACEMENT, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
