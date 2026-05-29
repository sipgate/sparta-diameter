package com.sipgate.sparta.diameter.spec;

import java.util.List;

/**
 * Parsed Command Code Format definition per RFC 6733 §3.2.
 *
 * <p>{@code applicationId} is {@code 0} when the CCF header omits an
 * application-id (base protocol commands such as CER, DWR, DPR).
 */
public record CommandDef(
        long applicationId,
        long commandId,
        String commandName,
        boolean isRequest,
        boolean isProxiable,
        boolean isError,
        List<AvpRule> fixed,
        List<AvpRule> required,
        List<AvpRule> optional
) {
}
