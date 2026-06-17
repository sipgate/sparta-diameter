package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Pool AVP (RFC 4005 §6.11.4, code 88). */
public interface HasFramedPoolAVP extends AVPContainer {

    default void setFramedPool(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_POOL, 0), value));
    }

    default byte[] getFramedPool() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_POOL, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
