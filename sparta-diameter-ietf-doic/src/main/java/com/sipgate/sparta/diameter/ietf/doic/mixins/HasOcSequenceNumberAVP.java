package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

import java.math.BigInteger;

/** Mixin for containers carrying the OC-Sequence-Number AVP (RFC 7683 §7.4, code 624). */
public interface HasOcSequenceNumberAVP extends AVPContainer {

    default void setOcSequenceNumber(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_SEQUENCE_NUMBER, 0), value));
    }

    default BigInteger getOcSequenceNumber() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_SEQUENCE_NUMBER, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
