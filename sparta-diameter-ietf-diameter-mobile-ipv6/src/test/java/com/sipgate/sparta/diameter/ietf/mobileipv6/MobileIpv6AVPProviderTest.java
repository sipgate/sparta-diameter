package com.sipgate.sparta.diameter.ietf.mobileipv6;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MobileIpv6AVPProviderTest {

    @Test
    void it_defines_mip6_agent_info_as_grouped_with_m_flag_and_vendor_zero() {
        // GIVEN
        final var provider = new MobileIpv6AVPProvider();

        // WHEN — RFC 5447 §4.2.1
        final AVPDefinition definition = provider.getDefinitions().stream()
            .filter(d -> d.code() == 486)
            .findFirst().orElseThrow();

        // THEN
        assertThat(definition.name()).isEqualTo("MIP6-Agent-Info");
        assertThat(definition.dataType()).isEqualTo(GroupedAVP.class);
        assertThat(definition.mandatory()).isTrue();
        assertThat(definition.vendorSpecific()).isFalse();
        assertThat(definition.vendorId()).isZero();
    }
}
