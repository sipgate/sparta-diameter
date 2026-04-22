package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.transport.DiameterNode;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/// This test binds a real LISTEN port to validate the connection / reconnect mechanism.
public class ConnectionIntegrationTest {

    private static final int PORT = 3868;
    private static final long APP_ID = 1337;
    private static final long VENDOR_ID = 42;

    private static final List<InetAddress> LOOPBACK_ADDRESS = List.of(InetAddress.getLoopbackAddress());
    private static final DiameterNodeConfig.Capabilities CAPABILITIES = new DiameterNodeConfig.Capabilities(
        List.of(APP_ID),
        List.of(),
        List.of(VENDOR_ID),
        List.of(new DiameterNodeConfig.VendorSpecificApp(VENDOR_ID, APP_ID))
    );

    private static final DiameterNodeConfig SERVER_NODE_CONFIG = new DiameterNodeConfig(
        "demo-listener",
        "demo-realm",
        LOOPBACK_ADDRESS,
        0,
        "demo-product",
        CAPABILITIES
    );

    private static final DiameterNodeConfig CLIENT_NODE_CONFIG = new DiameterNodeConfig(
        "demo-client",
        "demo-realm",
        LOOPBACK_ADDRESS,
        0,
        "demo-product",
        CAPABILITIES,
        Duration.ofSeconds(6),
        Duration.ofSeconds(1)
    );

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Test
    void it_connects_to_server_session() {
        try (final var server = new DiameterNode(meterRegistry)) {
            // GIVEN: there is a listening server
            final var serverSessionRef = listen(server);
            try (final var client = new DiameterNode(meterRegistry)) {
                // WHEN
                final var clientSessionRef = connect(client);

                // THEN: after some time both sides think they are connected
                await().untilAsserted(() -> assertBothSidesConnected(serverSessionRef.get(), clientSessionRef.get()));

                clientSessionRef.get().stop();
            }
        }
    }

    @Test
    void it_connects_later_when_server_was_offline_during_client_startup() {
        // GIVEN: no server ready yet
        try (final var client = new DiameterNode(meterRegistry)) {
            // WHEN
            final var clientSessionRef = connect(client);

            // THEN: Client is initially "down"
            await().untilAsserted(() -> assertThat(clientSessionRef.get().getPeerState()).isEqualTo(PeerState.CLOSED));

            // GIVEN: there is a listening server
            try (final var server = new DiameterNode(meterRegistry)) {
                // WHEN: the server is started
                final var serverSessionRef = listen(server);

                // THEN: after some time both sides think they are connected
                await().untilAsserted(() -> assertBothSidesConnected(serverSessionRef.get(), clientSessionRef.get()));

                clientSessionRef.get().stop();
            }
        }
    }

    @Test
    void it_schedules_reconnect_after_closeGracefully() {
        try (final var server = new DiameterNode(meterRegistry)) {
            // GIVEN: there is a listening server
            final var serverSessionRef = listen(server);
            try (final var client = new DiameterNode(meterRegistry)) {
                // GIVEN: connection has been established
                final var clientSessionRef = connect(client);
                await().untilAsserted(() -> assertBothSidesConnected(serverSessionRef.get(), clientSessionRef.get()));

                // WHEN
                clientSessionRef.get().closeGracefully();

                // THEN: first it is closed
                await().untilAsserted(() -> assertThat(clientSessionRef.get().getPeerState()).isEqualTo(PeerState.CLOSED));

                // THEN: it has reconnected
                await().untilAsserted(() -> assertBothSidesConnected(serverSessionRef.get(), clientSessionRef.get()));

                clientSessionRef.get().stop();
            }
        }
    }

    // region Helpers

    private AtomicReference<DiameterResponderSession> listen(final DiameterNode node) {
        final var serverSessionRef = new AtomicReference<DiameterResponderSession>();
        node.listen(PORT, () -> {
            final var session = new DiameterResponderSession(SERVER_NODE_CONFIG, meterRegistry);
            serverSessionRef.set(session);
            return session;
        });
        return serverSessionRef;
    }

    private AtomicReference<DiameterInitiatorSession> connect(final DiameterNode node) {
        final var clientSessionRef = new AtomicReference<DiameterInitiatorSession>();
        node.connect(
            "localhost",
            PORT,
            reconnect -> {
                final var diameterInitiatorSession = new DiameterInitiatorSession(CLIENT_NODE_CONFIG, reconnect, meterRegistry);
                clientSessionRef.set(diameterInitiatorSession);
                return diameterInitiatorSession;
            }
        );
        return clientSessionRef;
    }

    private void assertBothSidesConnected(final DiameterResponderSession serverSession, final DiameterInitiatorSession clientSession) {
        assertThat(serverSession).isNotNull();
        assertThat(clientSession).isNotNull();
        assertThat(serverSession.getPeerState()).isEqualTo(PeerState.R_OPEN);
        assertThat(clientSession.getPeerState()).isEqualTo(PeerState.I_OPEN);
    }

    // endregion
}
