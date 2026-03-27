package com.sipgate.sparta.diameter.base.session;

import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for a local Diameter node.
 * <p>
 * Carries the node identity (Origin-Host, Origin-Realm, etc.) used in CER/CEA,
 * the declared application capabilities, and the protocol timers TWINIT and Tc
 * as defined in RFC 3539.
 * </p>
 */
public final class DiameterNodeConfig {

    /**
     * Default initial value for the watchdog timer (TWINIT), per RFC 3539 §3.4.1.
     */
    public static final Duration TWINIT_DEFAULT = Duration.ofSeconds(30);

    /**
     * Default value for the reconnect timer Tc.
     */
    public static final Duration TC_RECOMMENDED = Duration.ofSeconds(30);

    /**
     * Default request/answer timeout. No RFC-mandated value; 10 s sits below
     * the RFC 3539 Tw (30 s) and IR.88 Tc (30 s) infrastructure timers.
     */
    public static final Duration REQUEST_TIMEOUT_DEFAULT = Duration.ofSeconds(10);

    private static final Duration TWINIT_MIN = Duration.ofSeconds(6);

    private final String originHost;
    private final String originRealm;
    private final List<InetAddress> hostIpAddresses;
    private final long vendorId;
    private final String productName;
    private final Capabilities capabilities;
    private final Duration twinit;
    private final Duration tc;
    private final Duration requestTimeout;

    /**
     * Creates a config with explicit timer values.
     *
     * @param twinit         watchdog timer initial value; must be at least 6 seconds (RFC 3539 §3.4.1)
     * @param tc             reconnect timer
     * @param requestTimeout per-request answer timeout
     */
    public DiameterNodeConfig(
            final String originHost,
            final String originRealm,
            final List<InetAddress> hostIpAddresses,
            final long vendorId,
            final String productName,
            final Capabilities capabilities,
            final Duration twinit,
            final Duration tc,
            final Duration requestTimeout) {
        if (originHost == null || originHost.isEmpty()) {
            throw new IllegalArgumentException("originHost is required");
        }
        if (originRealm == null || originRealm.isEmpty()) {
            throw new IllegalArgumentException("originRealm is required");
        }
        if (hostIpAddresses == null || hostIpAddresses.isEmpty()) {
            throw new IllegalArgumentException("at least one hostIpAddress is required (RFC 6733 §5.3.1)");
        }
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("productName is required");
        }
        if (capabilities == null) {
            throw new IllegalArgumentException("capabilities is required");
        }
        if (twinit.compareTo(TWINIT_MIN) < 0) {
            throw new IllegalArgumentException("TWINIT MUST NOT be less than 6 seconds (RFC 3539 §3.4.1), got: " + twinit);
        }
        this.originHost = originHost;
        this.originRealm = originRealm;
        this.hostIpAddresses = List.copyOf(hostIpAddresses);
        this.vendorId = vendorId;
        this.productName = productName;
        this.capabilities = capabilities;
        this.twinit = twinit;
        this.tc = tc;
        this.requestTimeout = requestTimeout;
    }

    /**
     * Creates a config with explicit Tw/Tc timers and a default request timeout (10 s).
     *
     * @param twinit watchdog timer initial value; must be at least 6 seconds (RFC 3539 §3.4.1)
     * @param tc     reconnect timer
     */
    public DiameterNodeConfig(
            final String originHost,
            final String originRealm,
            final List<InetAddress> hostIpAddresses,
            final long vendorId,
            final String productName,
            final Capabilities capabilities,
            final Duration twinit,
            final Duration tc) {
        this(originHost, originRealm, hostIpAddresses, vendorId, productName, capabilities, twinit, tc, REQUEST_TIMEOUT_DEFAULT);
    }

    /**
     * Creates a config using the RFC-recommended default timer values
     * (TWINIT = 30s, Tc = 30s, request timeout = 10s).
     */
    public DiameterNodeConfig(
            final String originHost,
            final String originRealm,
            final List<InetAddress> hostIpAddresses,
            final long vendorId,
            final String productName,
            final Capabilities capabilities) {
        this(originHost, originRealm, hostIpAddresses, vendorId, productName, capabilities, TWINIT_DEFAULT, TC_RECOMMENDED);
    }

    public String getOriginHost() {
        return originHost;
    }

    public String getOriginRealm() {
        return originRealm;
    }

    public List<InetAddress> getHostIpAddresses() {
        return hostIpAddresses;
    }

    public long getVendorId() {
        return vendorId;
    }

    public String getProductName() {
        return productName;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Watchdog timer initial value (TWINIT), per RFC 3539 §3.4.1.
     */
    public Duration getTwinit() {
        return twinit;
    }

    /**
     * Reconnect timer (Tc).
     */
    public Duration getTc() {
        return tc;
    }

    /**
     * Per-request answer timeout.
     */
    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    // -------------------------------------------------------------------------

    /**
     * The application capabilities advertised in CER/CEA.
     */
    public record Capabilities(
            List<Long> authApplicationIds,
            List<Long> acctApplicationIds,
            List<Long> supportedVendorIds) {

        public Capabilities {
            authApplicationIds = List.copyOf(authApplicationIds);
            acctApplicationIds = List.copyOf(acctApplicationIds);
            supportedVendorIds = List.copyOf(supportedVendorIds);
        }
    }
}
