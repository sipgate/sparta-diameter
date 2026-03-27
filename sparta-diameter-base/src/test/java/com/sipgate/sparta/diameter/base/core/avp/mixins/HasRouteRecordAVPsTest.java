package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasRouteRecordAVPsTest {

    @Test
    void it_accumulates_multiple_route_records() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN
        rar.addRouteRecord("proxy1.example.com");
        rar.addRouteRecord("proxy2.example.com");

        // THEN
        assertThat(rar.getRouteRecords()).containsExactly("proxy1.example.com", "proxy2.example.com");
    }

    @Test
    void it_returns_empty_list_when_no_route_record_present() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN / THEN
        assertThat(rar.getRouteRecords()).isEmpty();
    }

    @Test
    void it_returns_first_route_record() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN
        rar.addRouteRecord("first.example.com");
        rar.addRouteRecord("second.example.com");

        // THEN
        assertThat(rar.getFirstRouteRecord()).isEqualTo("first.example.com");
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN / THEN
        assertThat(rar.getFirstRouteRecord()).isNull();
    }

    @Test
    void it_adds_all_route_records_from_collection() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN
        rar.addAllRouteRecords(List.of("a.example.com", "b.example.com"));

        // THEN
        assertThat(rar.getRouteRecords()).containsExactly("a.example.com", "b.example.com");
    }
}
