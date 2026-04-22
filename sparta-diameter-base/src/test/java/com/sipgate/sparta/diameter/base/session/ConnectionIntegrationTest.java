package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.transport.DiameterNode;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;

/// This test binds a real LISTEN port to validate the connection / reconnect mechanism.
public class ConnectionIntegrationTest {

    private static final long APP_ID = 1337;
    private static final long VENDOR_ID = 42;

    @Test
    void it_connects_to_server_session() throws Exception {
        // GIVEN
        final var port = 3868;
        final var meterRegistry = new SimpleMeterRegistry();

        final var capabilities = new DiameterNodeConfig.Capabilities(
            List.of(APP_ID),
            List.of(),
            List.of(VENDOR_ID),
            List.of(new DiameterNodeConfig.VendorSpecificApp(VENDOR_ID, APP_ID))
        );

        final var loopbackAddress = List.of(InetAddress.getLoopbackAddress());
        final var nodeConfig = new DiameterNodeConfig(
            "demo-client",
            "demo-realm",
            loopbackAddress,
            0,
            "demo-product",
            capabilities
        );

        // GIVEN: there is a listening server
        final var serverSessionRef = new AtomicReference<DiameterResponderSession>();
        try (final var server = new DiameterNode(meterRegistry)) {

            // WHEN: the server is started
            server.listen(port, () -> {
                final var session = createServer(capabilities, meterRegistry);
                serverSessionRef.set(session);
                return session;
            });

            final var client = new DiameterNode(meterRegistry);
            final var clientSessionRef = new AtomicReference<DiameterInitiatorSession>();
            client.connect(
                "localhost",
                port,
                reconnect -> {
                    final var diameterInitiatorSession = new DiameterInitiatorSession(nodeConfig, reconnect, meterRegistry);
                    clientSessionRef.set(diameterInitiatorSession);
                    return diameterInitiatorSession;
                }
            );

            // THEN: after some time both sides think they are connected
            await().untilAsserted(() -> assertThat(clientSessionRef.get().getPeerState()).isEqualTo(PeerState.I_OPEN));
            assertThat(serverSessionRef.get().getPeerState()).isEqualTo(PeerState.R_OPEN);
        }
    }

    @Test
    void it_connects_later_when_server_was_offline_during_client_startup() {
        // GIVEN
        final var port = 3868;
        final var meterRegistry = new SimpleMeterRegistry();

        final var capabilities = new DiameterNodeConfig.Capabilities(
            List.of(APP_ID),
            List.of(),
            List.of(VENDOR_ID),
            List.of(new DiameterNodeConfig.VendorSpecificApp(VENDOR_ID, APP_ID))
        );

        final var loopbackAddress = List.of(InetAddress.getLoopbackAddress());
        final var nodeConfig = new DiameterNodeConfig(
            "demo-client",
            "demo-realm",
            loopbackAddress,
            0,
            "demo-product",
            capabilities,
            Duration.ofSeconds(6),
            Duration.ofSeconds(1)
        );

        final AtomicReference<DiameterInitiatorSession> clientSessionRef;
        try (var client = new DiameterNode(meterRegistry)) {
            clientSessionRef = new AtomicReference<DiameterInitiatorSession>();
            client.connect(
                "localhost",
                port,
                reconnect -> {
                    final var diameterInitiatorSession = new DiameterInitiatorSession(nodeConfig, reconnect, meterRegistry);
                    clientSessionRef.set(diameterInitiatorSession);
                    return diameterInitiatorSession;
                }
            );

            // THEN: Client is initially "down"
            await().untilAsserted(() -> assertThat(clientSessionRef.get().getPeerState()).isEqualTo(PeerState.CLOSED));

            // GIVEN: there is a listening server
            final var serverSessionRef = new AtomicReference<DiameterResponderSession>();
            try (final var server = new DiameterNode(meterRegistry)) {

                // WHEN: the server is started
                server.listen(port, () -> {
                    final var session = createServer(capabilities, meterRegistry);
                    serverSessionRef.set(session);
                    return session;
                });

                // THEN: after some time both sides think they are connected
                await().untilAsserted(() -> assertThat(clientSessionRef.get().getPeerState()).isEqualTo(PeerState.I_OPEN));
                assertThat(serverSessionRef.get().getPeerState()).isEqualTo(PeerState.R_OPEN);

                clientSessionRef.get().stop();
            }
        }
    }

    @Test
    void it_schedules_Tc_timer_on_unexpected_disconnect() {
        fail("implement me");
    }

    @Test
    void it_schedules_Tc_timer_after_closeGracefully() {
        fail("implement me");
    }

    private static DiameterResponderSession createServer(final DiameterNodeConfig.Capabilities capabilities, final SimpleMeterRegistry meterRegistry) {
        final var loopbackAddress = List.of(InetAddress.getLoopbackAddress());
        return new DiameterResponderSession(new DiameterNodeConfig(
            "demo-listener",
            "demo-realm",
            loopbackAddress,
            0,
            "demo-product",
            capabilities
        ), meterRegistry);
    }
}
