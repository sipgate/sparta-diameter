package com.sipgate.sparta.diameter.ietf.mip6.integrated;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by RFC 5778 that are reused on other interfaces. */
public final class Mip6IntegratedAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            // RFC 5778 §5: M may be set, V MUST NOT be set, vendor id 0
            new AVPDefinition(Mip6IntegratedConstants.AVP_SERVICE_SELECTION, "Service-Selection", String.class, true, false, 0)
        );
    }
}
