package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the CC-Money AVP (RFC 4006 §8.22, code 413). */
public interface HasCcMoneyAVP extends AVPContainer {

    default void setCcMoney(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_MONEY, 0), avps));
    }

    default AVPContainer getCcMoney() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_MONEY, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
