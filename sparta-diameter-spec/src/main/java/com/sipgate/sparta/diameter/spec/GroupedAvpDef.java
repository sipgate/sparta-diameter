package com.sipgate.sparta.diameter.spec;

import java.util.List;

/**
 * Parsed Grouped AVP definition per RFC 6733 §4.4.
 *
 * <p>{@code vendor} is {@code 0} when the {@code AVP-Header} omits the
 * vendor field (per §4.4 ABNF: "If absent, the default value of zero is
 * used.").
 */
public record GroupedAvpDef(
        long vendor,
        long avpCode,
        String avpName,
        List<AvpRule> fixed,
        List<AvpRule> required,
        List<AvpRule> optional
) {
}
