package com.sipgate.sparta.diameter.etsi.e2;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class E2AVPProviderTest {

    private final E2AVPProvider provider = new E2AVPProvider();

    @Test
    void it_defines_line_identifier_as_etsi_vendor_octetstring_without_m_bit() {
        // GIVEN the ETSI e2 provider
        // WHEN its single definition is read
        final AVPDefinition def = provider.getDefinitions().iterator().next();

        // THEN Line-Identifier is OctetString, ETSI vendor, V only (mandatory=false)
        assertThat(def.code()).isEqualTo(500);
        assertThat(def.name()).isEqualTo("Line-Identifier");
        assertThat(def.dataType()).isEqualTo(byte[].class);
        assertThat(def.vendorSpecific()).isTrue();
        assertThat(def.vendorId()).isEqualTo(13019);
        assertThat(def.mandatory()).isFalse();
    }
}
