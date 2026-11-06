package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.math.BigInteger;

/** Mixin for messages carrying the CC-Input-Octets AVP (RFC 4006 §8.24, code 412). */
public interface HasCcInputOctetsAVP extends AVPContainer {

    default void setCcInputOctets(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_INPUT_OCTETS, 0), value));
    }

    default BigInteger getCcInputOctets() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_INPUT_OCTETS, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
