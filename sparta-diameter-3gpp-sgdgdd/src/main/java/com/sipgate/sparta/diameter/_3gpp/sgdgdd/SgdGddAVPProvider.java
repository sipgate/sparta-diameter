package com.sipgate.sparta.diameter._3gpp.sgdgdd;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides AVP definitions for the SGd/Gdd Diameter interface (3GPP TS 29.338 section 6).
 */
public final class SgdGddAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // Codes 3300-3307: M,V flags (mandatory=true, vendorSpecific=true)
            new AVPDefinition(SgdGddConstants.AVP_SC_ADDRESS, "SC-Address",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_RP_UI, "SM-RP-UI",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_TFR_FLAGS, "TFR-Flags",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_DELIVERY_FAILURE_CAUSE, "SM-Delivery-Failure-Cause",
                GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_ENUMERATED_DELIVERY_FAILURE_CAUSE,
                "SM-Enumerated-Delivery-Failure-Cause",
                Integer.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_DIAGNOSTIC_INFO, "SM-Diagnostic-Info",
                byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_DELIVERY_TIMER, "SM-Delivery-Timer",
                Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SM_DELIVERY_START_TIME, "SM-Delivery-Start-Time",
                Date.class, true, true, _3gppConstants.VENDOR_ID_3GPP),

            // Codes 3324-3332: V flag only (mandatory=false, vendorSpecific=true)
            new AVPDefinition(SgdGddConstants.AVP_SMSMI_CORRELATION_ID, "SMSMI-Correlation-ID",
                GroupedAVP.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_HSS_ID, "HSS-ID",
                String.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_ORIGINATING_SIP_URI, "Originating-SIP-URI",
                String.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_DESTINATION_SIP_URI, "Destination-SIP-URI",
                String.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_OFR_FLAGS, "OFR-Flags",
                Long.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_MAXIMUM_RETRANSMISSION_TIME, "Maximum-Retransmission-Time",
                Date.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_REQUESTED_RETRANSMISSION_TIME, "Requested-Retransmission-Time",
                Date.class, false, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SgdGddConstants.AVP_SMS_GMSC_ADDRESS, "SMS-GMSC-Address",
                byte[].class, false, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
