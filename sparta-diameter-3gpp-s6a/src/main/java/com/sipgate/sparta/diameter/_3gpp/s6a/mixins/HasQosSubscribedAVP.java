package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the QoS-Subscribed AVP (3GPP TS 29.272 §7.3.77, code 1404). */
public interface HasQosSubscribedAVP extends AVPContainer {

    default void setQosSubscribed(final byte[] value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_QOS_SUBSCRIBED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getQosSubscribed() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_QOS_SUBSCRIBED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
