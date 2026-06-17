package com.sipgate.sparta.diameter._3gpp.s6t;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.336 that are reused on other interfaces. */
public final class S6tAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(S6tConstants.AVP_AESE_COMMUNICATION_PATTERN, "AESE-Communication-Pattern", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_COMMUNICATION_PATTERN_SET, "Communication-Pattern-Set", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_PERIODIC_COMMUNICATION_INDICATOR, "Periodic-Communication-Indicator", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_COMMUNICATION_DURATION_TIME, "Communication-Duration-Time", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_PERIODIC_TIME, "Periodic-time", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SCHEDULED_COMMUNICATION_TIME, "Scheduled-Communication-Time", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_STATIONARY_INDICATION, "Stationary-Indication", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_AESE_COMMUNICATION_PATTERN_CONFIG_STATUS, "AESE-Communication-Pattern-Config-Status", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_AESE_ERROR_REPORT, "AESE-Error-Report", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_EVENT_CONFIGURATION, "Monitoring-Event-Configuration", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_EVENT_REPORT, "Monitoring-Event-Report", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SCEF_REFERENCE_ID, "SCEF-Reference-ID", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SCEF_ID, "SCEF-ID", String.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SCEF_REFERENCE_ID_FOR_DELETION, "SCEF-Reference-ID-for-Deletion", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_TYPE, "Monitoring-Type", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MAXIMUM_NUMBER_OF_REPORTS, "Maximum-Number-of-Reports", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_UE_REACHABILITY_CONFIGURATION, "UE-Reachability-Configuration", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_DURATION, "Monitoring-Duration", Date.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MAXIMUM_DETECTION_TIME, "Maximum-Detection-Time", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_REACHABILITY_TYPE, "Reachability-Type", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MAXIMUM_LATENCY, "Maximum Latency", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MAXIMUM_RESPONSE_TIME, "Maximum Response Time", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_LOCATION_INFORMATION_CONFIGURATION, "Location-Information-Configuration", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONTE_LOCATION_TYPE, "MONTE-Location-Type", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_ACCURACY, "Accuracy", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_ASSOCIATION_TYPE, "Association-Type", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_ROAMING_INFORMATION, "Roaming-Information", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_REACHABILITY_INFORMATION, "Reachability-Information", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_IMEI_CHANGE, "IMEI-Change", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_MONITORING_EVENT_CONFIG_STATUS, "Monitoring-Event-Config-Status", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SUPPORTED_SERVICES, "Supported-Services", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SUPPORTED_MONITORING_EVENTS, "Supported-Monitoring-Events", BigInteger.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_CIR_FLAGS, "CIR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SERVICE_RESULT, "Service-Result", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SERVICE_RESULT_CODE, "Service-Result-Code", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_REFERENCE_ID_VALIDITY_TIME, "Reference-ID-Validity-Time", Date.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_EVENT_HANDLING, "Event-Handling", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_NIDD_AUTHORIZATION_REQUEST, "NIDD-Authorization-Request", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_NIDD_AUTHORIZATION_RESPONSE, "NIDD-Authorization-Response", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_SERVICE_REPORT, "Service-Report", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_NODE_TYPE, "Node-Type", Long.class, true, true, V),
            new AVPDefinition(S6tConstants.AVP_S6T_HSS_CAUSE, "S6t-HSS-Cause", Long.class, true, true, V)
        );
    }
}
