package com.sipgate.sparta.diameter.ietf.cc;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CcAVPProviderTest {

    @Test
    void it_has_a_definition_for_every_avp_constant() throws IllegalAccessException {
        // GIVEN
        final var provider = new CcAVPProvider();
        final var registeredCodes = provider.getDefinitions().stream()
            .map(AVPDefinition::code)
            .collect(Collectors.toUnmodifiableSet());

        // WHEN
        final var missing = new ArrayList<String>();
        for (final var field : CcConstants.class.getDeclaredFields()) {
            if (!field.getName().startsWith("AVP_") || field.getType() != int.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            final long code = field.getLong(null);
            if (!registeredCodes.contains(code)) {
                missing.add(field.getName() + " (code=" + code + ")");
            }
        }

        // THEN
        assertThat(missing)
            .as("AVP_* constants in CcConstants without a definition in CcAVPProvider")
            .isEmpty();
    }
}
