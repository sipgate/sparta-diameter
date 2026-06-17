package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Filter-Id AVP (RFC 4005 §6.7, code 11). */
public interface HasFilterIdAVP extends AVPContainer {

    default void setFilterId(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FILTER_ID, 0), value));
    }

    default String getFilterId() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FILTER_ID, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
