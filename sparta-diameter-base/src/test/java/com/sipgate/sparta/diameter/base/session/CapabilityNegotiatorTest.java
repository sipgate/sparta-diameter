package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilityNegotiatorTest {

    private final CapabilityNegotiator negotiator = new CapabilityNegotiator();

    @Test
    void it_finds_common_auth_application_id() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(5L), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(5L), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_finds_common_acct_application_id() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
                Collections.emptyList(), Collections.singletonList(3L));

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.emptyList(), Collections.singletonList(3L), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_returns_false_when_no_common_applications() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(1L), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(2L), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void it_returns_false_with_empty_capabilities_on_both_sides() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
                Collections.emptyList(), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void it_accepts_relay_application_id_on_local_side() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(Integer.toUnsignedLong(DiameterConstants.APP_DIAMETER_RELAY)), Collections.emptyList());

        // WHEN: remote advertises no common application
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(99L), Collections.emptyList(), Collections.emptyList());

        // THEN: relay overrides — always common
        assertThat(result).isTrue();
    }

    @Test
    void it_accepts_relay_application_id_on_remote_side() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(5L), Collections.emptyList());
        final long remoteRelay = Integer.toUnsignedLong(DiameterConstants.APP_DIAMETER_RELAY);

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(remoteRelay), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_matches_local_vendor_specific_app_against_remote_vendor_specific_app() {
        // GIVEN: local declares SGd (3GPP appId 16777313, vendor 10415)
        final DiameterNodeConfig.Capabilities local = capabilitiesWithVendorSpecific(
                new DiameterNodeConfig.VendorSpecificApp(10415L, 16777313L));

        // WHEN: remote sends Vendor-Specific-Application-Id with same app ID
        final boolean result = negotiator.hasCommonApplication(
                local, Collections.emptyList(), Collections.emptyList(), Collections.singletonList(16777313L));

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_ignores_vendor_id_when_matching_vendor_specific_app() {
        // GIVEN: local declares SGd with vendor 10415
        final DiameterNodeConfig.Capabilities local = capabilitiesWithVendorSpecific(
                new DiameterNodeConfig.VendorSpecificApp(10415L, 16777313L));

        // WHEN: remote sends same app ID but a different vendor ID (vendor ID discarded per RFC §5.3)
        final boolean result = negotiator.hasCommonApplication(
                local, Collections.emptyList(), Collections.emptyList(), Collections.singletonList(16777313L));

        // THEN: still a match — vendor ID is irrelevant
        assertThat(result).isTrue();
    }

    @Test
    void it_matches_local_vendor_specific_app_against_remote_bare_auth_id() {
        // GIVEN: local declares only a vendor-specific app
        final DiameterNodeConfig.Capabilities local = capabilitiesWithVendorSpecific(
                new DiameterNodeConfig.VendorSpecificApp(10415L, 16777313L));

        // WHEN: remote sends the same app ID as a bare Auth-Application-Id
        final boolean result = negotiator.hasCommonApplication(
                local, Collections.singletonList(16777313L), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_matches_local_bare_auth_id_against_remote_vendor_specific_app() {
        // GIVEN: local declares a bare auth app ID
        final DiameterNodeConfig.Capabilities local = capabilities(
                Collections.singletonList(16777313L), Collections.emptyList());

        // WHEN: remote sends it wrapped in a Vendor-Specific-Application-Id
        final boolean result = negotiator.hasCommonApplication(
                local, Collections.emptyList(), Collections.emptyList(), Collections.singletonList(16777313L));

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_returns_false_when_only_vendor_specific_app_and_no_remote_match() {
        // GIVEN: local declares only a vendor-specific app
        final DiameterNodeConfig.Capabilities local = capabilitiesWithVendorSpecific(
                new DiameterNodeConfig.VendorSpecificApp(10415L, 16777313L));

        // WHEN: remote sends a completely different app ID
        final boolean result = negotiator.hasCommonApplication(
                local, Collections.singletonList(99L), Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isFalse();
    }

    private static DiameterNodeConfig.Capabilities capabilities(
            final List<Long> authIds,
            final List<Long> acctIds) {
        return new DiameterNodeConfig.Capabilities(authIds, acctIds, Collections.emptyList(), Collections.emptyList());
    }

    private static DiameterNodeConfig.Capabilities capabilitiesWithVendorSpecific(
            final DiameterNodeConfig.VendorSpecificApp... apps) {
        return new DiameterNodeConfig.Capabilities(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), List.of(apps));
    }
}
