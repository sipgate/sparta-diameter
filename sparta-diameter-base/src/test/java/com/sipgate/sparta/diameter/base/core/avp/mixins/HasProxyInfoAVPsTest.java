package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasProxyInfoAVPsTest {

    private static final AVPKey PROXY_INFO_AVP_KEY = new AVPKey(DiameterConstants.AVP_PROXY_HOST, 0);

    private static List<AVP> proxyInfo(final String proxyHost) {
        return List.of(AVP.create(PROXY_INFO_AVP_KEY, proxyHost));
    }

    @Test
    void it_accumulates_multiple_proxy_infos() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();
        final var pi1 = proxyInfo("proxy1.example.com");
        final var pi2 = proxyInfo("proxy2.example.com");

        // WHEN
        rar.addProxyInfo(pi1);
        rar.addProxyInfo(pi2);

        // THEN
        assertThat(rar.getProxyInfos()).hasSize(2);
    }

    @Test
    void it_returns_empty_list_when_no_proxy_info_present() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN / THEN
        assertThat(rar.getProxyInfos()).isEmpty();
    }

    @Test
    void it_returns_first_proxy_info() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();
        final var pi1 = proxyInfo("first.example.com");
        final var pi2 = proxyInfo("second.example.com");

        // WHEN
        rar.addProxyInfo(pi1);
        rar.addProxyInfo(pi2);

        // THEN
        assertThat(rar.getFirstProxyInfo().findAVPs(PROXY_INFO_AVP_KEY)).isEqualTo(pi1);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();

        // WHEN / THEN
        assertThat(rar.getFirstProxyInfo()).isNull();
    }

    @Test
    void it_adds_all_proxy_infos_from_collection() {
        // GIVEN
        final var rar = new ReAuthRequest.Out();
        final var pi1 = proxyInfo("a.example.com");
        final var pi2 = proxyInfo("b.example.com");

        // WHEN
        rar.addAllProxyInfos(List.of(pi1, pi2));

        // THEN
        assertThat(rar.getProxyInfos()).hasSize(2);
    }
}
