package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-IPv6-Host AVP (RFC 4005 §6.15.2, code 98). */
public interface HasLoginIpv6HostAVP extends AVPContainer {

    default void setLoginIpv6Host(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_IPV6_HOST, 0), value));
    }

    default byte[] getLoginIpv6Host() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_IPV6_HOST, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
