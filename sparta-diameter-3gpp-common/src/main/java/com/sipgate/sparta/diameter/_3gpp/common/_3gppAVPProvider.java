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

            // 3GPP TS 29.338, S6c interface
            new AVPDefinition(_3gppConstants.AVP_SM_DELIVERY_OUTCOME, "SM-Delivery-Outcome",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(_3gppConstants.AVP_ABSENT_USER_DIAGNOSTIC_SM, "AbsentUser-Diagnostic-SM",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
