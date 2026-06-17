package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Subscriber-Status AVP (3GPP, code 1424). */
public interface HasSubscriberStatusAVP extends AVPContainer {

    default void setSubscriberStatus(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SUBSCRIBER_STATUS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSubscriberStatus() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SUBSCRIBER_STATUS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
