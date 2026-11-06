package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-User-Location-Info AVP (TS 29.061 §16.4.7.2, code 22). */
public interface Has3gppUserLocationInfoAVP extends AVPContainer {

    default void set3gppUserLocationInfo(final byte[] value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_USER_LOCATION_INFO, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] get3gppUserLocationInfo() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_USER_LOCATION_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
