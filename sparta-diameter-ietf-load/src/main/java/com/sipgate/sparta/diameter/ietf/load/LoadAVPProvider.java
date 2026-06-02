package com.sipgate.sparta.diameter.ietf.load;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Provides AVP definitions for Diameter Load Information Conveyance (RFC 8583 §7.5).
 * <p>
 * All three Load AVPs share the same flag rules: M may be set, V MUST NOT be set; vendor id 0.
 * SourceID (code 649) is defined by RFC 8581 and not provided here.
 */
public final class LoadAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(LoadConstants.AVP_LOAD, "Load", GroupedAVP.class, false, false, 0),
            new AVPDefinition(LoadConstants.AVP_LOAD_TYPE, "Load-Type", Integer.class, false, false, 0),
            new AVPDefinition(LoadConstants.AVP_LOAD_VALUE, "Load-Value", BigInteger.class, false, false, 0)
        );
    }
}
