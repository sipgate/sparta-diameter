package com.sipgate.sparta.diameter.ietf.mip6serviceselection;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Mip6ServiceSelectionAVPProviderTest {

    @Test
    void it_defines_service_selection_as_utf8string_with_m_flag_and_vendor_zero() {
        // GIVEN
        final var provider = new Mip6ServiceSelectionAVPProvider();

        // WHEN — RFC 5778 §6.2
        final AVPDefinition definition = provider.getDefinitions().stream()
            .filter(d -> d.code() == 493)
            .findFirst().orElseThrow();

        // THEN
        assertThat(definition.name()).isEqualTo("Service-Selection");
        assertThat(definition.dataType()).isEqualTo(String.class);
        assertThat(definition.mandatory()).isTrue();
        assertThat(definition.vendorSpecific()).isFalse();
        assertThat(definition.vendorId()).isZero();
    }
}
