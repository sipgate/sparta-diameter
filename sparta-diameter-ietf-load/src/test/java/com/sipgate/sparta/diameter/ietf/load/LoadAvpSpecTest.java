package com.sipgate.sparta.diameter.ietf.load;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class LoadAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 8583, §7.5 Attribute-Value Pair Flag Rules
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        --------------------------------------------------------+----+----+
        Load                   650   7.1      Grouped           |    | V  |
        --------------------------------------------------------+----+----+
        Load-Type              651   7.2      Enumerated        |    | V  |
        --------------------------------------------------------+----+----+
        Load-Value             652   7.3      Unsigned64        |    | V  |
        ------------------------------------------------------ -+----+----+
        SourceID               649   7.4      DiameterIdentity  |    | V  |
        --------------------------------------------------------+----+----+
        """);

    private static final Collection<AVPDefinition> DEFINITIONS = new LoadAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.load.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
