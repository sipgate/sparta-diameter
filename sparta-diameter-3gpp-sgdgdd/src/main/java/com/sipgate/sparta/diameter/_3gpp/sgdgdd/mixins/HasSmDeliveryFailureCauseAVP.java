package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying an SM-Delivery-Failure-Cause AVP (3GPP TS 29.338 §6.3.3.5, code 3303).
 * <p>
 * Grouped — contains SM-Enumerated-Delivery-Failure-Cause and optionally SM-Diagnostic-Info. M,V flags.
 * </p>
 */
public interface HasSmDeliveryFailureCauseAVP<T extends HasSmDeliveryFailureCauseAVP<T>> extends AVPContainer<T> {

    default T setSmDeliveryFailureCause(final GroupedAVP value) {
        setAVP(AVP.create(SgdGddConstants.AVP_SM_DELIVERY_FAILURE_CAUSE, value.getAVPs()));
        return self();
    }

    default GroupedAVP getSmDeliveryFailureCause() {
        final var avp = findAVP(SgdGddConstants.AVP_SM_DELIVERY_FAILURE_CAUSE);
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
