package com.sipgate.sparta.diameter.ietf.cc;

/**
 * Constants for the Diameter Credit-Control Application (RFC 4006). AVPs are vendor 0.
 */
public final class CcConstants {

    /** Application id of the Diameter credit-control application (RFC 4006 §1.1, §3.1). */
    public static final int APP_ID_CREDIT_CONTROL = 4;

    /** Credit-Control-Request / Credit-Control-Answer command code (RFC 4006 §3). */
    public static final int CMD_CREDIT_CONTROL = 272;

    // AVP codes (RFC 4006 §9)
    public static final int AVP_CC_CORRELATION_ID = 411; // OctetString
    public static final int AVP_CC_INPUT_OCTETS = 412; // Unsigned64
    public static final int AVP_CC_MONEY = 413; // Grouped
    public static final int AVP_CC_OUTPUT_OCTETS = 414; // Unsigned64
    public static final int AVP_CC_REQUEST_NUMBER = 415; // Unsigned32
    public static final int AVP_CC_REQUEST_TYPE = 416; // Enumerated
    public static final int AVP_CC_SERVICE_SPECIFIC_UNITS = 417; // Unsigned64
    public static final int AVP_CC_SESSION_FAILOVER = 418; // Enumerated
    public static final int AVP_CC_SUB_SESSION_ID = 419; // Unsigned64
    public static final int AVP_CC_TIME = 420; // Unsigned32
    public static final int AVP_CC_TOTAL_OCTETS = 421; // Unsigned64
    public static final int AVP_CHECK_BALANCE_RESULT = 422; // Enumerated
    public static final int AVP_COST_INFORMATION = 423; // Grouped
    public static final int AVP_COST_UNIT = 424; // UTF8String
    public static final int AVP_CURRENCY_CODE = 425; // Unsigned32
    public static final int AVP_CREDIT_CONTROL = 426; // Enumerated
    public static final int AVP_CREDIT_CONTROL_FAILURE_HANDLING = 427; // Enumerated
    public static final int AVP_DIRECT_DEBITING_FAILURE_HANDLING = 428; // Enumerated
    public static final int AVP_EXPONENT = 429; // Integer32
    public static final int AVP_FINAL_UNIT_INDICATION = 430; // Grouped
    public static final int AVP_GRANTED_SERVICE_UNIT = 431; // Grouped
    public static final int AVP_RATING_GROUP = 432; // Unsigned32
    public static final int AVP_REDIRECT_ADDRESS_TYPE = 433; // Enumerated
    public static final int AVP_REDIRECT_SERVER = 434; // Grouped
    public static final int AVP_REDIRECT_SERVER_ADDRESS = 435; // UTF8String
    public static final int AVP_REQUESTED_ACTION = 436; // Enumerated
    public static final int AVP_REQUESTED_SERVICE_UNIT = 437; // Grouped
    public static final int AVP_RESTRICTION_FILTER_RULE = 438; // IPFilterRule
    public static final int AVP_SERVICE_IDENTIFIER = 439; // Unsigned32
    public static final int AVP_SERVICE_PARAMETER_INFO = 440; // Grouped
    public static final int AVP_SERVICE_PARAMETER_TYPE = 441; // Unsigned32
    public static final int AVP_SERVICE_PARAMETER_VALUE = 442; // OctetString
    public static final int AVP_SUBSCRIPTION_ID = 443; // Grouped
    public static final int AVP_SUBSCRIPTION_ID_DATA = 444; // UTF8String
    public static final int AVP_UNIT_VALUE = 445; // Grouped
    public static final int AVP_USED_SERVICE_UNIT = 446; // Grouped
    public static final int AVP_VALUE_DIGITS = 447; // Integer64
    public static final int AVP_VALIDITY_TIME = 448; // Unsigned32
    public static final int AVP_FINAL_UNIT_ACTION = 449; // Enumerated
    public static final int AVP_SUBSCRIPTION_ID_TYPE = 450; // Enumerated
    public static final int AVP_TARIFF_TIME_CHANGE = 451; // Time
    public static final int AVP_TARIFF_CHANGE_USAGE = 452; // Enumerated
    public static final int AVP_G_S_U_POOL_IDENTIFIER = 453; // Unsigned32
    public static final int AVP_CC_UNIT_TYPE = 454; // Enumerated
    public static final int AVP_MULTIPLE_SERVICES_INDICATOR = 455; // Enumerated
    public static final int AVP_MULTIPLE_SERVICES_CREDIT_CONTROL = 456; // Grouped
    public static final int AVP_G_S_U_POOL_REFERENCE = 457; // Grouped
    public static final int AVP_USER_EQUIPMENT_INFO = 458; // Grouped
    public static final int AVP_USER_EQUIPMENT_INFO_TYPE = 459; // Enumerated
    public static final int AVP_USER_EQUIPMENT_INFO_VALUE = 460; // OctetString
    public static final int AVP_SERVICE_CONTEXT_ID = 461; // UTF8String

    // CC-Request-Type (RFC 4006 §8.3)
    public static final int CC_REQUEST_TYPE_INITIAL_REQUEST = 1;
    public static final int CC_REQUEST_TYPE_UPDATE_REQUEST = 2;
    public static final int CC_REQUEST_TYPE_TERMINATION_REQUEST = 3;
    public static final int CC_REQUEST_TYPE_EVENT_REQUEST = 4;

