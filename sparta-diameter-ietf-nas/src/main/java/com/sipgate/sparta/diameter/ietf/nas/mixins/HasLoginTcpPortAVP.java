package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-TCP-Port AVP (RFC 4005 §6.16.1, code 16). */
public interface HasLoginTcpPortAVP extends AVPContainer {

    default void setLoginTcpPort(final long value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_TCP_PORT, 0), value));
    }

    default long getLoginTcpPort() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_TCP_PORT, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
