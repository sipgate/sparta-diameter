package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Deregistration-Reason AVP (3GPP, code 615). */
public interface HasDeregistrationReasonAVP extends AVPContainer {

    default void setDeregistrationReason(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_DEREGISTRATION_REASON, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getDeregistrationReason() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_DEREGISTRATION_REASON, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
