package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Callback-Number AVP (RFC 4005 §6.2, code 19). */
public interface HasCallbackNumberAVP extends AVPContainer {

    default void setCallbackNumber(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_CALLBACK_NUMBER, 0), value));
    }

    default String getCallbackNumber() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_CALLBACK_NUMBER, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
