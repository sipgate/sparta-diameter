package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SM-Delivery-Cause AVP (3GPP, code 3321). */
public interface HasSmDeliveryCauseAVP extends AVPContainer {

    default void setSmDeliveryCause(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_CAUSE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSmDeliveryCause() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_CAUSE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
