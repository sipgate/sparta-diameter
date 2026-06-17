package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Routing AVP (RFC 4005 §6.10.2, code 10). */
public interface HasFramedRoutingAVP extends AVPContainer {

    default void setFramedRouting(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_ROUTING, 0), value));
    }

    default int getFramedRouting() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_ROUTING, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
