package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import java.util.Date;

/** Mixin for messages carrying the Maximum-UE-Availability-Time AVP (3GPP TS 29.272 §7.3.198, code 3329). */
public interface HasMaximumUeAvailabilityTimeAVP extends AVPContainer {

    default void setMaximumUeAvailabilityTime(final Date value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_MAXIMUM_UE_AVAILABILITY_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getMaximumUeAvailabilityTime() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_MAXIMUM_UE_AVAILABILITY_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
