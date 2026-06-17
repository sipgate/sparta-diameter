package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-LAT-Node AVP (RFC 4005 §6.17.2, code 35). */
public interface HasLoginLatNodeAVP extends AVPContainer {

    default void setLoginLatNode(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_NODE, 0), value));
    }

    default byte[] getLoginLatNode() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_NODE, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
