package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Callback-Id AVP (RFC 4005 §6.3, code 20). */
public interface HasCallbackIdAVP extends AVPContainer {

    default void setCallbackId(final String value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_CALLBACK_ID, 0), value));
    }

    default String getCallbackId() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_CALLBACK_ID, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
