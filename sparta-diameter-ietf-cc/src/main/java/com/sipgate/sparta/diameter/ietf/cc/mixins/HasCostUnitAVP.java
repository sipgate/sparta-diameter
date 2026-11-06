package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Cost-Unit AVP (RFC 4006 §8.12, code 424). */
public interface HasCostUnitAVP extends AVPContainer {

    default void setCostUnit(final String value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_COST_UNIT, 0), value));
    }

    default String getCostUnit() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_COST_UNIT, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
