package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-LAT-Service AVP (RFC 4005 §6.17.1, code 34). */
public interface HasLoginLatServiceAVP extends AVPContainer {

    default void setLoginLatService(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_SERVICE, 0), value));
    }

    default byte[] getLoginLatService() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_SERVICE, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
