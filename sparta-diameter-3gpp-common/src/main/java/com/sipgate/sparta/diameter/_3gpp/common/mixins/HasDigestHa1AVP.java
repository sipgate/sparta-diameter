package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Digest-HA1 AVP (3GPP, code 121). */
public interface HasDigestHa1AVP extends AVPContainer {

    default void setDigestHa1(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_DIGEST_HA1, 0), value));
    }

    default String getDigestHa1() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_DIGEST_HA1, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
