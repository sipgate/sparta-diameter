package com.sipgate.sparta.diameter.base.session;

import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Configuration for a local Diameter node.
 * <p>
 * Carries the node identity (Origin-Host, Origin-Realm, Host-IP-Address list, Vendor-Id,
 * Product-Name) used in CER/CEA, the declared application capabilities, the protocol
 * timers TWINIT and Tc as defined in RFC 3539, the request timeout and the per-node
 * {@link Behaviour} toggles.
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
    private final long originStateId;
    private final Behaviour behaviour;

    /**
     * Creates a config with explicit timer values and an Origin-State-Id.
     *
     * @param twinit         watchdog timer initial value; must be at least 6 seconds (RFC 3539 §3.4.1)
     * @param tc             reconnect timer
     * @param requestTimeout per-request answer timeout
     * @param originStateId  Origin-State-Id stamped into CER/CEA (RFC 6733 §8.16);
                        0 omits the AVP. The application supplies the value (e.g. epoch
                        seconds of the boot that last reset the node's state); the
                        library never generates it. MUST be strictly higher after every
                        restart with loss of previous state.
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
            final Duration requestTimeout,
            final long originStateId) {
        this(originHost, originRealm, hostIpAddresses, vendorId, productName, capabilities,
                twinit, tc, requestTimeout, originStateId, Behaviour.DEFAULT);
    }

    /**
     * Creates a config with explicit timer values, an Origin-State-Id and protocol
     * behaviour toggles.
     *
     * @param hostIpAddresses every local address advertised as Host-IP-Address in CER/CEA, in
     *                        order; for SCTP all multi-homing addresses (RFC 6733 §5.3.5)
     * @param twinit          watchdog timer initial value; must be at least 6 seconds (RFC 3539 §3.4.1)
     * @param tc              reconnect timer
     * @param requestTimeout  per-request answer timeout (also used for relayed requests)
     * @param originStateId   Origin-State-Id stamped into CER/CEA; 0 omits the AVP
     * @param behaviour       per-node protocol behaviour toggles; {@link Behaviour#DEFAULT} is
     *                        the RFC behaviour of the other constructors
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
            final Duration requestTimeout,
            final long originStateId,
            final Behaviour behaviour) {
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
        this.originStateId = originStateId;
        this.behaviour = Objects.requireNonNull(behaviour, "behaviour must not be null");
    }

    /**
     * Creates a config with explicit timer values and no Origin-State-Id (the AVP is
     * omitted from CER/CEA; RFC 6733 §8.16).
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
        this(originHost, originRealm, hostIpAddresses, vendorId, productName, capabilities,
                twinit, tc, requestTimeout, 0L);
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

    /**
     * The configured Origin-State-Id (RFC 6733 §8.16); 0 means "not configured" — the
     * AVP is omitted from CER/CEA so peers cannot infer session termination from us.
     *
     * @return the application-supplied Origin-State-Id, or 0
     */
    public long getOriginStateId() {
        return originStateId;
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
     * Per-node protocol behaviour toggles; {@link Behaviour#DEFAULT} unless configured.
     */
    public Behaviour getBehaviour() {
        return behaviour;
    }

    /**
     * Returns a copy of this config with the given behaviour toggles; all other values unchanged.
     */
    public DiameterNodeConfig withBehaviour(final Behaviour newBehaviour) {
        return new DiameterNodeConfig(originHost, originRealm, hostIpAddresses, vendorId, productName,
                capabilities, twinit, tc, requestTimeout, originStateId, newBehaviour);
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
     * Independent per-node protocol behaviour toggles. Every toggle defaults to
     * {@code false}, which is the RFC 6733 behaviour; {@code true} selects the
     * behaviour of peers such as the Yate DRA, for wire-level compatibility.
     *
     * @param parseErrorAnswerWithErrorBit {@code false}: an answer to an unparseable request
     *                                     carries the E-bit only for 3xxx and a
     *                                     {@code Failed-AVP} for AVP errors (RFC 6733 §7.1);
     *                                     {@code true}: always E-bit, never {@code Failed-AVP}
     * @param keepTransportOpenAfterDpa    {@code false}: after answering a peer's DPR the
     *                                     transport is closed (RFC 6733 §5.4);
     *                                     {@code true}: the DPA is sent and the transport is
     *                                     left open until the peer closes it
     */
    public record Behaviour(boolean parseErrorAnswerWithErrorBit, boolean keepTransportOpenAfterDpa) {

        /** RFC 6733 behaviour: every toggle {@code false}. */
        public static final Behaviour DEFAULT = new Behaviour(false, false);
    }

    /**
     * A single vendor-specific application entry as defined in RFC 6733 §6.11.
     * <p>
     * Carries the IANA-assigned vendor identifier and the vendor-specific
     * Diameter application identifier, advertised together inside a
     * {@code Vendor-Specific-Application-Id} grouped AVP (AVP code 260).
     * The {@code vendorId} is informational only and MUST NOT be used when
     * computing the capability intersection (RFC 6733 §5.3).
     * </p>
     *
     * @param vendorId          the IANA-assigned vendor identifier (e.g. 10415 for 3GPP)
     * @param authApplicationId the vendor-specific Diameter application identifier
     */
    public record VendorSpecificApp(long vendorId, long authApplicationId) {}

    /**
     * The application capabilities advertised in CER/CEA.
     */
    public record Capabilities(
            List<Long> authApplicationIds,
            List<Long> acctApplicationIds,
            List<Long> supportedVendorIds,
            List<VendorSpecificApp> vendorSpecificApplications) {

        public Capabilities {
            authApplicationIds = List.copyOf(authApplicationIds);
            acctApplicationIds = List.copyOf(acctApplicationIds);
            supportedVendorIds = List.copyOf(supportedVendorIds);
            vendorSpecificApplications = List.copyOf(vendorSpecificApplications);
        }
    }
}
