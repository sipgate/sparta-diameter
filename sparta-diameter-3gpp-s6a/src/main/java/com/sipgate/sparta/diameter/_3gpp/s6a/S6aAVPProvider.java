package com.sipgate.sparta.diameter._3gpp.s6a;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides AVP definitions for the S6a/S6d Diameter interface (3GPP TS 29.272 §7.3).
 * <p>
 * Covers the in-scope subset used by the HSS (see {@code specs/s6a-s6d}). Includes every AVP that
 * can appear — directly or nested in a grouped AVP — in an in-scope command, so that a decoded
 * message resolves all definitions (an unknown M-bit AVP would otherwise fail decode with 5001).
 * AVPs reused from other specs (QoS family, MIP6/Service-Selection, MSISDN, Supported-Features,
 * Confidentiality-/Integrity-Key, …) are defined in their own modules and reused, not redefined.
 * </p>
 */
public final class S6aAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // M,V flags (mandatory=true, vendorSpecific=true)
            new AVPDefinition(S6aConstants.AVP_SUBSCRIPTION_DATA, "Subscription-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_TERMINAL_INFORMATION, "Terminal-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMEI, "IMEI", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SOFTWARE_VERSION, "Software-Version", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ULR_FLAGS, "ULR-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ULA_FLAGS, "ULA-Flags", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_VISITED_PLMN_ID, "Visited-PLMN-Id", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO,
                "Requested-EUTRAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO,
                "Requested-UTRAN-GERAN-Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, "Number-Of-Requested-Vectors", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_RE_SYNCHRONIZATION_INFO, "Re-Synchronization-Info", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IMMEDIATE_RESPONSE_PREFERRED, "Immediate-Response-Preferred", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AUTHENTICATION_INFO, "Authentication-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_E_UTRAN_VECTOR, "E-UTRAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_UTRAN_VECTOR, "UTRAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_GERAN_VECTOR, "GERAN-Vector", GroupedAVP.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_NETWORK_ACCESS_MODE, "Network-Access-Mode", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ITEM_NUMBER, "Item-Number", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CANCELLATION_TYPE, "Cancellation-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_CONTEXT_IDENTIFIER, "Context-Identifier", Long.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SUBSCRIBER_STATUS, "Subscriber-Status", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_APN_OI_REPLACEMENT, "APN-OI-Replacement", String.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR,
                "All-APN-Configurations-Included-Indicator", Integer.class, true, true, V),
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
            new AVPDefinition(S6aConstants.AVP_RAND, "RAND", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_XRES, "XRES", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_AUTN, "AUTN", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_KASME, "KASME", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_KC, "Kc", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_SRES, "SRES", byte[].class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_PDN_TYPE, "PDN-Type", Integer.class, true, true, V),
            new AVPDefinition(S6aConstants.AVP_IDR_FLAGS, "IDR-Flags", Long.class, true, true, V),

            // V flag only (mandatory=false, vendorSpecific=true)
            new AVPDefinition(S6aConstants.AVP_SIPTO_PERMISSION, "SIPTO-Permission", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_UE_SRVCC_CAPABILITY, "UE-SRVCC-Capability", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_LIPA_PERMISSION, "LIPA-Permission", Integer.class, false, true, V),
            new AVPDefinition(S6aConstants.AVP_CLR_FLAGS, "CLR-Flags", Long.class, false, true, V)
        );
    }
}
