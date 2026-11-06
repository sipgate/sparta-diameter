package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Called-Station-Id AVP (RFC 4005 §4.5, code 30). */
public interface HasCalledStationIdAVP extends AVPContainer {

    default void setCalledStationId(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_CALLED_STATION_ID, 0), value));
    }

    default String getCalledStationId() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_CALLED_STATION_ID, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
