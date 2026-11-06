package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Cost-Information AVP (RFC 4006 §8.7, code 423). */
public interface HasCostInformationAVP extends AVPContainer {

    default void setCostInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_COST_INFORMATION, 0), avps));
    }

    default AVPContainer getCostInformation() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_COST_INFORMATION, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
