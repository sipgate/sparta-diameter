package com.sipgate.sparta.diameter.ietf.doic;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Provides AVP definitions for Diameter Overload Indication Conveyance (RFC 7683 §7.8).
 * <p>
 * All DOIC AVPs share the same flag rules: M may be set, V MUST NOT be set; vendor id 0.
 */
public final class DoicAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(DoicConstants.AVP_OC_SUPPORTED_FEATURES, "OC-Supported-Features", GroupedAVP.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_FEATURE_VECTOR, "OC-Feature-Vector", BigInteger.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_OLR, "OC-OLR", GroupedAVP.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_SEQUENCE_NUMBER, "OC-Sequence-Number", BigInteger.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_VALIDITY_DURATION, "OC-Validity-Duration", Long.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_REPORT_TYPE, "OC-Report-Type", Integer.class, false, false, 0),
            new AVPDefinition(DoicConstants.AVP_OC_REDUCTION_PERCENTAGE, "OC-Reduction-Percentage", Long.class, false, false, 0)
        );
    }
}
