package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Credit-Control AVP (RFC 4006 §8.13, code 426). */
public interface HasCreditControlAVP extends AVPContainer {

    default void setCreditControl(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CREDIT_CONTROL, 0), value));
    }

    default int getCreditControl() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CREDIT_CONTROL, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
