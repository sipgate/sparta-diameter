package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.session.DiameterInitiatorSession;
import com.sipgate.sparta.diameter.base.session.DiameterResponderSession;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the {@link DiameterNode.Transport} enum and the argument validation of
 * the SCTP {@code listen}/{@code connect} overloads. All validation happens before any
 * socket is touched, so these tests run without network access.
 */
class DiameterNodeSctpValidationTest {

    private static final Supplier<DiameterResponderSession> RESPONDER_FACTORY =
            () -> null; // never invoked: the node rejects before touching the factory

    private static final Function<Consumer<DiameterInitiatorSession>, DiameterInitiatorSession> INITIATOR_FACTORY =
            reconnect -> null; // never invoked: the node rejects before touching the factory

    private static final DiameterNode.Transport TRANSPORT = DiameterNode.Transport.SCTP;

    private static final DiameterNode.Transport TRANSPORT_TCP = DiameterNode.Transport.TCP;

    @Test
    void it_exposes_tcp_and_sctp_as_transports() {
        assertThat(DiameterNode.Transport.values()).containsExactly(
                DiameterNode.Transport.TCP, DiameterNode.Transport.SCTP);
    }

    @Test
    void it_rejects_null_local_addresses_for_sctp_listen() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.listen(3868, RESPONDER_FACTORY, TRANSPORT, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("serverLocalAddresses");
        }
    }

    @Test
    void it_rejects_empty_local_addresses_for_sctp_listen() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.listen(3868, RESPONDER_FACTORY, TRANSPORT, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("serverLocalAddresses");
        }
    }

    @Test
    void it_rejects_local_addresses_for_tcp_listen() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.listen(
                    3868, RESPONDER_FACTORY, TRANSPORT_TCP, List.of(InetAddress.getLoopbackAddress())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Transport.SCTP");
        }
    }

    @Test
    void it_rejects_null_local_addresses_for_sctp_connect() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.connect("localhost", 3868, INITIATOR_FACTORY, TRANSPORT, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("clientLocalAddresses");
        }
    }

    @Test
    void it_rejects_empty_local_addresses_for_sctp_connect() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.connect("localhost", 3868, INITIATOR_FACTORY, TRANSPORT, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("clientLocalAddresses");
        }
    }

    @Test
    void it_rejects_local_addresses_for_tcp_connect() {
        // GIVEN / WHEN / THEN
        try (DiameterNode node = new DiameterNode()) {
            assertThatThrownBy(() -> node.connect(
                    "localhost", 3868, INITIATOR_FACTORY, TRANSPORT_TCP, List.of(InetAddress.getLoopbackAddress())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Transport.SCTP");
        }
    }
}
