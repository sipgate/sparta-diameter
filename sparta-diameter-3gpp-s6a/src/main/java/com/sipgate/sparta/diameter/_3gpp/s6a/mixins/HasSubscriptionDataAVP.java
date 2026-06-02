package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying a Subscription-Data grouped AVP (3GPP TS 29.272 §7.3.2, code 1400). */
public interface HasSubscriptionDataAVP extends AVPContainer {

    default void setSubscriptionData(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SUBSCRIPTION_DATA, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getSubscriptionData() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SUBSCRIPTION_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
