package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasInbandSecurityIdAVPsTest {

    @Test
    void it_accumulates_multiple_inband_security_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addInbandSecurityId(0L);
        cer.addInbandSecurityId(1L);

        // THEN
        assertThat(cer.getInbandSecurityIds()).containsExactly(0L, 1L);
    }

    @Test
    void it_returns_empty_list_when_no_inband_security_id_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getInbandSecurityIds()).isEmpty();
    }

    @Test
    void it_returns_first_inband_security_id() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addInbandSecurityId(0L);
        cer.addInbandSecurityId(1L);

        // THEN
        assertThat(cer.getFirstInbandSecurityId()).isEqualTo(0L);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstInbandSecurityId()).isNull();
    }

    @Test
    void it_adds_all_inband_security_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAllInbandSecurityIds(List.of(0L, 1L));

        // THEN
        assertThat(cer.getInbandSecurityIds()).containsExactly(0L, 1L);
    }
}
