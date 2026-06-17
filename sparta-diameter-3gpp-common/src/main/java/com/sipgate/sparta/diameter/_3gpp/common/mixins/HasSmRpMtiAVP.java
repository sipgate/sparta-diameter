package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SM-RP-MTI AVP (3GPP, code 3308). */
public interface HasSmRpMtiAVP extends AVPContainer {

    default void setSmRpMti(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SM_RP_MTI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSmRpMti() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SM_RP_MTI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
