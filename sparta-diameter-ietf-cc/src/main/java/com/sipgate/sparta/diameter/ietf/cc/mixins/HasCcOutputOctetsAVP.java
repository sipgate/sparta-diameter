package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.math.BigInteger;

/** Mixin for messages carrying the CC-Output-Octets AVP (RFC 4006 §8.25, code 414). */
public interface HasCcOutputOctetsAVP extends AVPContainer {

    default void setCcOutputOctets(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_OUTPUT_OCTETS, 0), value));
    }

    default BigInteger getCcOutputOctets() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_OUTPUT_OCTETS, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
