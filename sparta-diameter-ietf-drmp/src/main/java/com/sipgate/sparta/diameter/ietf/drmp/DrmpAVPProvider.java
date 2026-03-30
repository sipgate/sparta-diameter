package com.sipgate.sparta.diameter.ietf.drmp;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * Provides AVP definitions for Diameter Routing Message Priority (RFC 7944).
 */
public final class DrmpAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            // RFC 7944 §9.1 — Enumerated, MUST NOT set M or V flags
            new AVPDefinition(DrmpConstants.AVP_DRMP, "DRMP", Integer.class, false, false, 0)
        );
    }
}
