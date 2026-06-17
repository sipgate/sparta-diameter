package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-IPv6-Pool AVP (RFC 4005 §6.11.8, code 100). */
public interface HasFramedIpv6PoolAVP extends AVPContainer {

    default void setFramedIpv6Pool(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPV6_POOL, 0), value));
    }

    default byte[] getFramedIpv6Pool() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPV6_POOL, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
