package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.math.BigInteger;

/** Mixin for messages carrying the CC-Sub-Session-Id AVP (RFC 4006 §8.5, code 419). */
public interface HasCcSubSessionIdAVP extends AVPContainer {

    default void setCcSubSessionId(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_SUB_SESSION_ID, 0), value));
    }

    default BigInteger getCcSubSessionId() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_SUB_SESSION_ID, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
