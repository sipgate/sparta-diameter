package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying an SM-Delivery-Outcome AVP (3GPP TS 29.338 §5.3.3.14).
 */
public interface HasSmDeliveryOutcomeAVP extends AVPContainer {

    default void setSmDeliveryOutcome(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getSmDeliveryOutcome() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
