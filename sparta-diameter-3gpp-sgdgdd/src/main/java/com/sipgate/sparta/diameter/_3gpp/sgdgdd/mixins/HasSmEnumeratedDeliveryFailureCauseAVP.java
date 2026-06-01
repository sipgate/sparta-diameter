package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SM-Enumerated-Delivery-Failure-Cause AVP (3GPP TS 29.338 §6.3.3.6, code 3304).
 * <p>
 * Enumerated — see {@code SM_DELIVERY_FAILURE_CAUSE_*} constants in {@link SgdGddConstants}. M,V flags.
 * </p>
 */
public interface HasSmEnumeratedDeliveryFailureCauseAVP extends AVPContainer {

    default void setSmEnumeratedDeliveryFailureCause(final int value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SM_ENUMERATED_DELIVERY_FAILURE_CAUSE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSmEnumeratedDeliveryFailureCause() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SM_ENUMERATED_DELIVERY_FAILURE_CAUSE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
