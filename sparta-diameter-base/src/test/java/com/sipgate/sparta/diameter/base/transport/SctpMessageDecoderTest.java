package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link SctpMessageDecoder}: unwraps {@link SctpMessage}s into
 * {@code ByteBuf}s, dropping everything that is not clear-text Diameter (PPID 46).
 * Runs entirely on an {@link EmbeddedChannel} — no real sockets involved.
 */
class SctpMessageDecoderTest {

    private static EmbeddedChannel newDecoder() {
        return new EmbeddedChannel(
            new SctpMessageCompletionHandler(),
            new SctpMessageDecoder(),
            new DiameterMessageDecoder(new DiameterTransportMeters(new SimpleMeterRegistry()))
        );
    }

    private static SctpMessage diameterMessage(final int payloadProtocolId, final int streamIdentifier,
                                               final boolean unordered, final OutgoingRequest<?> request,
                                               final HopByHopId hopByHop, final EndToEndId endToEnd)
            throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(baos), hopByHop, endToEnd);
        return new SctpMessage(payloadProtocolId, streamIdentifier, unordered,
                Unpooled.wrappedBuffer(baos.toByteArray()));
    }

    @Test
    void it_decodes_a_sctp_message_with_ppid_46_into_a_command() throws Exception {
        // GIVEN
        final EmbeddedChannel channel = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();

        // WHEN
        channel.writeInbound(diameterMessage(
                SctpPayloadProtocolId.DIAMETER, 0, true, dwr, new HopByHopId(0x0000BEEF), new EndToEndId(0x0000CAFE)));
        final IncomingCommand decoded = channel.readInbound();

        // THEN
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(new HopByHopId(0x0000BEEF));
        assertThat(decoded.endToEndId()).isEqualTo(new EndToEndId(0x0000CAFE));
        assertThat(((Command) decoded).isRequest()).isTrue();

        channel.finish();
    }

    @Test
    void it_decodes_a_sctp_message_with_unspecified_ppid_0_as_diameter() throws Exception {
        // GIVEN: many peers send PPID 0 instead of 46
        final EmbeddedChannel channel = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();

        // WHEN
        channel.writeInbound(diameterMessage(
                SctpPayloadProtocolId.UNSPECIFIED, 0, true, dwr, new HopByHopId(0x10), new EndToEndId(0x20)));
        final IncomingCommand decoded = channel.readInbound();

        // THEN
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(new HopByHopId(0x10));

        channel.finish();
    }

    @Test
    void it_drops_a_sctp_message_with_foreign_ppid() throws Exception {
        // GIVEN
        final EmbeddedChannel channel = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();

        // WHEN
        channel.writeInbound(diameterMessage(
                SctpPayloadProtocolId.DTLS, 0, false, dwr, new HopByHopId(1), new EndToEndId(2)));
        final IncomingCommand decoded = channel.readInbound();

        // THEN
        assertThat(decoded).isNull();

        channel.finish();
    }

    @Test
    void it_processes_an_unordered_message_on_a_nonzero_stream() throws Exception {
        // GIVEN: the receiver must be ready for arbitrary streams (RFC 6733 §2.1.1, unordered stream-0
        // is the default send mode, but a peer may legally send on other streams)
        final EmbeddedChannel channel = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();

        // WHEN
        channel.writeInbound(diameterMessage(
                SctpPayloadProtocolId.DIAMETER, 5, true, dwr, new HopByHopId(0x77), new EndToEndId(0x88)));
        final IncomingCommand decoded = channel.readInbound();

        // THEN
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(new HopByHopId(0x77));

        channel.finish();
    }

    @Test
    void it_participates_in_a_full_roundtrip_through_the_request_encoder() throws Exception {
        // GIVEN: the complete SCTP codec chain as wired by DiameterNode
        final EmbeddedChannel channel = newDecoder();
        final OutgoingRequestEncoder tcpRequestEncoder = new OutgoingRequestEncoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final HopByHopId hopByHop = new HopByHopId(0x1111);
        final EndToEndId endToEnd = new EndToEndId(0x2222);

        // WHEN: the SctpRequestEncoder packs the request...
        final EmbeddedChannel outbound = new EmbeddedChannel(
                tcpRequestEncoder, new SctpRequestEncoder());
        outbound.writeOutbound(new OutgoingRequestEnvelope(dwr, hopByHop, endToEnd));
        final SctpMessage wire = outbound.readOutbound();

        // THEN: ...it must be stream 0, unordered, PPID 46...
        assertThat(wire.protocolIdentifier()).isEqualTo(SctpPayloadProtocolId.DIAMETER);
        assertThat(wire.streamIdentifier()).isZero();
        assertThat(wire.isUnordered()).isTrue();

        // ...and it must survive the full inbound decode chain as the original request
        channel.writeInbound(wire);
        final IncomingCommand decoded = channel.readInbound();
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(hopByHop);
        assertThat(decoded.endToEndId()).isEqualTo(endToEnd);

        outbound.finish();
        channel.finish();
    }

    @Test
    void it_reports_ppid_46_as_the_diameter_payload_id() {
        assertThat(SctpPayloadProtocolId.DIAMETER).isEqualTo(46);
        assertThat(SctpPayloadProtocolId.DTLS).isEqualTo(47);
    }
}
