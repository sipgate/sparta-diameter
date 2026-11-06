package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Time AVP (RFC 4006 §8.21, code 420). */
public interface HasCcTimeAVP extends AVPContainer {

    default void setCcTime(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_TIME, 0), value));
    }

    default long getCcTime() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_TIME, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
