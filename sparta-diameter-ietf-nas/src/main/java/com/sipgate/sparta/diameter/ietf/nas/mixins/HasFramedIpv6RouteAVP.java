package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-IPv6-Route AVP (RFC 4005 §6.11.7, code 99). */
public interface HasFramedIpv6RouteAVP extends AVPContainer {

    default void setFramedIpv6Route(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPV6_ROUTE, 0), value));
    }

    default String getFramedIpv6Route() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPV6_ROUTE, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
