package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-Service AVP (RFC 4005 §6.15.3, code 15). */
public interface HasLoginServiceAVP extends AVPContainer {

    default void setLoginService(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_SERVICE, 0), value));
    }

    default int getLoginService() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_SERVICE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
