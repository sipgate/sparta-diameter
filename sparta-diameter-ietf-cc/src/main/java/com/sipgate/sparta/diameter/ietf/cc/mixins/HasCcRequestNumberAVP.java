package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Request-Number AVP (RFC 4006 §8.2, code 415). */
public interface HasCcRequestNumberAVP extends AVPContainer {

    default void setCcRequestNumber(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_REQUEST_NUMBER, 0), value));
    }

    default long getCcRequestNumber() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_REQUEST_NUMBER, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
