package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-PDP-Type AVP (TS 29.061 §16.4.7.2, code 3). */
public interface Has3gppPdpTypeAVP extends AVPContainer {

    default void set3gppPdpType(final int value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_PDP_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int get3gppPdpType() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_PDP_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
