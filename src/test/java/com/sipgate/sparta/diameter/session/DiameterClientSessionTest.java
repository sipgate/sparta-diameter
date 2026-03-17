package com.sipgate.sparta.diameter.session;

import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterClientSessionTest {

    private static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final DiameterNodeConfig CONFIG = new DiameterNodeConfig(
            "hss.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

    @Test
    void it_starts_with_closed_peer_state() {
        // GIVEN / WHEN
        final DiameterClientSession session = new DiameterClientSession(CONFIG, () -> {});

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_starts_with_initial_watchdog_state() {
        // GIVEN / WHEN
        final DiameterClientSession session = new DiameterClientSession(CONFIG, () -> {});

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.INITIAL);
    }

    @Test
    void it_transitions_to_closed_and_down_on_disconnect() {
        // GIVEN
        final DiameterClientSession session = new DiameterClientSession(CONFIG, () -> {});

        // WHEN
        session.onDisconnected(null);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.DOWN);
    }
}
