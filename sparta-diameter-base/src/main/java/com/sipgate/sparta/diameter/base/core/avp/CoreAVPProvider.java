package com.sipgate.sparta.diameter.base.core.avp;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.math.BigInteger;

/**
 * Provides AVP definitions for the core Diameter protocol (RFC 6733).
 */
public final class CoreAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // Core protocol AVPs
            new AVPDefinition(DiameterConstants.AVP_RESULT_CODE, "Result-Code", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_HOST, "Origin-Host", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_REALM, "Origin-Realm", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_HOST_IP_ADDRESS, "Host-IP-Address", InetAddress.class, true, false, 0), // Address
            new AVPDefinition(DiameterConstants.AVP_VENDOR_ID, "Vendor-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_PRODUCT_NAME, "Product-Name", String.class, false, false, 0), // UTF8String
            new AVPDefinition(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, "Supported-Vendor-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_AUTH_APPLICATION_ID, "Auth-Application-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_ACCT_APPLICATION_ID, "Acct-Application-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, "Vendor-Specific-Application-Id", GroupedAVP.class, true, false, 0), // Grouped
            new AVPDefinition(DiameterConstants.AVP_FIRMWARE_REVISION, "Firmware-Revision", Long.class, false, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_ORIGIN_STATE_ID, "Origin-State-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_ERROR_MESSAGE, "Error-Message", String.class, false, false, 0), // UTF8String
            new AVPDefinition(DiameterConstants.AVP_FAILED_AVP, "Failed-AVP", GroupedAVP.class, true, false, 0), // Grouped
            new AVPDefinition(DiameterConstants.AVP_DISCONNECT_CAUSE, "Disconnect-Cause", Integer.class, true, false, 0), // Enumerated

            // Session Management AVPs
            new AVPDefinition(DiameterConstants.AVP_SESSION_ID, "Session-Id", String.class, true, false, 0), // UTF8String
            new AVPDefinition(DiameterConstants.AVP_AUTH_REQUEST_TYPE, "Auth-Request-Type", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_AUTH_GRACE_PERIOD, "Auth-Grace-Period", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_AUTH_SESSION_STATE, "Auth-Session-State", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_AUTHORIZATION_LIFETIME, "Authorization-Lifetime", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_SESSION_TIMEOUT, "Session-Timeout", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_TERMINATION_CAUSE, "Termination-Cause", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_SESSION_BINDING, "Session-Binding", Long.class, true, false, 0), // Unsigned32

            // Re-Auth AVPs
            new AVPDefinition(DiameterConstants.AVP_RE_AUTH_REQUEST_TYPE, "Re-Auth-Request-Type", Integer.class, true, false, 0), // Enumerated

            // Accounting AVPs
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_RECORD_TYPE, "Accounting-Record-Type", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_RECORD_NUMBER, "Accounting-Record-Number", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_SESSION_ID, "Acct-Session-Id", byte[].class, true, false, 0), // OctetString
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, "Accounting-Sub-Session-Id", BigInteger.class, true, false, 0), // Unsigned64
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_MULTI_SESSION_ID, "Acct-Multi-Session-Id", String.class, true, false, 0), // UTF8String
            new AVPDefinition(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED, "Accounting-Realtime-Required", Integer.class, true, false, 0), // Enumerated

            // User Identity AVPs
            new AVPDefinition(DiameterConstants.AVP_USER_NAME, "User-Name", String.class, true, false, 0), // UTF8String
            new AVPDefinition(DiameterConstants.AVP_CLASS, "Class", byte[].class, true, false, 0), // OctetString

            // Destination and Route AVPs
            new AVPDefinition(DiameterConstants.AVP_DESTINATION_HOST, "Destination-Host", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_DESTINATION_REALM, "Destination-Realm", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_ROUTE_RECORD, "Route-Record", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_PROXY_INFO, "Proxy-Info", GroupedAVP.class, true, false, 0), // Grouped
            new AVPDefinition(DiameterConstants.AVP_PROXY_HOST, "Proxy-Host", String.class, true, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_PROXY_STATE, "Proxy-State", byte[].class, true, false, 0), // OctetString

            // Service and Application AVPs
            new AVPDefinition(DiameterConstants.AVP_MULTI_ROUND_TIME_OUT, "Multi-Round-Time-Out", Long.class, true, false, 0), // Unsigned32

            // Additional missing AVPs from RFC 6733
            new AVPDefinition(DiameterConstants.AVP_ERROR_REPORTING_HOST, "Error-Reporting-Host", String.class, false, false, 0), // DiameterIdentity
            new AVPDefinition(DiameterConstants.AVP_REDIRECT_HOST, "Redirect-Host", String.class, true, false, 0), // DiameterURI
            new AVPDefinition(DiameterConstants.AVP_REDIRECT_HOST_USAGE, "Redirect-Host-Usage", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_REDIRECT_MAX_CACHE_TIME, "Redirect-Max-Cache-Time", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_INBAND_SECURITY_ID, "Inband-Security-Id", Long.class, true, false, 0), // Unsigned32
            new AVPDefinition(DiameterConstants.AVP_SESSION_SERVER_FAILOVER, "Session-Server-Failover", Integer.class, true, false, 0), // Enumerated
            new AVPDefinition(DiameterConstants.AVP_EVENT_TIMESTAMP, "Event-Timestamp", Date.class, true, false, 0), // Time
            new AVPDefinition(DiameterConstants.AVP_ACCT_INTERIM_INTERVAL, "Acct-Interim-Interval", Long.class, true, false, 0), // Unsigned32

            // Experimental AVPs
            new AVPDefinition(DiameterConstants.AVP_EXPERIMENTAL_RESULT, "Experimental-Result", GroupedAVP.class, true, false, 0), // Grouped
            new AVPDefinition(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, "Experimental-Result-Code", Long.class, true, false, 0) // Unsigned32
        );
    }

    @Override
    public String getProtocolName() {
        return "Diameter Base Protocol (RFC 6733)";
    }
}
