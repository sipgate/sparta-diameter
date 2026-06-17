package com.sipgate.sparta.diameter._3gpp.s6t;

/**
 * Constants for the S6t/T6a Diameter interface (3GPP TS 29.336). This module exports only the
 * AVPs that other interfaces (e.g. S6a in TS 29.272) reference; the S6t/T6a command messages
 * themselves are not implemented here.
 */
public final class S6tConstants {

    /** AESE-Communication-Pattern — TS 29.336 §8.4.25. */
    public static final int AVP_AESE_COMMUNICATION_PATTERN = 3113;
    /** Communication-Pattern-Set — TS 29.336 §8.4.26. */
    public static final int AVP_COMMUNICATION_PATTERN_SET = 3114;
    /** Periodic-Communication-Indicator — TS 29.336 §8.4.27. */
    public static final int AVP_PERIODIC_COMMUNICATION_INDICATOR = 3115;
    /** Communication-Duration-Time — TS 29.336 §8.4.28. */
    public static final int AVP_COMMUNICATION_DURATION_TIME = 3116;
    /** Periodic-time — TS 29.336 §8.4.29. */
    public static final int AVP_PERIODIC_TIME = 3117;
    /** Scheduled-Communication-Time — TS 29.336 §8.4.30. */
    public static final int AVP_SCHEDULED_COMMUNICATION_TIME = 3118;
    /** Stationary-Indication — TS 29.336 §8.4.31. */
    public static final int AVP_STATIONARY_INDICATION = 3119;
    /** AESE-Communication-Pattern-Config-Status — TS 29.336 §8.4.32. */
    public static final int AVP_AESE_COMMUNICATION_PATTERN_CONFIG_STATUS = 3120;
    /** AESE-Error-Report — TS 29.336 §8.4.33. */
    public static final int AVP_AESE_ERROR_REPORT = 3121;
    /** Monitoring-Event-Configuration — TS 29.336 §8.4.2. */
    public static final int AVP_MONITORING_EVENT_CONFIGURATION = 3122;
    /** Monitoring-Event-Report — TS 29.336 §8.4.3. */
    public static final int AVP_MONITORING_EVENT_REPORT = 3123;
    /** SCEF-Reference-ID — TS 29.336 §8.4.4. */
    public static final int AVP_SCEF_REFERENCE_ID = 3124;
    /** SCEF-ID — TS 29.336 §8.4.5. */
    public static final int AVP_SCEF_ID = 3125;
    /** SCEF-Reference-ID-for-Deletion — TS 29.336 §8.4.6. */
    public static final int AVP_SCEF_REFERENCE_ID_FOR_DELETION = 3126;
    /** Monitoring-Type — TS 29.336 §8.4.7. */
    public static final int AVP_MONITORING_TYPE = 3127;
    /** Maximum-Number-of-Reports — TS 29.336 §8.4.8. */
    public static final int AVP_MAXIMUM_NUMBER_OF_REPORTS = 3128;
    /** UE-Reachability-Configuration — TS 29.336 §8.4.9. */
    public static final int AVP_UE_REACHABILITY_CONFIGURATION = 3129;
    /** Monitoring-Duration — TS 29.336 §8.4.10. */
    public static final int AVP_MONITORING_DURATION = 3130;
    /** Maximum-Detection-Time — TS 29.336 §8.4.11. */
    public static final int AVP_MAXIMUM_DETECTION_TIME = 3131;
    /** Reachability-Type — TS 29.336 §8.4.12. */
    public static final int AVP_REACHABILITY_TYPE = 3132;
    /** Maximum Latency — TS 29.336 §8.4.13. */
    public static final int AVP_MAXIMUM_LATENCY = 3133;
    /** Maximum Response Time — TS 29.336 §8.4.14. */
    public static final int AVP_MAXIMUM_RESPONSE_TIME = 3134;
    /** Location-Information-Configuration — TS 29.336 §8.4.15. */
    public static final int AVP_LOCATION_INFORMATION_CONFIGURATION = 3135;
    /** MONTE-Location-Type — TS 29.336 §8.4.16. */
    public static final int AVP_MONTE_LOCATION_TYPE = 3136;
    /** Accuracy — TS 29.336 §8.4.17. */
    public static final int AVP_ACCURACY = 3137;
    /** Association-Type — TS 29.336 §8.4.18. */
    public static final int AVP_ASSOCIATION_TYPE = 3138;
    /** Roaming-Information — TS 29.336 §8.4.19. */
    public static final int AVP_ROAMING_INFORMATION = 3139;
    /** Reachability-Information — TS 29.336 §8.4.20. */
    public static final int AVP_REACHABILITY_INFORMATION = 3140;
    /** IMEI-Change — TS 29.336 §8.4.22. */
    public static final int AVP_IMEI_CHANGE = 3141;
    /** Monitoring-Event-Config-Status — TS 29.336 §8.4.24. */
    public static final int AVP_MONITORING_EVENT_CONFIG_STATUS = 3142;
    /** Supported-Services — TS 29.336 §8.4.40. */
    public static final int AVP_SUPPORTED_SERVICES = 3143;
    /** Supported-Monitoring-Events — TS 29.336 §8.4.41. */
    public static final int AVP_SUPPORTED_MONITORING_EVENTS = 3144;
    /** CIR-Flags — TS 29.336 §8.4.39. */
    public static final int AVP_CIR_FLAGS = 3145;
    /** Service-Result — TS 29.336 §8.4.37. */
    public static final int AVP_SERVICE_RESULT = 3146;
    /** Service-Result-Code — TS 29.336 §8.4.38. */
    public static final int AVP_SERVICE_RESULT_CODE = 3147;
    /** Reference-ID-Validity-Time — TS 29.336 §8.4.42. */
    public static final int AVP_REFERENCE_ID_VALIDITY_TIME = 3148;
    /** Event-Handling — TS 29.336 §8.4.43. */
    public static final int AVP_EVENT_HANDLING = 3149;
    /** NIDD-Authorization-Request — TS 29.336 §8.4.44. */
    public static final int AVP_NIDD_AUTHORIZATION_REQUEST = 3150;
    /** NIDD-Authorization-Response — TS 29.336 §8.4.45. */
    public static final int AVP_NIDD_AUTHORIZATION_RESPONSE = 3151;
    /** Service-Report — TS 29.336 §8.4.47. */
    public static final int AVP_SERVICE_REPORT = 3152;
    /** Node-Type — TS 29.336 §8.4.48. */
    public static final int AVP_NODE_TYPE = 3153;
    /** S6t-HSS-Cause — TS 29.336 §8.4.50. */
    public static final int AVP_S6T_HSS_CAUSE = 3154;

    private S6tConstants() {}
}
