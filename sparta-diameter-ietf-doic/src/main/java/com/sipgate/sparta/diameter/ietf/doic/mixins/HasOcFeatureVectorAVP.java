package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

import java.math.BigInteger;

/** Mixin for containers carrying the OC-Feature-Vector AVP (RFC 7683 §7.2, code 622). */
public interface HasOcFeatureVectorAVP extends AVPContainer {

    default void setOcFeatureVector(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_FEATURE_VECTOR, 0), value));
    }

    default BigInteger getOcFeatureVector() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_FEATURE_VECTOR, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
