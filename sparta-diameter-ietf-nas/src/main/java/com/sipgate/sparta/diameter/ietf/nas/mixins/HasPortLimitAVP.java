package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Port-Limit AVP (RFC 4005 §6.5, code 62). */
public interface HasPortLimitAVP extends AVPContainer {

    default void setPortLimit(final long value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_PORT_LIMIT, 0), value));
    }

    default long getPortLimit() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_PORT_LIMIT, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
