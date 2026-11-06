package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcAvps;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Value-Digits AVP (RFC 4006 §8.10, code 447). */
public interface HasValueDigitsAVP extends AVPContainer {

    default void setValueDigits(final long value) {
        setAVP(CcAvps.valueDigits(value));
    }

    default long getValueDigits() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_VALUE_DIGITS, 0));
        return avp != null ? avp.getDataAsLong() : 0L;
    }
}
