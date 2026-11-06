package com.sipgate.sparta.diameter._3gpp.gy;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The fixture is a hand-picked subset of 3GPP TS 32.299 Table 7.2.0.1 (Service-Information, PS-Information,
 * PDP-Address; the extractor cannot read that table's layout) and TS 29.061 Table 9a (the 3GPP-* AVPs).
 */
public class GyAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/gy-avps.json");
    private static final Collection<AVPDefinition> DEFINITIONS = new GyAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.gy.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
