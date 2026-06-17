package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.Date;

/** Mixin for messages carrying the Monitoring-Duration AVP (3GPP TS 29.336 §8.4.10, code 3130). */
public interface HasMonitoringDurationAVP extends AVPContainer {

    default void setMonitoringDuration(final Date value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_MONITORING_DURATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getMonitoringDuration() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_MONITORING_DURATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
