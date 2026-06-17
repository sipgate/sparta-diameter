package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the QoS-Subscribed AVP (3GPP, code 1404). */
public interface HasQosSubscribedAVP extends AVPContainer {

    default void setQosSubscribed(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_QOS_SUBSCRIBED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getQosSubscribed() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_QOS_SUBSCRIBED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
