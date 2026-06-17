package com.sipgate.sparta.diameter._3gpp.gx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.212 (Gx) that are reused on other interfaces. */
public final class GxAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(GxConstants.AVP_RAT_TYPE, "RAT-Type", Integer.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_ACCESS_NETWORK_CHARGING_IDENTIFIER_GX, "Access-Network-Charging-Identifier-Gx", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_ALLOCATION_RETENTION_PRIORITY, "Allocation-Retention-Priority", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_BEARER_CONTROL_MODE, "Bearer-Control-Mode", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_BEARER_IDENTIFIER, "Bearer-Identifier", byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_BEARER_OPERATION, "Bearer-Operation", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_BEARER_USAGE, "Bearer-Usage", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_BASE_NAME, "Charging-Rule-Base-Name", String.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_DEFINITION, "Charging-Rule-Definition", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_INSTALL, "Charging-Rule-Install", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_NAME, "Charging-Rule-Name", byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_REMOVE, "Charging-Rule-Remove", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_CHARGING_RULE_REPORT, "Charging-Rule-Report", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_EVENT_TRIGGER, "Event-Trigger", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_GUARANTEED_BITRATE_DL, "Guaranteed-Bitrate-DL", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_GUARANTEED_BITRATE_UL, "Guaranteed-Bitrate-UL", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_IP_CAN_TYPE, "IP-CAN-Type", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_METERING_METHOD, "Metering-Method", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_NETWORK_REQUEST_SUPPORT, "Network-Request-Support", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_OFFLINE, "Offline", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_ONLINE, "Online", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_PCC_RULE_STATUS, "PCC-Rule-Status", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_PRE_EMPTION_CAPABILITY, "Pre-emption-Capability", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_PRE_EMPTION_VULNERABILITY, "Pre-emption-Vulnerability", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_PRECEDENCE, "Precedence", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_PRIORITY_LEVEL, "Priority-Level", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_QOS_CLASS_IDENTIFIER, "QoS-Class-Identifier", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_QOS_INFORMATION, "QoS-Information", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_QOS_NEGOTIATION, "QoS-Negotiation", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_QOS_UPGRADE, "QoS-Upgrade", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_REPORTING_LEVEL, "Reporting-Level", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_REVALIDATION_TIME, "Revalidation-Time", Date.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_RULE_ACTIVATION_TIME, "Rule-Activation-Time", Date.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_RULE_DEACTIVATION_TIME, "Rule-Deactivation-Time", Date.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_RULE_FAILURE_CODE, "Rule-Failure-Code", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_SESSION_RELEASE_CAUSE, "Session-Release-Cause", Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_TFT_FILTER, "TFT-Filter", String.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_TFT_PACKET_FILTER_INFORMATION, "TFT-Packet-Filter-Information", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(GxConstants.AVP_TOS_TRAFFIC_CLASS, "ToS-Traffic-Class", byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
