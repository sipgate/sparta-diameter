package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests the placement rules of {@link Command#setAVP}; the AVP list is read from the same package. */
class CommandSetAvpTest {

    private static List<Integer> codesOf(final Command command) {
        return command.getAVPs().stream().map(avp -> (int) avp.getCode()).toList();
    }

    @Test
    void it_replaces_an_existing_session_id_and_keeps_it_first() {
        // GIVEN
        final var request = new ReAuthRequest.Out();
        request.setSessionId("first;1;1");
        request.setOriginHost("host.example.org");
        request.setOriginRealm("example.org");

        // WHEN
        request.setSessionId("second;2;2");

        // THEN
        assertThat(codesOf(request)).containsExactly(
            DiameterConstants.AVP_SESSION_ID, DiameterConstants.AVP_ORIGIN_HOST, DiameterConstants.AVP_ORIGIN_REALM);
        assertThat(request.getSessionId()).isEqualTo("second;2;2");
    }

    @Test
    void it_puts_a_session_id_set_last_in_front_of_the_other_avps() {
        // GIVEN
        final var request = new ReAuthRequest.Out();
        request.setOriginHost("host.example.org");
        request.setOriginRealm("example.org");

        // WHEN
        request.setSessionId("host;1;1");

        // THEN
        assertThat(codesOf(request).getFirst()).isEqualTo(DiameterConstants.AVP_SESSION_ID);
    }

    @Test
    void it_still_appends_other_avps_in_the_order_they_are_set() {
        // GIVEN
        final var request = new ReAuthRequest.Out();
        request.setSessionId("host;1;1");

        // WHEN
        request.setOriginRealm("example.org");
        request.setOriginHost("host.example.org");
        request.setOriginRealm("other.example.org");

        // THEN a replaced AVP moves to the end, as before
        assertThat(codesOf(request)).containsExactly(
            DiameterConstants.AVP_SESSION_ID, DiameterConstants.AVP_ORIGIN_HOST, DiameterConstants.AVP_ORIGIN_REALM);
        assertThat(request.getOriginRealm()).isEqualTo("other.example.org");
    }

    @Test
    void it_leaves_a_command_without_a_session_id_unchanged() {
        // GIVEN
        final var request = new DeviceWatchdogRequest.Out();

        // WHEN
        request.setOriginHost("host.example.org");
        request.setOriginRealm("example.org");

        // THEN
        assertThat(codesOf(request)).containsExactly(DiameterConstants.AVP_ORIGIN_HOST, DiameterConstants.AVP_ORIGIN_REALM);
    }

    @Test
    void it_keeps_exactly_one_session_id_when_it_follows_unshiftAvp() {
        // GIVEN the answer path of DiameterMessageFactory: unshiftAvp, then the caller's setSessionId
        final var request = new ReAuthRequest.Out();
        request.setOriginHost("host.example.org");
        request.unshiftAvp(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "first;1;1"));

        // WHEN
        request.setSessionId("second;2;2");

        // THEN
        assertThat(codesOf(request)).containsExactly(DiameterConstants.AVP_SESSION_ID, DiameterConstants.AVP_ORIGIN_HOST);
        assertThat(request.getSessionId()).isEqualTo("second;2;2");
    }
}
