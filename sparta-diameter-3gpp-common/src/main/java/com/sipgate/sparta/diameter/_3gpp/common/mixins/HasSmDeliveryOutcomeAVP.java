package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying an SM-Delivery-Outcome AVP (3GPP TS 29.338 §5.3.3.14).
 */
public interface HasSmDeliveryOutcomeAVP<T extends HasSmDeliveryOutcomeAVP<T>> extends AVPContainer<T> {

    default T setSmDeliveryOutcome(final GroupedAVP value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, _3gppConstants.VENDOR_ID_3GPP), value.getAVPs()));
        return self();
    }

    default GroupedAVP getSmDeliveryOutcome() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
