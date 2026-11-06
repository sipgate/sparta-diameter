package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.math.BigInteger;

/** Mixin for messages carrying the CC-Service-Specific-Units AVP (RFC 4006 §8.26, code 417). */
public interface HasCcServiceSpecificUnitsAVP extends AVPContainer {

    default void setCcServiceSpecificUnits(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_SERVICE_SPECIFIC_UNITS, 0), value));
    }

    default BigInteger getCcServiceSpecificUnits() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_SERVICE_SPECIFIC_UNITS, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
