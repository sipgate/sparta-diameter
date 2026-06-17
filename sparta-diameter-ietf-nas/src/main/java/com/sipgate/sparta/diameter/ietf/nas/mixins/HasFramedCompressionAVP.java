package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Compression AVP (RFC 4005 §6.10.4, code 13). */
public interface HasFramedCompressionAVP extends AVPContainer {

    default void setFramedCompression(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_COMPRESSION, 0), value));
    }

    default int getFramedCompression() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_COMPRESSION, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
