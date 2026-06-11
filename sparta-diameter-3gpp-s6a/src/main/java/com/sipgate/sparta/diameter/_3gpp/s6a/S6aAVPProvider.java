package com.sipgate.sparta.diameter._3gpp.s6a;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

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
            new AVPDefinition(S6aConstants.AVP_ULR_FLAGS, "ULR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ULA_FLAGS, "ULA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_VISITED_PLMN_ID, "Visited-PLMN-Id", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO, "Requested-EUTRAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO, "Requested-UTRAN-GERAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AUTHENTICATION_INFO, "Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CANCELLATION_TYPE, "Cancellation-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_DSR_FLAGS, "DSR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_DSA_FLAGS, "DSA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CONTEXT_IDENTIFIER, "Context-Identifier", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ALERT_REASON, "Alert-Reason", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IDA_FLAGS, "IDA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PUA_FLAGS, "PUA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NOR_FLAGS, "NOR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_USER_ID, "User-Id", String.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_TRACE_REFERENCE, "Trace-Reference", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SS_CODE, "SS-Code", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TS_CODE, "TS-Code", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IDR_FLAGS, "IDR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED, "IMS-Voice-Over-PS-Sessions-Supported", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_HOMOGENEOUS_SUPPORT_OF_IMS_VOICE_OVER_PS_SESSIONS, "Homogeneous-Support-of-IMS-Voice-Over-PS-Sessions", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_LAST_UE_ACTIVITY_TIME, "Last-UE-Activity-Time", Date.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EPS_USER_STATE, "EPS-User-State", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_ERROR_DIAGNOSTIC, "Error-Diagnostic", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_UE_SRVCC_CAPABILITY, "UE-SRVCC-Capability", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_ACTIVE_APN, "Active-APN", GroupedAVP.class, false, true, V),
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

            new AVPDefinition(S6aConstants.AVP_SUPPORTED_SERVICES, "Supported-Services", GroupedAVP.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_MAXIMUM_UE_AVAILABILITY_TIME, "Maximum-UE-Availability-Time", Date.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_EMERGENCY_SERVICES, "Emergency-Services", Integer.class, false, true, V)
        );
    }
}
