package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Restriction-Filter-Rule AVP (RFC 4006 §8.36, code 438). */
public interface HasRestrictionFilterRuleAVP extends AVPContainer {

    default void setRestrictionFilterRule(final String value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_RESTRICTION_FILTER_RULE, 0), value));
    }

    default String getRestrictionFilterRule() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_RESTRICTION_FILTER_RULE, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
