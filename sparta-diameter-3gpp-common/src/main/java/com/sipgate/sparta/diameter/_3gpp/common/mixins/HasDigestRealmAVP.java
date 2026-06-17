package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Digest-Realm AVP (3GPP, code 104). */
public interface HasDigestRealmAVP extends AVPContainer {

    default void setDigestRealm(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_DIGEST_REALM, 0), value));
    }

    default String getDigestRealm() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_DIGEST_REALM, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
