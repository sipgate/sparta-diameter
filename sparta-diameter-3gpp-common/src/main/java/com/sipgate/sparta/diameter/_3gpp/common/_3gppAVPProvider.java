package com.sipgate.sparta.diameter._3gpp.common;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides AVP definitions for common 3GPP Diameter interfaces.
 */
public final class _3gppAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // 3GPP TS 29.229, Cx and Dx interfaces
            new AVPDefinition(_3gppConstants.AVP_SUPPORTED_FEATURES, "Supported-Features",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_FEATURE_LIST_ID, "Feature-List-ID",
                Long.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_FEATURE_LIST, "Feature-List",
                Long.class, false, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.329, Sh interface
            new AVPDefinition(_3gppConstants.AVP_MSISDN, "MSISDN",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.336, S6m/S6n interfaces
            new AVPDefinition(_3gppConstants.AVP_USER_IDENTIFIER, "User-Identifier",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_EXTERNAL_IDENTIFIER, "External-Identifier",
                String.class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.272, S6a/S6d, S7a/S7d and S13/S13 interfaces
            new AVPDefinition(_3gppConstants.AVP_EPS_LOCATION_INFORMATION, "EPS-Location-Information",
                GroupedAVP.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_MME_NUMBER_FOR_MT_SMS, "MME-Number-for-MT-SMS",
                byte[].class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_SGSN_NUMBER, "SGSN-Number",
                byte[].class, false, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.173, SLh interface
            new AVPDefinition(_3gppConstants.AVP_SERVING_NODE, "Serving-Node",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.212, Gx interface (QoS AVPs reused by S6a/S6d)
            new AVPDefinition(_3gppConstants.AVP_QOS_CLASS_IDENTIFIER, "QoS-Class-Identifier",
                Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_RAT_TYPE, "RAT-Type",
                Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_ALLOCATION_RETENTION_PRIORITY, "Allocation-Retention-Priority",
                GroupedAVP.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_PRIORITY_LEVEL, "Priority-Level",
                Long.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_PRE_EMPTION_CAPABILITY, "Pre-emption-Capability",
                Integer.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_PRE_EMPTION_VULNERABILITY, "Pre-emption-Vulnerability",
                Integer.class, false, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.214, Rx interface (bandwidth AVPs reused by S6a/S6d AMBR)
            new AVPDefinition(_3gppConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, "Max-Requested-Bandwidth-DL",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_MAX_REQUESTED_BANDWIDTH_UL, "Max-Requested-Bandwidth-UL",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.061, Gi/Sgi interface (UTF8String per §16.4.7 Diameter AVP table)
            new AVPDefinition(_3gppConstants.AVP_3GPP_CHARGING_CHARACTERISTICS, "3GPP-Charging-Characteristics",
                String.class, false, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.229, Cx/Dx interface (also reused by S6a/S6d UTRAN-Vector)
            new AVPDefinition(_3gppConstants.AVP_CONFIDENTIALITY_KEY, "Confidentiality-Key",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_INTEGRITY_KEY, "Integrity-Key",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // 3GPP TS 29.338, S6c interface
            new AVPDefinition(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, "SM-Delivery-Outcome",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_ABSENT_USER_DIAGNOSTIC_SM, "AbsentUser-Diagnostic-SM",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_SMS_GMSC_ALERT_EVENT, "SMS-GMSC-Alert-Event",
                Long.class, false, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
