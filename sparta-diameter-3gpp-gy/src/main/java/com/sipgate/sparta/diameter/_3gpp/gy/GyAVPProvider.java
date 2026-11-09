package com.sipgate.sparta.diameter._3gpp.gy;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/**
 * Provides the Gy AVPs the online-charging PS-Information carries (3GPP TS 32.299 §7.2, TS 29.061 §16.4.7.2).
 * The 3GPP-* AVPs have the V bit set and the M bit cleared; the others set both. Also provides the AVPs that
 * GGSN/PGW-style charging clients (Open5GS) add to Credit-Control-Requests.
 */
public final class GyAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(GyConstants.AVP_3GPP_CHARGING_ID, "3GPP-Charging-Id", byte[].class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_IMSI_MCC_MNC, "3GPP-IMSI-MCC-MNC", String.class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_SGSN_MCC_MNC, "3GPP-SGSN-MCC-MNC", String.class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_RAT_TYPE, "3GPP-RAT-Type", byte[].class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_USER_LOCATION_INFO, "3GPP-User-Location-Info", byte[].class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_MS_TIMEZONE, "3GPP-MS-TimeZone", byte[].class, false, true, V),
            new AVPDefinition(GyConstants.AVP_SERVICE_INFORMATION, "Service-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_PS_INFORMATION, "PS-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_PDP_ADDRESS, "PDP-Address", InetAddress.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_PDP_TYPE, "3GPP-PDP-Type", Integer.class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_NSAPI, "3GPP-NSAPI", byte[].class, false, true, V),
            new AVPDefinition(GyConstants.AVP_3GPP_SELECTION_MODE, "3GPP-Selection-Mode", String.class, false, true, V),
            new AVPDefinition(GyConstants.AVP_GGSN_ADDRESS, "GGSN-Address", InetAddress.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_REPORTING_REASON, "Reporting-Reason", Integer.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_SGSN_ADDRESS, "SGSN-Address", InetAddress.class, true, true, V),
            new AVPDefinition(GyConstants.AVP_AOC_REQUEST_TYPE, "AoC-Request-Type", Integer.class, true, true, V)
        );
    }
}
