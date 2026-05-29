package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.SessionTerminationRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasClassAVPsTest {

    @Test
    void it_accumulates_multiple_class_avps() {
        // GIVEN
        final var str = new SessionTerminationRequest.Out();
        final var v1 = new byte[]{1, 2, 3};
        final var v2 = new byte[]{4, 5, 6};

        // WHEN
        str.addClass(v1);
        str.addClass(v2);

        // THEN
        assertThat(str.getClasses()).hasSize(2);
        assertThat(str.getClasses().get(0)).isEqualTo(v1);
        assertThat(str.getClasses().get(1)).isEqualTo(v2);
    }

    @Test
    void it_returns_empty_list_when_no_class_avp_present() {
        // GIVEN
        final var str = new SessionTerminationRequest.Out();

        // WHEN / THEN
        assertThat(str.getClasses()).isEmpty();
    }

    @Test
    void it_returns_first_class_avp() {
        // GIVEN
        final var str = new SessionTerminationRequest.Out();
        final var v1 = new byte[]{0xA};
        final var v2 = new byte[]{0xB};

        // WHEN
        str.addClass(v1);
        str.addClass(v2);

        // THEN
        assertThat(str.getFirstClass()).isEqualTo(v1);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var str = new SessionTerminationRequest.Out();

        // WHEN / THEN
        assertThat(str.getFirstClass()).isNull();
    }

    @Test
    void it_adds_all_class_avps_from_collection() {
        // GIVEN
        final var str = new SessionTerminationRequest.Out();
        final var v1 = new byte[]{1};
        final var v2 = new byte[]{2};

        // WHEN
        str.addAllClasses(List.of(v1, v2));

        // THEN
        assertThat(str.getClasses()).hasSize(2);
    }
}
