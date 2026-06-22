package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Emergency-Services AVP (3GPP TS 29.273, code 3370). */
public interface HasEmergencyServicesAVP extends AVPContainer {

    default void setEmergencyServices(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_EMERGENCY_SERVICES, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getEmergencyServices() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_EMERGENCY_SERVICES, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
