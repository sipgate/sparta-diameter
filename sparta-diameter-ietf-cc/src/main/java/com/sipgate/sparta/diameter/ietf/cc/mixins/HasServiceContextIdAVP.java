package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Service-Context-Id AVP (RFC 4006 §8.42, code 461). */
public interface HasServiceContextIdAVP extends AVPContainer {

    default void setServiceContextId(final String value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SERVICE_CONTEXT_ID, 0), value));
    }

    default String getServiceContextId() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SERVICE_CONTEXT_ID, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
