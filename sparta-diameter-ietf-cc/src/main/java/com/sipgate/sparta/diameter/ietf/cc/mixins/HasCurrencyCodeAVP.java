package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Currency-Code AVP (RFC 4006 §8.11, code 425). */
public interface HasCurrencyCodeAVP extends AVPContainer {

    default void setCurrencyCode(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CURRENCY_CODE, 0), value));
    }

    default long getCurrencyCode() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CURRENCY_CODE, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
