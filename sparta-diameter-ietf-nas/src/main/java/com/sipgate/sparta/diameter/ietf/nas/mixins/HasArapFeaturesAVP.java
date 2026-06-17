package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the ARAP-Features AVP (RFC 4005 §6.14.1, code 71). */
public interface HasArapFeaturesAVP extends AVPContainer {

    default void setArapFeatures(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_ARAP_FEATURES, 0), value));
    }

    default byte[] getArapFeatures() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_ARAP_FEATURES, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
