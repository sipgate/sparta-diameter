package com.sipgate.sparta.diameter._3gpp.sgdgdd;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.provider.Arguments;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Set;
import java.util.stream.Stream;

public class SgdGddAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs();

    private static Set<AvpDef> loadAvpDefs() {
        final ObjectMapper mapper = JsonMapper.builder().build();
        try (final InputStream in = SgdGddAvpSpecTest.class.getResourceAsStream("/sgdgdd-avps.json")) {
            if (in == null) {
                throw new IllegalStateException("sgdgdd-avps.json not found on the test classpath");
            }
            return mapper.readValue(in, new TypeReference<Set<AvpDef>>() {});
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS);
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins";
    }
}
