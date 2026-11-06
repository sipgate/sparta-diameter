package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Subscription-Id-Type AVP (RFC 4006 §8.47, code 450). */
public interface HasSubscriptionIdTypeAVP extends AVPContainer {

    default void setSubscriptionIdType(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_TYPE, 0), value));
    }

    default int getSubscriptionIdType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
