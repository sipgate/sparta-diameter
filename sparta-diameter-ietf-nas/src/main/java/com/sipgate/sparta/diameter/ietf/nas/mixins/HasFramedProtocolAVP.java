package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Protocol AVP (RFC 4005 §6.10.1, code 7). */
public interface HasFramedProtocolAVP extends AVPContainer {

    default void setFramedProtocol(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_PROTOCOL, 0), value));
    }

    default int getFramedProtocol() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_PROTOCOL, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
