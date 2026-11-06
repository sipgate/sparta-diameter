package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Final-Unit-Action AVP (RFC 4006 §8.35, code 449). */
public interface HasFinalUnitActionAVP extends AVPContainer {

    default void setFinalUnitAction(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_FINAL_UNIT_ACTION, 0), value));
    }

    default int getFinalUnitAction() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_FINAL_UNIT_ACTION, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
