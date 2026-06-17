package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Service-ID AVP (3GPP, code 3103). */
public interface HasServiceIdAVP extends AVPContainer {

    default void setServiceId(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SERVICE_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getServiceId() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SERVICE_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
