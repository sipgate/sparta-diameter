package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Idle-Timeout AVP (RFC 4005 §6.4, code 28). */
public interface HasIdleTimeoutAVP extends AVPContainer {

    default void setIdleTimeout(final long value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_IDLE_TIMEOUT, 0), value));
    }

    default long getIdleTimeout() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_IDLE_TIMEOUT, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
