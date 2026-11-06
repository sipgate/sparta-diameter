package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-Charging-Id AVP (TS 29.061 §16.4.7.2, code 2). */
public interface Has3gppChargingIdAVP extends AVPContainer {

    default void set3gppChargingId(final byte[] value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_CHARGING_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] get3gppChargingId() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_CHARGING_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
