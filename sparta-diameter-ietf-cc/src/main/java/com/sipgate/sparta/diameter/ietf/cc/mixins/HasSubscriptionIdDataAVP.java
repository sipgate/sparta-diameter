package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Subscription-Id-Data AVP (RFC 4006 §8.48, code 444). */
public interface HasSubscriptionIdDataAVP extends AVPContainer {

    default void setSubscriptionIdData(final String value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_DATA, 0), value));
    }

    default String getSubscriptionIdData() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_DATA, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
