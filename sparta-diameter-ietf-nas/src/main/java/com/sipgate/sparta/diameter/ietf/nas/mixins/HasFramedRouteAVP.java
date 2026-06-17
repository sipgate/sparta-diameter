package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Route AVP (RFC 4005 §6.11.3, code 22). */
public interface HasFramedRouteAVP extends AVPContainer {

    default void setFramedRoute(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_ROUTE, 0), value));
    }

    default String getFramedRoute() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_ROUTE, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
