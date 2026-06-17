package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IP-SM-GW-Realm AVP (3GPP, code 3112). */
public interface HasIpSmGwRealmAVP extends AVPContainer {

    default void setIpSmGwRealm(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_IP_SM_GW_REALM, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getIpSmGwRealm() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_IP_SM_GW_REALM, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
