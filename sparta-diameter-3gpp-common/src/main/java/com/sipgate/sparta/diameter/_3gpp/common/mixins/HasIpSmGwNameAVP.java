package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IP-SM-GW-Name AVP (3GPP, code 3101). */
public interface HasIpSmGwNameAVP extends AVPContainer {

    default void setIpSmGwName(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_IP_SM_GW_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getIpSmGwName() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_IP_SM_GW_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
