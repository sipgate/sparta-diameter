package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Primary-Event-Charging-Function-Name AVP (3GPP, code 619). */
public interface HasPrimaryEventChargingFunctionNameAVP extends AVPContainer {

    default void setPrimaryEventChargingFunctionName(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getPrimaryEventChargingFunctionName() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
