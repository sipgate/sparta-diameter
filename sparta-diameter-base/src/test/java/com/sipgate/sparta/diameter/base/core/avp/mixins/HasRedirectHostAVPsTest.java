package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.messages.ReAuthAnswer;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasRedirectHostAVPsTest {

    private static final HopByHopId HBH = new HopByHopId(1);
    private static final EndToEndId E2E = new EndToEndId(2);

    private ReAuthAnswer.Out raa() {
        final var rar = (ReAuthRequest.In) DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_RE_AUTH, 0, true, HBH, E2E, false);
        return DiameterMessageFactory.createAnswer(rar, DiameterConstants.RES_DIAMETER_SUCCESS);
    }

    @Test
    void it_accumulates_multiple_redirect_hosts() {
        // GIVEN
        final var raa = raa();

        // WHEN
        raa.addRedirectHost("redirect1.example.com");
        raa.addRedirectHost("redirect2.example.com");

        // THEN
        assertThat(raa.getRedirectHosts()).containsExactly("redirect1.example.com", "redirect2.example.com");
    }

    @Test
    void it_returns_empty_list_when_no_redirect_host_present() {
        // GIVEN / WHEN / THEN
        assertThat(raa().getRedirectHosts()).isEmpty();
    }

    @Test
    void it_returns_first_redirect_host() {
        // GIVEN
        final var raa = raa();

        // WHEN
        raa.addRedirectHost("first.example.com");
        raa.addRedirectHost("second.example.com");

        // THEN
        assertThat(raa.getFirstRedirectHost()).isEqualTo("first.example.com");
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN / WHEN / THEN
        assertThat(raa().getFirstRedirectHost()).isNull();
    }

    @Test
    void it_adds_all_redirect_hosts_from_collection() {
        // GIVEN
        final var raa = raa();

        // WHEN
        raa.addAllRedirectHosts(List.of("a.example.com", "b.example.com"));

        // THEN
        assertThat(raa.getRedirectHosts()).containsExactly("a.example.com", "b.example.com");
    }
}
