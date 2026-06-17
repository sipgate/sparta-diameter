package com.sipgate.sparta.diameter._3gpp.rx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.214 (Rx) that are reused on other interfaces. */
public final class RxAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(RxConstants.AVP_ABORT_CAUSE, "Abort-Cause", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_ACCESS_NETWORK_CHARGING_ADDRESS, "Access-Network-Charging-Address", InetAddress.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_ACCESS_NETWORK_CHARGING_IDENTIFIER, "Access-Network-Charging-Identifier", GroupedAVP.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_ACCESS_NETWORK_CHARGING_IDENTIFIER_VALUE, "Access-Network-Charging-Identifier-Value", byte[].class, true, true, V),
            new AVPDefinition(RxConstants.AVP_AF_APPLICATION_IDENTIFIER, "AF-Application-Identifier", byte[].class, true, true, V),
            new AVPDefinition(RxConstants.AVP_AF_CHARGING_IDENTIFIER, "AF-Charging-Identifier", byte[].class, true, true, V),
            new AVPDefinition(RxConstants.AVP_FLOW_DESCRIPTION, "Flow-Description", String.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_FLOW_NUMBER, "Flow-Number", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_FLOWS, "Flows", GroupedAVP.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_FLOW_STATUS, "Flow-Status", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_FLOW_USAGE, "Flow-Usage", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_SPECIFIC_ACTION, "Specific-Action", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, "Max-Requested-Bandwidth-DL", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MAX_REQUESTED_BANDWIDTH_UL, "Max-Requested-Bandwidth-UL", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MEDIA_COMPONENT_DESCRIPTION, "Media-Component-Description", GroupedAVP.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MEDIA_COMPONENT_NUMBER, "Media-Component-Number", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MEDIA_SUB_COMPONENT, "Media-Sub-Component", GroupedAVP.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MEDIA_TYPE, "Media-Type", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_RR_BANDWIDTH, "RR-Bandwidth", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_RS_BANDWIDTH, "RS-Bandwidth", Long.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_SIP_FORKING_INDICATION, "SIP-Forking-Indication", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_CODEC_DATA, "Codec-Data", byte[].class, true, true, V),
            new AVPDefinition(RxConstants.AVP_SERVICE_URN, "Service-URN", byte[].class, true, true, V),
            new AVPDefinition(RxConstants.AVP_ACCEPTABLE_SERVICE_INFO, "Acceptable-Service-Info", GroupedAVP.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_SERVICE_INFO_STATUS, "Service-Info-Status", Integer.class, true, true, V),
            new AVPDefinition(RxConstants.AVP_MPS_IDENTIFIER, "MPS-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_AF_SIGNALLING_PROTOCOL, "AF-Signalling-Protocol", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SPONSORED_CONNECTIVITY_DATA, "Sponsored-Connectivity-Data", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SPONSOR_IDENTITY, "Sponsor-Identity", String.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_APPLICATION_SERVICE_PROVIDER_IDENTITY, "Application-Service-Provider-Identity", String.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_RX_REQUEST_TYPE, "Rx-Request-Type", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MIN_REQUESTED_BANDWIDTH_DL, "Min-Requested-Bandwidth-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MIN_REQUESTED_BANDWIDTH_UL, "Min-Requested-Bandwidth-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_REQUIRED_ACCESS_INFO, "Required-Access-Info", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_IP_DOMAIN_ID, "IP-Domain-Id", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_GCS_IDENTIFIER, "GCS-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SHARING_KEY_DL, "Sharing-Key-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SHARING_KEY_UL, "Sharing-Key-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_RETRY_INTERVAL, "Retry-Interval", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SPONSORING_ACTION, "Sponsoring-Action", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MAX_SUPPORTED_BANDWIDTH_DL, "Max-Supported-Bandwidth-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MAX_SUPPORTED_BANDWIDTH_UL, "Max-Supported-Bandwidth-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MIN_DESIRED_BANDWIDTH_DL, "Min-Desired-Bandwidth-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MIN_DESIRED_BANDWIDTH_UL, "Min-Desired-Bandwidth-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MCPTT_IDENTIFIER, "MCPTT-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SERVICE_AUTHORIZATION_INFO, "Service-Authorization-Info", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MEDIA_COMPONENT_STATUS, "Media-Component-Status", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_PRIORITY_SHARING_INDICATOR, "Priority-Sharing-Indicator", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_AF_REQUESTED_DATA, "AF-Requested-Data", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_CONTENT_VERSION, "Content-Version", BigInteger.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_PRE_EMPTION_CONTROL_INFO, "Pre-emption-Control-Info", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MAX_REQUESTED_BW_DL, "Extended-Max-Requested-BW-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MAX_REQUESTED_BW_UL, "Extended-Max-Requested-BW-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MAX_SUPPORTED_BW_DL, "Extended-Max-Supported-BW-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MAX_SUPPORTED_BW_UL, "Extended-Max-Supported-BW-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MIN_DESIRED_BW_DL, "Extended-Min-Desired-BW-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MIN_DESIRED_BW_UL, "Extended-Min-Desired-BW-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MIN_REQUESTED_BW_DL, "Extended-Min-Requested-BW-DL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_EXTENDED_MIN_REQUESTED_BW_UL, "Extended-Min-Requested-BW-UL", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MCVIDEO_IDENTIFIER, "MCVideo-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_IMS_CONTENT_IDENTIFIER, "IMS-Content-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_IMS_CONTENT_TYPE, "IMS-Content-Type", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_CALLEE_INFORMATION, "Callee-Information", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_FLUS_IDENTIFIER, "FLUS-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_DESIRED_MAX_LATENCY, "Desired-Max-Latency", Float.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_DESIRED_MAX_LOSS, "Desired-Max-Loss", Float.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_NID, "NID", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MA_INFORMATION, "MA-Information", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MA_INFORMATION_ACTION, "MA-Information-Action", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_5GS_RAN_NAS_RELEASE_CAUSE, "5GS-RAN-NAS-Release-Cause", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_5GMM_CAUSE, "5GMM-Cause", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_5GSM_CAUSE, "5GSM-Cause", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_NGAP_CAUSE, "NGAP-Cause", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_NGAP_GROUP, "NGAP-Group", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_NGAP_VALUE, "NGAP-Value", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_WIRELINE_USER_LOCATION_INFO, "Wireline-User-Location-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_HFC_NODE_IDENTIFIER, "HFC-Node-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_GLI_IDENTIFIER, "GLI-Identifier", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_LINE_TYPE, "Line-Type", Long.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_MPS_ACTION, "MPS-Action", Integer.class, false, true, V),
            new AVPDefinition(RxConstants.AVP_SERVING_SATELLITE_IDENTITY, "Serving-Satellite-Identity", byte[].class, false, true, V),
            new AVPDefinition(RxConstants.AVP_PC_SESSION_RECOVERY_STATUS, "PC-Session-Recovery-Status", Integer.class, false, true, V)
        );
    }
}
