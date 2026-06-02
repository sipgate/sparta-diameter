package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Emergency-Services AVP (3GPP TS 29.272 §7.3.221, code 3370). */
public interface HasEmergencyServicesAVP extends AVPContainer {

    default void setEmergencyServices(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_EMERGENCY_SERVICES, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getEmergencyServices() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_EMERGENCY_SERVICES, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
