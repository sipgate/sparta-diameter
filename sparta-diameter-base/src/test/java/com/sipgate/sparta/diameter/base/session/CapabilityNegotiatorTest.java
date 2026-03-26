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
            Collections.singletonList(5), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(5L), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_finds_common_acct_application_id() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
                Collections.emptyList(), Collections.singletonList(3));

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.emptyList(), Collections.singletonList(3L));

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void it_returns_false_when_no_common_applications() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(1), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(2L), Collections.emptyList());

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void it_returns_false_with_empty_capabilities_on_both_sides() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
                Collections.emptyList(), Collections.emptyList());

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.emptyList(), Collections.emptyList());

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void it_accepts_relay_application_id_on_local_side() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(DiameterConstants.APP_DIAMETER_RELAY), Collections.emptyList());

        // WHEN: remote advertises no common application
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(99L), Collections.emptyList());

        // THEN: relay overrides — always common
        assertThat(result).isTrue();
    }

    @Test
    void it_accepts_relay_application_id_on_remote_side() {
        // GIVEN
        final DiameterNodeConfig.Capabilities local = capabilities(
            Collections.singletonList(5), Collections.emptyList());
        final long remoteRelay = Integer.toUnsignedLong(DiameterConstants.APP_DIAMETER_RELAY);

        // WHEN
        final boolean result = negotiator.hasCommonApplication(local, Collections.singletonList(remoteRelay), Collections.emptyList());

        // THEN
        assertThat(result).isTrue();
    }

    private static DiameterNodeConfig.Capabilities capabilities(
            final List<Integer> authIds,
            final List<Integer> acctIds) {
        return new DiameterNodeConfig.Capabilities(authIds, acctIds, Collections.emptyList());
    }
}
