package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IP-SM-GW-Number AVP (3GPP, code 3100). */
public interface HasIpSmGwNumberAVP extends AVPContainer {

    default void setIpSmGwNumber(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_IP_SM_GW_NUMBER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getIpSmGwNumber() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_IP_SM_GW_NUMBER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
