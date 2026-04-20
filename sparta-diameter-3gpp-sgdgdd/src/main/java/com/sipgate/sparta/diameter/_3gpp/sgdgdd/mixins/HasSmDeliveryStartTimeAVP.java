package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.Date;

/**
 * Mixin for messages carrying an SM-Delivery-Start-Time AVP (3GPP TS 29.338 §6.3.3.11, code 3307).
 * <p>
 * Time — UTC timestamp at which the SM Delivery Supervision Timer was started. M,V flags.
 * </p>
 */
public interface HasSmDeliveryStartTimeAVP extends AVPContainer {

    default void setSmDeliveryStartTime(final Date value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SM_DELIVERY_START_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getSmDeliveryStartTime() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SM_DELIVERY_START_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
