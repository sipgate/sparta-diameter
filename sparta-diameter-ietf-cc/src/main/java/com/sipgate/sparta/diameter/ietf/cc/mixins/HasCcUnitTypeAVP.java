package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Unit-Type AVP (RFC 4006 §8.32, code 454). */
public interface HasCcUnitTypeAVP extends AVPContainer {

    default void setCcUnitType(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_UNIT_TYPE, 0), value));
    }

    default int getCcUnitType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_UNIT_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
