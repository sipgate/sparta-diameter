package com.sipgate.sparta.diameter.ietf.mip6serviceselection;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * Provides the Service-Selection AVP definition (RFC 5778 §6.2).
 * <p>
 * UTF8String with the M flag set and the V flag unset (vendor 0), per the RFC 5778 §6 AVP flag
 * table.
 * </p>
 */
public final class Mip6ServiceSelectionAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(Mip6ServiceSelectionConstants.AVP_SERVICE_SELECTION, "Service-Selection",
                String.class, true, false, 0)
        );
    }
}
