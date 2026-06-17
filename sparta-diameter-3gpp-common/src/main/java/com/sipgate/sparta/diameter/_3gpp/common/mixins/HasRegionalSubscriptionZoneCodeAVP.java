package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Regional-Subscription-Zone-Code AVP (3GPP, code 1446). */
public interface HasRegionalSubscriptionZoneCodeAVP extends AVPContainer {

    default void setRegionalSubscriptionZoneCode(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_REGIONAL_SUBSCRIPTION_ZONE_CODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getRegionalSubscriptionZoneCode() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_REGIONAL_SUBSCRIPTION_ZONE_CODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
