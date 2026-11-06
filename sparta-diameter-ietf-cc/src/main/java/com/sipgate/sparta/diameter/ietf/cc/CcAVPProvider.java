package com.sipgate.sparta.diameter.ietf.cc;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Provides AVP definitions for the Diameter Credit-Control Application (RFC 4006 §9).
 * <p>
 * The M bit is set on the AVPs the RFC table marks "MUST". Value-Digits (Integer64) is declared as
 * {@code Long}, but {@code AVP.create(key, long)} would write 32 bits. Create it with
 * {@link CcAvps#valueDigits(long)} or {@code HasUnitValueAVP.setUnitValue(long, int)}.
 * </p>
 */
public final class CcAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(CcConstants.AVP_CC_CORRELATION_ID, "CC-Correlation-Id", byte[].class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_INPUT_OCTETS, "CC-Input-Octets", BigInteger.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_MONEY, "CC-Money", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_OUTPUT_OCTETS, "CC-Output-Octets", BigInteger.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_REQUEST_NUMBER, "CC-Request-Number", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_REQUEST_TYPE, "CC-Request-Type", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_SERVICE_SPECIFIC_UNITS, "CC-Service-Specific-Units", BigInteger.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_SESSION_FAILOVER, "CC-Session-Failover", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_SUB_SESSION_ID, "CC-Sub-Session-Id", BigInteger.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_TIME, "CC-Time", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_TOTAL_OCTETS, "CC-Total-Octets", BigInteger.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CHECK_BALANCE_RESULT, "Check-Balance-Result", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_COST_INFORMATION, "Cost-Information", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_COST_UNIT, "Cost-Unit", String.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CURRENCY_CODE, "Currency-Code", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CREDIT_CONTROL, "Credit-Control", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CREDIT_CONTROL_FAILURE_HANDLING, "Credit-Control-Failure-Handling", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_DIRECT_DEBITING_FAILURE_HANDLING, "Direct-Debiting-Failure-Handling", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_EXPONENT, "Exponent", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_FINAL_UNIT_INDICATION, "Final-Unit-Indication", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_GRANTED_SERVICE_UNIT, "Granted-Service-Unit", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_RATING_GROUP, "Rating-Group", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_REDIRECT_ADDRESS_TYPE, "Redirect-Address-Type", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_REDIRECT_SERVER, "Redirect-Server", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_REDIRECT_SERVER_ADDRESS, "Redirect-Server-Address", String.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_REQUESTED_ACTION, "Requested-Action", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_REQUESTED_SERVICE_UNIT, "Requested-Service-Unit", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_RESTRICTION_FILTER_RULE, "Restriction-Filter-Rule", String.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_SERVICE_IDENTIFIER, "Service-Identifier", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_SERVICE_PARAMETER_INFO, "Service-Parameter-Info", GroupedAVP.class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_SERVICE_PARAMETER_TYPE, "Service-Parameter-Type", Long.class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_SERVICE_PARAMETER_VALUE, "Service-Parameter-Value", byte[].class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_SUBSCRIPTION_ID, "Subscription-Id", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_SUBSCRIPTION_ID_DATA, "Subscription-Id-Data", String.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_UNIT_VALUE, "Unit-Value", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_USED_SERVICE_UNIT, "Used-Service-Unit", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_VALUE_DIGITS, "Value-Digits", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_VALIDITY_TIME, "Validity-Time", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_FINAL_UNIT_ACTION, "Final-Unit-Action", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_SUBSCRIPTION_ID_TYPE, "Subscription-Id-Type", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_TARIFF_TIME_CHANGE, "Tariff-Time-Change", Date.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_TARIFF_CHANGE_USAGE, "Tariff-Change-Usage", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_G_S_U_POOL_IDENTIFIER, "G-S-U-Pool-Identifier", Long.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_CC_UNIT_TYPE, "CC-Unit-Type", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_MULTIPLE_SERVICES_INDICATOR, "Multiple-Services-Indicator", Integer.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_MULTIPLE_SERVICES_CREDIT_CONTROL, "Multiple-Services-Credit-Control", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_G_S_U_POOL_REFERENCE, "G-S-U-Pool-Reference", GroupedAVP.class, true, false, 0),
            new AVPDefinition(CcConstants.AVP_USER_EQUIPMENT_INFO, "User-Equipment-Info", GroupedAVP.class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_USER_EQUIPMENT_INFO_TYPE, "User-Equipment-Info-Type", Integer.class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_USER_EQUIPMENT_INFO_VALUE, "User-Equipment-Info-Value", byte[].class, false, false, 0),
            new AVPDefinition(CcConstants.AVP_SERVICE_CONTEXT_ID, "Service-Context-Id", String.class, true, false, 0)
        );
    }
}
