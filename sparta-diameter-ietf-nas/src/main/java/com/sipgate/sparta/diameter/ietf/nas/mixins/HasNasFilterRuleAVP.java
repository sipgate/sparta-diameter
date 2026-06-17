package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the NAS-Filter-Rule AVP (RFC 4005 §6.6, code 400). */
public interface HasNasFilterRuleAVP extends AVPContainer {

    default void setNasFilterRule(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_NAS_FILTER_RULE, 0), value));
    }

    default String getNasFilterRule() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_NAS_FILTER_RULE, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
