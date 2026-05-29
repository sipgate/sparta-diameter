package com.sipgate.sparta.diameter.etsi.e2;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * AVP definition for the ETSI ES 283 035 Line-Identifier AVP (code 500, vendor 13019).
 */
public final class E2AVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(E2Constants.AVP_LINE_IDENTIFIER, "Line-Identifier", byte[].class, false, true, E2Constants.VENDOR_ID_ETSI)
        );
    }
}
