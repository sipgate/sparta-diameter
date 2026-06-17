package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-IPX-Network AVP (RFC 4005 §6.12.1, code 23). */
public interface HasFramedIpxNetworkAVP extends AVPContainer {

    default void setFramedIpxNetwork(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPX_NETWORK, 0), value));
    }

    default String getFramedIpxNetwork() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_IPX_NETWORK, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
