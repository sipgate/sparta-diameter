package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import java.util.Date;

/** Mixin for messages carrying the Last-UE-Activity-Time AVP (3GPP TS 29.272 §7.3.108, code 1494). */
public interface HasLastUeActivityTimeAVP extends AVPContainer {

    default void setLastUeActivityTime(final Date value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_LAST_UE_ACTIVITY_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getLastUeActivityTime() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_LAST_UE_ACTIVITY_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
