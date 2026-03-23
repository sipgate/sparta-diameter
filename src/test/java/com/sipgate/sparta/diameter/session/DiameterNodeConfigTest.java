package com.sipgate.sparta.diameter.session;

import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiameterNodeConfigTest {

    private static final InetAddress LOCALHOST;
    private static final DiameterNodeConfig.Capabilities EMPTY_CAPABILITIES =
            new DiameterNodeConfig.Capabilities(
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    static {
        try {
            LOCALHOST = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    private DiameterNodeConfig minimal() {
        return new DiameterNodeConfig(
                "hss.example.com",
                "example.com",
                Collections.singletonList(LOCALHOST),
                0L,
                "sparta",
                EMPTY_CAPABILITIES);
    }

    @Test
    void it_uses_rfc_default_timers_when_not_specified() {
        // GIVEN / WHEN
        final DiameterNodeConfig config = minimal();

        // THEN
        assertThat(config.getTwinit()).isEqualTo(Duration.ofSeconds(30));
        assertThat(config.getTc()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void it_uses_default_request_timeout_when_not_specified() {
        // GIVEN / WHEN
        final DiameterNodeConfig config = minimal();

        // THEN
        assertThat(config.getRequestTimeout()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    void it_accepts_tw_at_minimum_boundary() {
        // GIVEN / WHEN
        final DiameterNodeConfig config = new DiameterNodeConfig(
                "hss.example.com", "example.com", Collections.singletonList(LOCALHOST),
                0L, "sparta", EMPTY_CAPABILITIES,
                Duration.ofSeconds(6), DiameterNodeConfig.TC_RECOMMENDED);

        // THEN
        assertThat(config.getTwinit()).isEqualTo(Duration.ofSeconds(6));
    }

    @Test
    void it_rejects_tw_below_minimum() {
        // GIVEN
        final List<InetAddress> addresses = Collections.singletonList(LOCALHOST);
        final Duration tooShort = Duration.ofSeconds(5);

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                "hss.example.com", "example.com", addresses,
                0L, "sparta", EMPTY_CAPABILITIES,
                tooShort, DiameterNodeConfig.TC_RECOMMENDED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TWINIT");
    }

    @Test
    void it_rejects_null_origin_host() {
        // GIVEN
        final List<InetAddress> addresses = Collections.singletonList(LOCALHOST);

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                null, "example.com", addresses, 0L, "sparta", EMPTY_CAPABILITIES))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void it_rejects_null_origin_realm() {
        // GIVEN
        final List<InetAddress> addresses = Collections.singletonList(LOCALHOST);

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                "hss.example.com", null, addresses, 0L, "sparta", EMPTY_CAPABILITIES))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void it_rejects_empty_host_ip_addresses() {
        // GIVEN
        final List<InetAddress> empty = Collections.emptyList();

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                "hss.example.com", "example.com", empty, 0L, "sparta", EMPTY_CAPABILITIES))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void it_rejects_null_product_name() {
        // GIVEN
        final List<InetAddress> addresses = Collections.singletonList(LOCALHOST);

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                "hss.example.com", "example.com", addresses, 0L, null, EMPTY_CAPABILITIES))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void it_rejects_null_capabilities() {
        // GIVEN
        final List<InetAddress> addresses = Collections.singletonList(LOCALHOST);

        // WHEN / THEN
        assertThatThrownBy(() -> new DiameterNodeConfig(
                "hss.example.com", "example.com", addresses, 0L, "sparta", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void it_stores_capabilities() {
        // GIVEN
        final DiameterNodeConfig.Capabilities capabilities = new DiameterNodeConfig.Capabilities(
            Collections.singletonList(16777251),
            Collections.singletonList(3),
            Collections.singletonList(10415L));

        // WHEN
        final DiameterNodeConfig config = new DiameterNodeConfig(
                "hss.example.com", "example.com", Collections.singletonList(LOCALHOST),
                0L, "sparta", capabilities);

        // THEN
        assertThat(config.getCapabilities().authApplicationIds()).containsExactly(16777251);
        assertThat(config.getCapabilities().acctApplicationIds()).containsExactly(3);
        assertThat(config.getCapabilities().supportedVendorIds()).containsExactly(10415L);
    }

    @Test
    void it_returns_unmodifiable_host_ip_addresses() throws UnknownHostException {
        // GIVEN
        final DiameterNodeConfig config = minimal();
        final InetAddress extra = InetAddress.getByName("10.0.0.1");

        // WHEN / THEN
        final List<InetAddress> addresses = config.getHostIpAddresses();
        assertThatThrownBy(() -> addresses.add(extra))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
