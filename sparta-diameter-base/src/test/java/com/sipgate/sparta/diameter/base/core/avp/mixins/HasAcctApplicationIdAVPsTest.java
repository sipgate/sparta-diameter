package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasAcctApplicationIdAVPsTest {

    @Test
    void it_accumulates_multiple_acct_application_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAcctApplicationId(3L);
        cer.addAcctApplicationId(4L);

        // THEN
        assertThat(cer.getAcctApplicationIds()).containsExactly(3L, 4L);
    }

    @Test
    void it_returns_empty_list_when_no_acct_application_id_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getAcctApplicationIds()).isEmpty();
    }

    @Test
    void it_returns_first_acct_application_id() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAcctApplicationId(5L);
        cer.addAcctApplicationId(6L);

        // THEN
        assertThat(cer.getFirstAcctApplicationId()).isEqualTo(5L);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstAcctApplicationId()).isNull();
    }

    @Test
    void it_adds_all_acct_application_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAllAcctApplicationIds(List.of(7L, 8L));

        // THEN
        assertThat(cer.getAcctApplicationIds()).containsExactly(7L, 8L);
    }
}