    // CC-Session-Failover (RFC 4006 §8.4)
    public static final int CC_SESSION_FAILOVER_NOT_SUPPORTED = 0;
    public static final int CC_SESSION_FAILOVER_SUPPORTED = 1;

    // Check-Balance-Result (RFC 4006 §8.6)
    public static final int CHECK_BALANCE_RESULT_ENOUGH_CREDIT = 0;
    public static final int CHECK_BALANCE_RESULT_NO_CREDIT = 1;

    // Credit-Control (RFC 4006 §8.13)
    public static final int CREDIT_CONTROL_CREDIT_AUTHORIZATION = 0;
    public static final int CREDIT_CONTROL_RE_AUTHORIZATION = 1;

    // Credit-Control-Failure-Handling (RFC 4006 §8.14)
    public static final int CREDIT_CONTROL_FAILURE_HANDLING_TERMINATE = 0;
    public static final int CREDIT_CONTROL_FAILURE_HANDLING_CONTINUE = 1;
    public static final int CREDIT_CONTROL_FAILURE_HANDLING_RETRY_AND_TERMINATE = 2;

    // Direct-Debiting-Failure-Handling (RFC 4006 §8.15)
    public static final int DIRECT_DEBITING_FAILURE_HANDLING_TERMINATE_OR_BUFFER = 0;
    public static final int DIRECT_DEBITING_FAILURE_HANDLING_CONTINUE = 1;

    // Tariff-Change-Usage (RFC 4006 §8.27)
    public static final int TARIFF_CHANGE_USAGE_UNIT_BEFORE_TARIFF_CHANGE = 0;
    public static final int TARIFF_CHANGE_USAGE_UNIT_AFTER_TARIFF_CHANGE = 1;
    public static final int TARIFF_CHANGE_USAGE_UNIT_INDETERMINATE = 2;

    // CC-Unit-Type (RFC 4006 §8.32)
    public static final int CC_UNIT_TYPE_TIME = 0;
    public static final int CC_UNIT_TYPE_MONEY = 1;
    public static final int CC_UNIT_TYPE_TOTAL_OCTETS = 2;
    public static final int CC_UNIT_TYPE_INPUT_OCTETS = 3;
    public static final int CC_UNIT_TYPE_OUTPUT_OCTETS = 4;
    public static final int CC_UNIT_TYPE_SERVICE_SPECIFIC_UNITS = 5;

    // Final-Unit-Action (RFC 4006 §8.35)
    public static final int FINAL_UNIT_ACTION_TERMINATE = 0;
    public static final int FINAL_UNIT_ACTION_REDIRECT = 1;
    public static final int FINAL_UNIT_ACTION_RESTRICT_ACCESS = 2;

    // Redirect-Address-Type (RFC 4006 §8.38)
    public static final int REDIRECT_ADDRESS_TYPE_IPV4_ADDRESS = 0;
    public static final int REDIRECT_ADDRESS_TYPE_IPV6_ADDRESS = 1;
    public static final int REDIRECT_ADDRESS_TYPE_URL = 2;
    public static final int REDIRECT_ADDRESS_TYPE_SIP_URI = 3;

    // Multiple-Services-Indicator (RFC 4006 §8.40)
    public static final int MULTIPLE_SERVICES_INDICATOR_NOT_SUPPORTED = 0;
    public static final int MULTIPLE_SERVICES_INDICATOR_SUPPORTED = 1;

    // Requested-Action (RFC 4006 §8.41)
    public static final int REQUESTED_ACTION_DIRECT_DEBITING = 0;
    public static final int REQUESTED_ACTION_REFUND_ACCOUNT = 1;
    public static final int REQUESTED_ACTION_CHECK_BALANCE = 2;
    public static final int REQUESTED_ACTION_PRICE_ENQUIRY = 3;

    // Subscription-Id-Type (RFC 4006 §8.47)
    public static final int SUBSCRIPTION_ID_TYPE_END_USER_E164 = 0;
    public static final int SUBSCRIPTION_ID_TYPE_END_USER_IMSI = 1;
    public static final int SUBSCRIPTION_ID_TYPE_END_USER_SIP_URI = 2;
    public static final int SUBSCRIPTION_ID_TYPE_END_USER_NAI = 3;
    public static final int SUBSCRIPTION_ID_TYPE_END_USER_PRIVATE = 4;

    // User-Equipment-Info-Type (RFC 4006 §8.50)
    public static final int USER_EQUIPMENT_INFO_TYPE_IMEISV = 0;
    public static final int USER_EQUIPMENT_INFO_TYPE_MAC = 1;
    public static final int USER_EQUIPMENT_INFO_TYPE_EUI64 = 2;
    public static final int USER_EQUIPMENT_INFO_TYPE_MODIFIED_EUI64 = 3;

    // Result-Code values (RFC 4006 §9.1)
    public static final long RES_END_USER_SERVICE_DENIED = 4010;
    public static final long RES_CREDIT_CONTROL_NOT_APPLICABLE = 4011;
    public static final long RES_CREDIT_LIMIT_REACHED = 4012;
    public static final long RES_USER_UNKNOWN = 5030;
    public static final long RES_RATING_FAILED = 5031;

    private CcConstants() {}
}
