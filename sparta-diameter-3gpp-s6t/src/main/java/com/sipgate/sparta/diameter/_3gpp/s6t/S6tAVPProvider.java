package com.sipgate.sparta.diameter._3gpp.s6t;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.336 that are reused on other interfaces. */
public final class S6tAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(S6tConstants.AVP_MONITORING_EVENT_REPORT, "Monitoring-Event-Report", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SCEF_ID, "SCEF-ID", String.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_EVENT_CONFIG_STATUS, "Monitoring-Event-Config-Status", GroupedAVP.class, true, true, V)
        );
    }
}
