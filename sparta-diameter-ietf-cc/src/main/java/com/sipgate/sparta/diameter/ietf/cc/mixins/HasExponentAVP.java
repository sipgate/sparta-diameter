package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Exponent AVP (RFC 4006 §8.9, code 429). */
public interface HasExponentAVP extends AVPContainer {

    default void setExponent(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_EXPONENT, 0), value));
    }

    default int getExponent() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_EXPONENT, 0));
        return avp != null ? avp.getDataAsInt() : 0;
    }
}
