package com.sipgate.sparta.diameter.ietf.mip6.split.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.mip6.split.Mip6SplitConstants;

import java.math.BigInteger;

/** Mixin for containers carrying the MIP6-Feature-Vector AVP (RFC 5447 §4.2.5, code 124). */
public interface HasMip6FeatureVectorAVP extends AVPContainer {

    default void setMip6FeatureVector(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(Mip6SplitConstants.AVP_MIP6_FEATURE_VECTOR, 0), value));
    }

    default BigInteger getMip6FeatureVector() {
        final var avp = findAVP(new AVPKey(Mip6SplitConstants.AVP_MIP6_FEATURE_VECTOR, 0));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
