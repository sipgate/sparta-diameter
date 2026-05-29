package com.sipgate.sparta.diameter.ietf.mobileipv4;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class MobileIpv4AVPProviderTest {

    @Test
    void it_defines_the_rfc4004_avps_with_correct_type_and_flags() {
        // GIVEN
        final var provider = new MobileIpv4AVPProvider();
        final Function<Integer, AVPDefinition> byCode = code -> provider.getDefinitions().stream()
            .filter(d -> d.code() == code)
            .findFirst().orElseThrow();

        // WHEN
        final var address = byCode.apply(334);
        final var host = byCode.apply(348);

        // THEN — RFC 4004 §7.4 / §7.11: M flag set, V MUST NOT be set (vendor 0)
        assertThat(address.name()).isEqualTo("MIP-Home-Agent-Address");
        assertThat(address.dataType()).isEqualTo(InetAddress.class);
        assertThat(address.mandatory()).isTrue();
        assertThat(address.vendorSpecific()).isFalse();
        assertThat(address.vendorId()).isZero();

        assertThat(host.name()).isEqualTo("MIP-Home-Agent-Host");
        assertThat(host.dataType()).isEqualTo(GroupedAVP.class);
        assertThat(host.mandatory()).isTrue();
        assertThat(host.vendorSpecific()).isFalse();
        assertThat(host.vendorId()).isZero();
    }
}
