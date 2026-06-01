package com.sipgate.sparta.diameter.ietf.drmp;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class DrmpAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 7944, §9.2.  Attribute Value Pair Flag Rules
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        --------------------------------------------------+----+----+
        DRMP                    301  9.1      Enumerated  |    | V  |
        """);

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.drmp.mixins";
    }
}
