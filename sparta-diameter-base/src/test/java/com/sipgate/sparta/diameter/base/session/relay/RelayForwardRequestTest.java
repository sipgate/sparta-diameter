package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.messages.AbortSessionRequest;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RelayForwardRequestTest {

    @Test
    void it_copies_all_avps_and_carries_route_records() throws Exception {
        // GIVEN — an incoming ASR with origin AVPs, a Session-Id and one existing
        // Route-Record from an upstream relay hop
        final AbortSessionRequest.Out out = new AbortSessionRequest.Out();
        out.setSessionId("sid-1;42");
        out.setOriginHost("ucn.example.com");
        out.setOriginRealm("example.com");
        out.setDestinationHost("dra.example.com");
        out.setDestinationRealm("example.com");
        out.addRouteRecord("ucn.example.com");
        final IncomingRequest<?> received = toIncoming(out, new HopByHopId(100), new EndToEndId(200));

        // WHEN — the relay forward frame is built and our own Route-Record appended
        final RelayForwardRequest forward = new RelayForwardRequest(received);
        forward.addRouteRecord("dra.example.com");

        // THEN — the frame carries the received header and every AVP over
        // (including the upstream Route-Record), plus ours appended
        assertThat(forward.getCommandCode()).isEqualTo(received.getCommandCode());
        assertThat(forward.isProxiable()).isEqualTo(received.isProxiable());
        assertThat(forward.getApplicationId()).isEqualTo(received.getApplicationId());
        assertThat(forward.getOriginHost()).isEqualTo("ucn.example.com");
        assertThat(forward.getOriginRealm()).isEqualTo("example.com");
        assertThat(forward.getSessionId()).isEqualTo("sid-1;42");
        assertThat(forward.getRouteRecords()).containsExactly("ucn.example.com", "dra.example.com");
        assertThat(forward.getAVPs()).hasSize(received.getAVPs().size() + 1);
    }

    @Test
    void it_encodes_as_a_valid_wire_request() throws Exception {
        // GIVEN — a forward frame with the received AVPs plus our Route-Record
        final AbortSessionRequest.Out out = new AbortSessionRequest.Out();
        out.setOriginHost("ucn.example.com");
        out.setOriginRealm("example.com");
        final IncomingRequest<?> received = toIncoming(out, new HopByHopId(7), new EndToEndId(9));
        final RelayForwardRequest forward = new RelayForwardRequest(received);
        forward.addRouteRecord("dra.example.com");

        // WHEN — the frame is encoded and re-parsed
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream dataOut = new DataOutputStream(bytes);
        forward.writeTo(dataOut, new HopByHopId(1234), new EndToEndId(5678));
        dataOut.flush();
        final com.sipgate.sparta.diameter.base.core.IncomingCommand parsedRaw = Command.parseMessage(ByteBuffer.wrap(bytes.toByteArray()));

        // THEN — it parses back as the same proxiable command and the writer
        // supplied the identifiers (fresh hop-by-hop from relay, preserved
        // end-to-end), not the frame
        final Command parsed = (Command) parsedRaw;
        assertThat(parsed.isProxiable()).isTrue();
        assertThat(parsed.getCommandCode()).isEqualTo(DiameterConstants.CMD_ABORT_SESSION);
        assertThat(parsed.getOriginHost()).isEqualTo("ucn.example.com");
        final var routed = (com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs) parsed;
        assertThat(routed.getRouteRecords()).containsExactly("dra.example.com");
    }

    @Test
    void it_never_mutates_the_received_request() {
        // GIVEN
        final AbortSessionRequest.Out out = new AbortSessionRequest.Out();
        final IncomingRequest<?> received = toIncoming(out, new HopByHopId(1), new EndToEndId(2));
        final int avpCount = received.getAVPs().size();

        // WHEN
        final RelayForwardRequest forward = new RelayForwardRequest(received);
        forward.addRouteRecord("dra.example.com");

        // THEN — the received command stays untouched; the copy owns its AVP list
        assertThat(received.getAVPs()).hasSize(avpCount);
        assertThat(((com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs) received)
                .getRouteRecords()).isEmpty();
    }

    @Test
    void it_carries_over_the_T_flag_of_a_retransmitted_request() throws Exception {
        // GIVEN — a received retransmission (T flag set)
        final IncomingRequest<?> received = new com.sipgate.sparta.diameter.base.core.GenericRequest(
                99999, true, true, 16777251, new HopByHopId(1), new EndToEndId(2));

        // WHEN
        final RelayForwardRequest forward = new RelayForwardRequest(received);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        forward.writeTo(new DataOutputStream(bytes), new HopByHopId(3), new EndToEndId(2));

        // THEN — RFC 6733 §3: the forwarded request still carries the T flag on the wire
        assertThat(forward.isRetransmitted()).isTrue();
        assertThat(bytes.toByteArray()[4] & 0x10).isEqualTo(0x10);
    }

    @Test
    void it_leaves_the_T_flag_clear_for_a_first_transmission() {
        // GIVEN
        final IncomingRequest<?> received = new com.sipgate.sparta.diameter.base.core.GenericRequest(
                99999, true, false, 16777251, new HopByHopId(1), new EndToEndId(2));

        // WHEN
        final RelayForwardRequest forward = new RelayForwardRequest(received);

        // THEN
        assertThat(forward.isRetransmitted()).isFalse();
    }

    @Test
    void it_rejects_a_null_received_request() {
        // GIVEN / WHEN / THEN
        assertThatThrownBy(() -> new RelayForwardRequest(null))
                .isInstanceOf(NullPointerException.class);
    }

    private static IncomingRequest<?> toIncoming(final AbortSessionRequest.Out out,
                                                 final HopByHopId hbh, final EndToEndId e2e) {
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // OutgoingRequest.writeTo(payload, hbh, e2e) is the public wire encoder
            out.writeTo(new DataOutputStream(bytes), hbh, e2e);
            return (IncomingRequest<?>) Command.parseMessage(ByteBuffer.wrap(bytes.toByteArray()));
        } catch (final Exception e) {
            throw new IllegalStateException("cannot build incoming request", e);
        }
    }
}
