package com.sipgate.sparta.diameter.ietf.mip6.split;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class Mip6SplitAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 5447, §4.2.1 — flag rules per RFC 5778 §6.
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        ------------------------------------------+----+---+------+----+----+
        MIP6-Agent-Info 486  4.2.1     Grouped    |  M | P |      | V  | Y  |
        ------------------------------------------+----+---+------+----+----+
        """);

    private static final Collection<AVPDefinition> DEFINITIONS = new Mip6SplitAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.mip6.split.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
