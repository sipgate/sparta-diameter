package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Correlation-Id AVP (RFC 4006 §8.1, code 411). */
public interface HasCcCorrelationIdAVP extends AVPContainer {

    default void setCcCorrelationId(final byte[] value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_CORRELATION_ID, 0), value));
    }

    default byte[] getCcCorrelationId() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_CORRELATION_ID, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
