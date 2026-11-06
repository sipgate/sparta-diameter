package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Credit-Control-Failure-Handling AVP (RFC 4006 §8.14, code 427). */
public interface HasCreditControlFailureHandlingAVP extends AVPContainer {

    default void setCreditControlFailureHandling(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CREDIT_CONTROL_FAILURE_HANDLING, 0), value));
    }

    default int getCreditControlFailureHandling() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CREDIT_CONTROL_FAILURE_HANDLING, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
