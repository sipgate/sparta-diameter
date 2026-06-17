package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the ARAP-Zone-Access AVP (RFC 4005 §6.14.2, code 72). */
public interface HasArapZoneAccessAVP extends AVPContainer {

    default void setArapZoneAccess(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_ARAP_ZONE_ACCESS, 0), value));
    }

    default int getArapZoneAccess() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_ARAP_ZONE_ACCESS, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
