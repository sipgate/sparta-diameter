package com.sipgate.sparta.diameter.core;

/**
 * Constants for Diameter protocol.
 */
public final class DiameterConstants {

    // Diameter version
    public static final int DIAMETER_VERSION = 1;

    // Common Command Codes
    public static final int CMD_CAPABILITIES_EXCHANGE = 257;
    public static final int CMD_RE_AUTH = 258;
    public static final int CMD_ACCOUNTING = 271;
    public static final int CMD_ABORT_SESSION = 274;
    public static final int CMD_SESSION_TERMINATION = 275;
    public static final int CMD_DEVICE_WATCHDOG = 280;
    public static final int CMD_DISCONNECT_PEER = 282;

    // Common Application IDs
    public static final int APP_DIAMETER_COMMON_MESSAGES = 0;
    public static final int APP_DIAMETER_BASE_ACCOUNTING = 3;
    public static final int APP_DIAMETER_RELAY = 0xFFFFFFFF;

    // Common AVP Codes
    public static final int AVP_RESULT_CODE = 268;
    public static final int AVP_ORIGIN_HOST = 264;
    public static final int AVP_ORIGIN_REALM = 296;
    public static final int AVP_HOST_IP_ADDRESS = 257;
    public static final int AVP_VENDOR_ID = 266;
    public static final int AVP_PRODUCT_NAME = 269;
    public static final int AVP_SUPPORTED_VENDOR_ID = 265;
    public static final int AVP_AUTH_APPLICATION_ID = 258;
    public static final int AVP_ACCT_APPLICATION_ID = 259;
    public static final int AVP_VENDOR_SPECIFIC_APPLICATION_ID = 260;
    public static final int AVP_FIRMWARE_REVISION = 267;
    public static final int AVP_ORIGIN_STATE_ID = 278;
    public static final int AVP_ERROR_MESSAGE = 281;
    public static final int AVP_FAILED_AVP = 279;
    public static final int AVP_DISCONNECT_CAUSE = 273;

    // Session Management AVPs
    public static final int AVP_SESSION_ID = 263;
    public static final int AVP_AUTH_REQUEST_TYPE = 274;
    public static final int AVP_AUTH_GRACE_PERIOD = 276;
    public static final int AVP_AUTH_SESSION_STATE = 277;
    public static final int AVP_AUTHORIZATION_LIFETIME = 291;
    public static final int AVP_SESSION_TIMEOUT = 27;
    public static final int AVP_TERMINATION_CAUSE = 295;
    public static final int AVP_SESSION_BINDING = 270;

    // Re-Auth AVPs
    public static final int AVP_RE_AUTH_REQUEST_TYPE = 285;

    // Accounting AVPs
    public static final int AVP_ACCOUNTING_RECORD_TYPE = 480;
    public static final int AVP_ACCOUNTING_RECORD_NUMBER = 485;
    public static final int AVP_ACCOUNTING_SESSION_ID = 44;
    public static final int AVP_ACCOUNTING_SUB_SESSION_ID = 287;
    public static final int AVP_ACCOUNTING_MULTI_SESSION_ID = 50;
    public static final int AVP_ACCOUNTING_REALTIME_REQUIRED = 483;

    // User Identity AVPs
    public static final int AVP_USER_NAME = 1;
    public static final int AVP_CLASS = 25;

    // Destination and Route AVPs
    public static final int AVP_DESTINATION_HOST = 293;
    public static final int AVP_DESTINATION_REALM = 283;
    public static final int AVP_ROUTE_RECORD = 282;
    public static final int AVP_PROXY_INFO = 284;
    public static final int AVP_PROXY_HOST = 280;
    public static final int AVP_PROXY_STATE = 33;

    // Service and Application AVPs
    public static final int AVP_MULTI_ROUND_TIME_OUT = 272;

