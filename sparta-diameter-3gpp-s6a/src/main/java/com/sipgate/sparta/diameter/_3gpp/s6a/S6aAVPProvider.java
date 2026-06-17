package com.sipgate.sparta.diameter._3gpp.s6a;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides AVP definitions for the S6a/S6d Diameter interface (3GPP TS 29.272 §7.3).
 * <p>
 * Covers AVPs that appear at the top level of the command ABNFs in §7.2. Sub-AVPs of grouped
 * AVPs (e.g. fields inside Subscription-Data) are not declared here.
 */
public final class S6aAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // TS 29.272 §7.3 — 3GPP vendor-specific AVPs, M and V bits set
            new AVPDefinition(S6aConstants.AVP_SUBSCRIPTION_DATA, "Subscription-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TERMINAL_INFORMATION, "Terminal-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMEI, "IMEI", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SOFTWARE_VERSION, "Software-Version", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_QOS_SUBSCRIBED, "QoS-Subscribed", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ULR_FLAGS, "ULR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ULA_FLAGS, "ULA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_VISITED_PLMN_ID, "Visited-PLMN-Id", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO, "Requested-EUTRAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO, "Requested-UTRAN-GERAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, "Number-Of-Requested-Vectors", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_RE_SYNCHRONIZATION_INFO, "Re-Synchronization-Info", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMMEDIATE_RESPONSE_PREFERRED, "Immediate-Response-Preferred", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AUTHENTICATION_INFO, "Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_E_UTRAN_VECTOR, "E-UTRAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_UTRAN_VECTOR, "UTRAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NETWORK_ACCESS_MODE, "Network-Access-Mode", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ITEM_NUMBER, "Item-Number", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CANCELLATION_TYPE, "Cancellation-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_DSR_FLAGS, "DSR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_DSA_FLAGS, "DSA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CONTEXT_IDENTIFIER, "Context-Identifier", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SUBSCRIBER_STATUS, "Subscriber-Status", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_APN_OI_REPLACEMENT, "APN-OI-Replacement", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR, "All-APN-Configurations-Included-Indicator", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_APN_CONFIGURATION_PROFILE, "APN-Configuration-Profile", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_APN_CONFIGURATION, "APN-Configuration", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_EPS_SUBSCRIBED_QOS_PROFILE, "EPS-Subscribed-QoS-Profile", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED, "VPLMN-Dynamic-Address-Allowed", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ALERT_REASON, "Alert-Reason", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AMBR, "AMBR", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PDN_GW_ALLOCATION_TYPE, "PDN-GW-Allocation-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_RAT_FREQUENCY_SELECTION_PRIORITY_ID, "RAT-Frequency-Selection-Priority-ID", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IDA_FLAGS, "IDA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PUA_FLAGS, "PUA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NOR_FLAGS, "NOR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_USER_ID, "User-Id", String.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_RAND, "RAND", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_XRES, "XRES", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AUTN, "AUTN", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_KASME, "KASME", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PDN_TYPE, "PDN-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_REFERENCE, "Trace-Reference", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SS_CODE, "SS-Code", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TS_CODE, "TS-Code", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IDR_FLAGS, "IDR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED, "IMS-Voice-Over-PS-Sessions-Supported", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_HOMOGENEOUS_SUPPORT_OF_IMS_VOICE_OVER_PS_SESSIONS, "Homogeneous-Support-of-IMS-Voice-Over-PS-Sessions", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_LAST_UE_ACTIVITY_TIME, "Last-UE-Activity-Time", Date.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EPS_USER_STATE, "EPS-User-State", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_ACTIVE_APN, "Active-APN", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SIPTO_PERMISSION, "SIPTO-Permission", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_ERROR_DIAGNOSTIC, "Error-Diagnostic", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_UE_SRVCC_CAPABILITY, "UE-SRVCC-Capability", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_LIPA_PERMISSION, "LIPA-Permission", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_PUR_FLAGS, "PUR-Flags", Long.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EQUIVALENT_PLMN_LIST, "Equivalent-PLMN-List", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_CLR_FLAGS, "CLR-Flags", Long.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_LOCAL_TIME_ZONE, "Local-Time-Zone", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SMS_REGISTER_REQUEST, "SMS-Register-Request", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SGS_MME_IDENTITY, "SGs-MME-Identity", String.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_COUPLED_NODE_DIAMETER_ID, "Coupled-Node-Diameter-ID", String.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_RESET_ID, "Reset-ID", byte[].class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_ADJACENT_PLMNS, "Adjacent-PLMNs", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_AIR_FLAGS, "AIR-Flags", Long.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_UE_USAGE_TYPE, "UE-Usage-Type", Long.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SUBSCRIPTION_DATA_DELETION, "Subscription-Data-Deletion", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EDRX_RELATED_RAT, "eDRX-Related-RAT", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SF_ULR_TIMESTAMP, "SF-ULR-Timestamp", Date.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_SF_PROVISIONAL_INDICATION, "SF-Provisional-Indication", Integer.class, false, true, V),

            // TS 29.272 §7.3 — additional mandatory (M-bit) AVPs
            new AVPDefinition(S6aConstants.AVP_GERAN_VECTOR, "GERAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_HPLMN_ODB, "HPLMN-ODB", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_OPERATOR_DETERMINED_BARRING, "Operator-Determined-Barring", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ACCESS_RESTRICTION_DATA, "Access-Restriction-Data", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_STN_SR, "STN-SR", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CSG_SUBSCRIPTION_DATA, "CSG-Subscription-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CSG_ID, "CSG-Id", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_EXPIRATION_DATE, "Expiration-Date", Date.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_EQUIPMENT_STATUS, "Equipment-Status", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REGIONAL_SUBSCRIPTION_ZONE_CODE, "Regional-Subscription-Zone-Code", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_COLLECTION_ENTITY, "Trace-Collection-Entity", InetAddress.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_KC, "Kc", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SRES, "SRES", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ROAMING_RESTRICTED_DUE_TO_UNSUPPORTED_FEATURE, "Roaming-Restricted-Due-To-Unsupported-Feature", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_DATA, "Trace-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_DEPTH, "Trace-Depth", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_NE_TYPE_LIST, "Trace-NE-Type-List", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_INTERFACE_LIST, "Trace-Interface-List", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_EVENT_LIST, "Trace-Event-List", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_OMC_ID, "OMC-Id", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_GPRS_SUBSCRIPTION_DATA, "GPRS-Subscription-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_COMPLETE_DATA_LIST_INCLUDED_INDICATOR, "Complete-Data-List-Included-Indicator", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PDP_CONTEXT, "PDP-Context", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PDP_TYPE, "PDP-Type", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_3GPP2_MEID, "3GPP2-MEID", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SPECIFIC_APN_INFO, "Specific-APN-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_LCS_INFO, "LCS-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_GMLC_NUMBER, "GMLC-Number", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_LCS_PRIVACYEXCEPTION, "LCS-PrivacyException", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SS_STATUS, "SS-Status", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NOTIFICATION_TO_UE_USER, "Notification-To-UE-User", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_EXTERNAL_CLIENT, "External-Client", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CLIENT_IDENTITY, "Client-Identity", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_GMLC_RESTRICTION, "GMLC-Restriction", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PLMN_CLIENT, "PLMN-Client", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SERVICE_TYPE, "Service-Type", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SERVICETYPEIDENTITY, "ServiceTypeIdentity", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_MO_LR, "MO-LR", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TELESERVICE_LIST, "Teleservice-List", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CALL_BARRING_INFO, "Call-Barring-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SGSN_NUMBER, "SGSN-Number", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_UVR_FLAGS, "UVR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_UVA_FLAGS, "UVA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_VPLMN_CSG_SUBSCRIPTION_DATA, "VPLMN-CSG-Subscription-Data", GroupedAVP.class, true, true, V),

            new AVPDefinition(S6aConstants.AVP_MAXIMUM_UE_AVAILABILITY_TIME, "Maximum-UE-Availability-Time", Date.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EMERGENCY_SERVICES, "Emergency-Services", Integer.class, false, true, V),

            new AVPDefinition(S6aConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, "Max-Requested-Bandwidth-DL", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_MAX_REQUESTED_BANDWIDTH_UL, "Max-Requested-Bandwidth-UL", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_QOS_CLASS_IDENTIFIER, "QoS-Class-Identifier", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ALLOCATION_RETENTION_PRIORITY, "Allocation-Retention-Priority", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PRIORITY_LEVEL, "Priority-Level", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PRE_EMPTION_CAPABILITY, "Pre-emption-Capability", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PRE_EMPTION_VULNERABILITY, "Pre-emption-Vulnerability", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CHARGING_CHARACTERISTICS_3GPP, "3GPP-Charging-Characteristics", byte[].class, true, true, V)
        );
    }
}
