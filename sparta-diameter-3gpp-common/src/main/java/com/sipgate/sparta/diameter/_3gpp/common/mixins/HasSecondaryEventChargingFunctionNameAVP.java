package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Secondary-Event-Charging-Function-Name AVP (3GPP, code 620). */
public interface HasSecondaryEventChargingFunctionNameAVP extends AVPContainer {

    default void setSecondaryEventChargingFunctionName(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getSecondaryEventChargingFunctionName() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
