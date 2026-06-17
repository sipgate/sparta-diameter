package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Primary-Charging-Collection-Function-Name AVP (3GPP, code 621). */
public interface HasPrimaryChargingCollectionFunctionNameAVP extends AVPContainer {

    default void setPrimaryChargingCollectionFunctionName(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getPrimaryChargingCollectionFunctionName() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
