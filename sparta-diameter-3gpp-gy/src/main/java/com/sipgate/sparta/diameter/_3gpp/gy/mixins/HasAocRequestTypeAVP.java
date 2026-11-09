package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the AoC-Request-Type AVP (TS 32.299, code 2055). */
public interface HasAocRequestTypeAVP extends AVPContainer {

    default void setAocRequestType(final int value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_AOC_REQUEST_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getAocRequestType() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_AOC_REQUEST_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
