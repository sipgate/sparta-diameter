package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the ToS-Traffic-Class AVP (3GPP TS 29.212, code 1014). */
public interface HasTosTrafficClassAVP extends AVPContainer {

    default void setTosTrafficClass(final byte[] value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_TOS_TRAFFIC_CLASS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getTosTrafficClass() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_TOS_TRAFFIC_CLASS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