    // Additional missing AVPs from RFC 6733
    public static final int AVP_ERROR_REPORTING_HOST = 294;
    public static final int AVP_REDIRECT_HOST = 292;
    public static final int AVP_REDIRECT_HOST_USAGE = 261;
    public static final int AVP_REDIRECT_MAX_CACHE_TIME = 262;
    public static final int AVP_INBAND_SECURITY_ID = 299;
    public static final int AVP_SESSION_SERVER_FAILOVER = 271;
    public static final int AVP_EVENT_TIMESTAMP = 55;
    public static final int AVP_ACCT_INTERIM_INTERVAL = 85;

    // Experimental AVPs
    public static final int AVP_EXPERIMENTAL_RESULT = 297;
    public static final int AVP_EXPERIMENTAL_RESULT_CODE = 298;

    // Result Codes
    // Informational (1xxx)
    public static final long RES_DIAMETER_MULTI_ROUND_AUTH = 1001;

    // Success (2xxx)
    public static final long RES_DIAMETER_SUCCESS = 2001;
    public static final long RES_DIAMETER_LIMITED_SUCCESS = 2002;

    // Protocol Errors (3xxx)
    public static final long RES_DIAMETER_COMMAND_UNSUPPORTED = 3001;
    public static final long RES_DIAMETER_UNABLE_TO_DELIVER = 3002;
    public static final long RES_DIAMETER_REALM_NOT_SERVED = 3003;
    public static final long RES_DIAMETER_TOO_BUSY = 3004;
    public static final long RES_DIAMETER_LOOP_DETECTED = 3005;
    public static final long RES_DIAMETER_REDIRECT_INDICATION = 3006;
    public static final long RES_DIAMETER_APPLICATION_UNSUPPORTED = 3007;
    public static final long RES_DIAMETER_INVALID_HDR_BITS = 3008;
    public static final long RES_DIAMETER_INVALID_AVP_BITS = 3009;
    public static final long RES_DIAMETER_UNKNOWN_PEER = 3010;

    // Transient Failures (4xxx)
    public static final long RES_DIAMETER_AUTHENTICATION_REJECTED = 4001;
    public static final long RES_DIAMETER_OUT_OF_SPACE = 4002;
    public static final long RES_DIAMETER_ELECTION_LOST = 4003;

    // Permanent Failures (5xxx)
    public static final long RES_DIAMETER_AVP_UNSUPPORTED = 5001;
    public static final long RES_DIAMETER_UNKNOWN_SESSION_ID = 5002;
    public static final long RES_DIAMETER_AUTHORIZATION_REJECTED = 5003;
    public static final long RES_DIAMETER_INVALID_AVP_VALUE = 5004;
    public static final long RES_DIAMETER_MISSING_AVP = 5005;
    public static final long RES_DIAMETER_RESOURCES_EXCEEDED = 5006;
    public static final long RES_DIAMETER_CONTRADICTING_AVPS = 5007;
    public static final long RES_DIAMETER_AVP_NOT_ALLOWED = 5008;
    public static final long RES_DIAMETER_AVP_OCCURS_TOO_MANY_TIMES = 5009;
    public static final long RES_DIAMETER_NO_COMMON_APPLICATION = 5010;
    public static final long RES_DIAMETER_UNSUPPORTED_VERSION = 5011;
    public static final long RES_DIAMETER_UNABLE_TO_COMPLY = 5012;
    public static final long RES_DIAMETER_INVALID_BIT_IN_HEADER = 5013;
    public static final long RES_DIAMETER_INVALID_AVP_LENGTH = 5014;
    public static final long RES_DIAMETER_INVALID_MESSAGE_LENGTH = 5015;
    public static final long RES_DIAMETER_INVALID_AVP_BIT_COMBO = 5016;
    public static final long RES_DIAMETER_NO_COMMON_SECURITY = 5017;

    // Disconnect Causes
    public static final int DCC_REBOOTING = 0;
    public static final int DCC_BUSY = 1;
    public static final int DCC_DO_NOT_WANT_TO_TALK_TO_YOU = 2;

    private DiameterConstants() {
        // Utility class - no instantiation
    }
}
