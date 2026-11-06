package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Final-Unit-Indication AVP (RFC 4006 §8.34, code 430). */
public interface HasFinalUnitIndicationAVP extends AVPContainer {

    default void setFinalUnitIndication(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_FINAL_UNIT_INDICATION, 0), avps));
    }

    default AVPContainer getFinalUnitIndication() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_FINAL_UNIT_INDICATION, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
