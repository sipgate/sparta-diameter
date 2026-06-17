package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Digest-QoP AVP (3GPP, code 110). */
public interface HasDigestQopAVP extends AVPContainer {

    default void setDigestQop(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_DIGEST_QOP, 0), value));
    }

    default String getDigestQop() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_DIGEST_QOP, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
