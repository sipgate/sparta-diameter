package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import java.util.List;

/** Mixin for messages carrying the VPLMN-CSG-Subscription-Data AVP (3GPP TS 29.272, code 1641). */
public interface HasVplmnCsgSubscriptionDataAVP extends AVPContainer {

    default void setVplmnCsgSubscriptionData(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_VPLMN_CSG_SUBSCRIPTION_DATA, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getVplmnCsgSubscriptionData() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_VPLMN_CSG_SUBSCRIPTION_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
