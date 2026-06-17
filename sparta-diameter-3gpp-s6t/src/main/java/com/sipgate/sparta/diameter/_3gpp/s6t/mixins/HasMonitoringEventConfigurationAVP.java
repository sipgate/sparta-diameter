package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Monitoring-Event-Configuration AVP (3GPP TS 29.336 §8.4.2, code 3122). */
public interface HasMonitoringEventConfigurationAVP extends AVPContainer {

    default void setMonitoringEventConfiguration(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_MONITORING_EVENT_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getMonitoringEventConfiguration() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_MONITORING_EVENT_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
