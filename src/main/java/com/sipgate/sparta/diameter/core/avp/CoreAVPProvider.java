package com.sipgate.sparta.diameter.core.avp;

import com.sipgate.sparta.diameter.core.DiameterConstants;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides AVP definitions for the core Diameter protocol (RFC 6733).
 */
public final class CoreAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // Core protocol AVPs
            new AVPDefinition(DiameterConstants.AVP_RESULT_CODE, "Result-Code", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_HOST, "Origin-Host", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_REALM, "Origin-Realm", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_HOST_IP_ADDRESS, "Host-IP-Address", byte[].class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_VENDOR_ID, "Vendor-Id", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_PRODUCT_NAME, "Product-Name", String.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, "Supported-Vendor-Id", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_AUTH_APPLICATION_ID, "Auth-Application-Id", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCT_APPLICATION_ID, "Acct-Application-Id", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, "Vendor-Specific-Application-Id", byte[].class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_FIRMWARE_REVISION, "Firmware-Revision", Integer.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_STATE_ID, "Origin-State-Id", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ERROR_MESSAGE, "Error-Message", String.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_FAILED_AVP, "Failed-AVP", byte[].class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_DISCONNECT_CAUSE, "Disconnect-Cause", Integer.class, true, false, 0),

            // Session Management AVPs
            new AVPDefinition(DiameterConstants.AVP_SESSION_ID, "Session-Id", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_AUTH_REQUEST_TYPE, "Auth-Request-Type", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_AUTH_GRACE_PERIOD, "Auth-Grace-Period", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_AUTH_SESSION_STATE, "Auth-Session-State", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_AUTHORIZATION_LIFETIME, "Authorization-Lifetime", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_SESSION_TIMEOUT, "Session-Timeout", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_TERMINATION_CAUSE, "Termination-Cause", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_SESSION_BINDING, "Session-Binding", Integer.class, true, false, 0),

            // Re-Auth AVPs
            new AVPDefinition(DiameterConstants.AVP_RE_AUTH_REQUEST_TYPE, "Re-Auth-Request-Type", Integer.class, true, false, 0),

            // Accounting AVPs
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE, "Accounting-Record-Type", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER, "Accounting-Record-Number", Integer.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_SESSION_ID, "Accounting-Session-Id", String.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, "Accounting-Sub-Session-Id", Long.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID, "Accounting-Multi-Session-Id", String.class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED, "Accounting-Realtime-Required", Integer.class, true, false, 0),

            // User Identity AVPs
            new AVPDefinition(DiameterConstants.AVP_USER_NAME, "User-Name", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_CLASS, "Class", byte[].class, true, false, 0),

            // Destination and Route AVPs
            new AVPDefinition(DiameterConstants.AVP_DESTINATION_HOST, "Destination-Host", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_DESTINATION_REALM, "Destination-Realm", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_ROUTE_RECORD, "Route-Record", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_PROXY_INFO, "Proxy-Info", byte[].class, false, false, 0),
            new AVPDefinition(DiameterConstants.AVP_PROXY_HOST, "Proxy-Host", String.class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_PROXY_STATE, "Proxy-State", byte[].class, true, false, 0),

            // Service and Application AVPs
            new AVPDefinition(DiameterConstants.AVP_MULTI_ROUND_TIME_OUT, "Multi-Round-Time-Out", Integer.class, true, false, 0),

            // Experimental AVPs
            new AVPDefinition(DiameterConstants.AVP_EXPERIMENTAL_RESULT, "Experimental-Result", byte[].class, true, false, 0),
            new AVPDefinition(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, "Experimental-Result-Code", Integer.class, true, false, 0)
        );
    }

    @Override
    public String getProtocolName() {
        return "Diameter Base Protocol (RFC 6733)";
    }
}
