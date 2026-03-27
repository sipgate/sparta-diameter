package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasAuthApplicationIdAVPsTest {

    @Test
    void it_accumulates_multiple_auth_application_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAuthApplicationId(1L);
        cer.addAuthApplicationId(2L);

        // THEN
        assertThat(cer.getAuthApplicationIds()).containsExactly(1L, 2L);
    }

    @Test
    void it_returns_empty_list_when_no_auth_application_id_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getAuthApplicationIds()).isEmpty();
    }

    @Test
    void it_returns_first_auth_application_id() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAuthApplicationId(10L);
        cer.addAuthApplicationId(20L);

        // THEN
        assertThat(cer.getFirstAuthApplicationId()).isEqualTo(10L);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstAuthApplicationId()).isNull();
    }

    @Test
    void it_adds_all_auth_application_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAllAuthApplicationIds(List.of(100L, 200L, 300L));

        // THEN
        assertThat(cer.getAuthApplicationIds()).containsExactly(100L, 200L, 300L);
    }
}
