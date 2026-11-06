package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Request-Type AVP (RFC 4006 §8.3, code 416). */
public interface HasCcRequestTypeAVP extends AVPContainer {

    default void setCcRequestType(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_REQUEST_TYPE, 0), value));
    }

    default int getCcRequestType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_REQUEST_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
