package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-MTU AVP (RFC 4005 §6.10.3, code 12). */
public interface HasFramedMtuAVP extends AVPContainer {

    default void setFramedMtu(final long value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_MTU, 0), value));
    }

    default long getFramedMtu() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_MTU, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
