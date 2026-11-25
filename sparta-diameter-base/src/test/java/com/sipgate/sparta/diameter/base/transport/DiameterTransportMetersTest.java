package com.sipgate.sparta.diameter.base.transport;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterTransportMetersTest {

    @Test
    void it_counts_the_connections_of_every_node_sharing_a_registry() {
        // GIVEN — two nodes' meters in one registry
        final SimpleMeterRegistry registry = new SimpleMeterRegistry();
        final DiameterTransportMeters meters = new DiameterTransportMeters(registry);
        final DiameterTransportMeters otherNodeMeters = new DiameterTransportMeters(registry);

        // WHEN
        meters.recordConnected(DiameterTransportMeters.DIRECTION_INBOUND);
        meters.recordActiveApplicationIds(List.of(4L));
        otherNodeMeters.recordConnected(DiameterTransportMeters.DIRECTION_INBOUND);
        otherNodeMeters.recordActiveApplicationIds(List.of(4L));

        // THEN — neither node's gauge is dropped
        assertThat(registry.get("diameter.connections.active").gauge().value()).isEqualTo(2.0);
        assertThat(registry.get("diameter.connections.active.direction").tag("direction", "inbound")
                .gauge().value()).isEqualTo(2.0);
        assertThat(registry.get("diameter.connections.active.application").tag("application_id", "4")
                .gauge().value()).isEqualTo(2.0);
    }

    @Test
    void it_keeps_the_connection_gauges_of_separate_registries_apart() {
        // GIVEN
        final SimpleMeterRegistry registry = new SimpleMeterRegistry();
        final SimpleMeterRegistry otherRegistry = new SimpleMeterRegistry();
        final DiameterTransportMeters meters = new DiameterTransportMeters(registry);
        new DiameterTransportMeters(otherRegistry);

        // WHEN
        meters.recordConnected(DiameterTransportMeters.DIRECTION_OUTBOUND);

        // THEN
        assertThat(registry.get("diameter.connections.active").gauge().value()).isEqualTo(1.0);
        assertThat(otherRegistry.get("diameter.connections.active").gauge().value()).isEqualTo(0.0);
    }
}
