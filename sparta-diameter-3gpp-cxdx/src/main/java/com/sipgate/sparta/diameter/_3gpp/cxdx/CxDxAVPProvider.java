package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides AVP definitions for the Cx/Dx Diameter interface (3GPP TS 29.229 §6.3).
 */
public final class CxDxAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // Codes 600-626: M,V (mandatory=true)
            new AVPDefinition(CxDxConstants.AVP_VISITED_NETWORK_IDENTIFIER, "Visited-Network-Identifier", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_PUBLIC_IDENTITY, "Public-Identity", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SERVER_NAME, "Server-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SERVER_CAPABILITIES, "Server-Capabilities", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_MANDATORY_CAPABILITY, "Mandatory-Capability", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_OPTIONAL_CAPABILITY, "Optional-Capability", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_USER_DATA, "User-Data", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_NUMBER_AUTH_ITEMS, "SIP-Number-Auth-Items", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATION_SCHEME, "SIP-Authentication-Scheme", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATE, "SIP-Authenticate", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHORIZATION, "SIP-Authorization", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATION_CONTEXT, "SIP-Authentication-Context", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, "SIP-Auth-Data-Item", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_ITEM_NUMBER, "SIP-Item-Number", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SERVER_ASSIGNMENT_TYPE, "Server-Assignment-Type", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_DEREGISTRATION_REASON, "Deregistration-Reason", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_REASON_CODE, "Reason-Code", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_REASON_INFO, "Reason-Info", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_CHARGING_INFORMATION, "Charging-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME, "Primary-Event-Charging-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME, "Secondary-Event-Charging-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, "Primary-Charging-Collection-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME, "Secondary-Charging-Collection-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_USER_AUTHORIZATION_TYPE, "User-Authorization-Type", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_USER_DATA_ALREADY_AVAILABLE, "User-Data-Already-Available", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_CONFIDENTIALITY_KEY, "Confidentiality-Key", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_INTEGRITY_KEY, "Integrity-Key", byte[].class, true, true, V),

            // Codes 632-666: V only (mandatory=false)
            new AVPDefinition(CxDxConstants.AVP_ASSOCIATED_IDENTITIES, "Associated-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ORIGINATING_REQUEST, "Originating-Request", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_WILDCARDED_PUBLIC_IDENTITY, "Wildcarded-Public-Identity", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, "SIP-Digest-Authenticate", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_LOOSE_ROUTE_INDICATION, "Loose-Route-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SCSCF_RESTORATION_INFO, "SCSCF-Restoration-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PATH, "Path", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_CONTACT, "Contact", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SUBSCRIPTION_INFO, "Subscription-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_CALL_ID_SIP_HEADER, "Call-ID-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_FROM_SIP_HEADER, "From-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_TO_SIP_HEADER, "To-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RECORD_ROUTE, "Record-Route", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ASSOCIATED_REGISTERED_IDENTITIES, "Associated-Registered-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_MULTIPLE_REGISTRATION_INDICATION, "Multiple-Registration-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RESTORATION_INFO, "Restoration-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SESSION_PRIORITY, "Session-Priority", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION, "Identity-with-Emergency-Registration", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIVILEDGED_SENDER_INDICATION, "Priviledged-Sender-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_INITIAL_CSEQ_SEQUENCE_NUMBER, "Initial-CSeq-Sequence-Number", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SAR_FLAGS, "SAR-Flags", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALLOWED_WAF_WWSF_IDENTITIES, "Allowed-WAF-WWSF-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_WEBRTC_AUTHENTICATION_FUNCTION_NAME, "WebRTC-Authentication-Function-Name", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_WEBRTC_WEB_SERVER_FUNCTION_NAME, "WebRTC-Web-Server-Function-Name", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RTR_FLAGS, "RTR-Flags", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_SUBSCRIPTION_INFO, "P-CSCF-Subscription-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_REGISTRATION_TIME_OUT, "Registration-Time-Out", Date.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALTERNATE_DIGEST_ALGORITHM, "Alternate-Digest-Algorithm", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALTERNATE_DIGEST_HA1, "Alternate-Digest-HA1", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_FAILED_PCSCF, "Failed-PCSCF", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_FQDN, "PCSCF-FQDN", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_IP_ADDRESS, "PCSCF-IP-Address", InetAddress.class, false, true, V),

            // re-used AVPs in Cx/Dx base on Radius Digest Authentication (RFC 5090)
            new AVPDefinition(CxDxConstants.AVP_DIGEST_REALM, "Digest-Realm", String.class, true, false, 0),
            new AVPDefinition(CxDxConstants.AVP_DIGEST_QOP, "Digest-QoP", String.class, true, false, 0),
            new AVPDefinition(CxDxConstants.AVP_DIGEST_ALGORITHM, "Digest-Algorithm", String.class, true, false, 0),
            new AVPDefinition(CxDxConstants.AVP_DIGEST_HA1, "Digest-HA1", String.class, true, false, 0)
        );
    }
}
