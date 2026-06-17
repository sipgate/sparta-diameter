package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Configuration-Token AVP (RFC 4005 §6.8, code 78). */
public interface HasConfigurationTokenAVP extends AVPContainer {

    default void setConfigurationToken(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_CONFIGURATION_TOKEN, 0), value));
    }

    default byte[] getConfigurationToken() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_CONFIGURATION_TOKEN, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
