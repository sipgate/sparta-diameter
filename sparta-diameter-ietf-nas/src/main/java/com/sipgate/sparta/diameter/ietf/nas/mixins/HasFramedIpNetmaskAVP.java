package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-IP-Netmask AVP (RFC 4005 §6.11.2, code 9). */
public interface HasFramedIpNetmaskAVP extends AVPContainer {

    default void setFramedIpNetmask(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_IP_NETMASK, 0), value));
    }

    default byte[] getFramedIpNetmask() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_IP_NETMASK, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
