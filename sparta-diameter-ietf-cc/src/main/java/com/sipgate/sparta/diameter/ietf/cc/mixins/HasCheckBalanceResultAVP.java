package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Check-Balance-Result AVP (RFC 4006 §8.6, code 422). */
public interface HasCheckBalanceResultAVP extends AVPContainer {

    default void setCheckBalanceResult(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CHECK_BALANCE_RESULT, 0), value));
    }

    default int getCheckBalanceResult() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CHECK_BALANCE_RESULT, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
