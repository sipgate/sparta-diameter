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

    /// from RFC 5447, §4.2.1–§4.2.5 — flag rules per RFC 5778 §6.
    /// MIP-Home-Agent-Address (334) and MIP-Home-Agent-Host (348) originate from RFC 4004
    /// and are re-specified by RFC 5447 for MIPv6 bootstrapping.
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        ------------------------------------------+----+---+------+----+----+
        MIP6-Agent-Info        486  4.2.1 Grouped |  M | P |      | V  | Y  |
        ------------------------------------------+----+---+------+----+----+
        MIP-Home-Agent-Address 334  4.2.2 Address |  M | P |      | V  | Y  |
        ------------------------------------------+----+---+------+----+----+
        MIP-Home-Agent-Host    348  4.2.3 Grouped |  M | P |      | V  | Y  |
        ------------------------------------------+----+---+------+----+----+
        MIP6-Home-Link-Prefix  125  4.2.4 OctetString |  M | P |      | V  | Y  |
        ------------------------------------------+----+---+------+----+----+
        MIP6-Feature-Vector    124  4.2.5 Unsigned64 |  M | P |      | V  | Y  |
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
