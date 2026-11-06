package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.math.BigInteger;

/** Mixin for messages carrying the CC-Total-Octets AVP (RFC 4006 §8.23, code 421). */
public interface HasCcTotalOctetsAVP extends AVPContainer {

    default void setCcTotalOctets(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0), value));
    }

    default BigInteger getCcTotalOctets() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
