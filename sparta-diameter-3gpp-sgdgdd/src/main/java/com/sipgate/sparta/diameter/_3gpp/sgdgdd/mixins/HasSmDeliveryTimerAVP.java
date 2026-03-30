package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SM-Delivery-Timer AVP (3GPP TS 29.338 §6.3.3.10, code 3306).
 * <p>
 * Unsigned32 — SM Delivery supervision timer value in seconds. M,V flags.
 * </p>
 */
public interface HasSmDeliveryTimerAVP<T extends HasSmDeliveryTimerAVP<T>> extends AVPContainer<T> {

    default T setSmDeliveryTimer(final long value) {
        setAVP(AVP.create(SgdGddConstants.AVP_SM_DELIVERY_TIMER, value));
        return self();
    }

    default long getSmDeliveryTimer() {
        final var avp = findAVP(SgdGddConstants.AVP_SM_DELIVERY_TIMER);
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
