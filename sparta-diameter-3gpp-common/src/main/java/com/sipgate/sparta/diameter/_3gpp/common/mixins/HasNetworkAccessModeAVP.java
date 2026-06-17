package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Network-Access-Mode AVP (3GPP, code 1417). */
public interface HasNetworkAccessModeAVP extends AVPContainer {

    default void setNetworkAccessMode(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_NETWORK_ACCESS_MODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getNetworkAccessMode() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_NETWORK_ACCESS_MODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
