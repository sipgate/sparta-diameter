package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-IP-Host AVP (RFC 4005 §6.15.1, code 14). */
public interface HasLoginIpHostAVP extends AVPContainer {

    default void setLoginIpHost(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_IP_HOST, 0), value));
    }

    default byte[] getLoginIpHost() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_IP_HOST, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
