package com.sipgate.sparta.diameter.ietf.load.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.load.LoadConstants;

import java.math.BigInteger;

/** Mixin for containers carrying the Load-Value AVP (RFC 8583 §7.3, code 652). */
public interface HasLoadValueAVP extends AVPContainer {

    default void setLoadValue(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(LoadConstants.AVP_LOAD_VALUE, 0), value));
    }

    default BigInteger getLoadValue() {
        final var avp = findAVP(new AVPKey(LoadConstants.AVP_LOAD_VALUE, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
