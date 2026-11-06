package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Direct-Debiting-Failure-Handling AVP (RFC 4006 §8.15, code 428). */
public interface HasDirectDebitingFailureHandlingAVP extends AVPContainer {

    default void setDirectDebitingFailureHandling(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_DIRECT_DEBITING_FAILURE_HANDLING, 0), value));
    }

    default int getDirectDebitingFailureHandling() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_DIRECT_DEBITING_FAILURE_HANDLING, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
