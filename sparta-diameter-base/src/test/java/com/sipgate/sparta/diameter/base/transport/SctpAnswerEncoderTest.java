package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.sctp.SctpMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link SctpAnswerEncoder} and {@link SctpRequestEncoder}: both serialize
 * outgoing messages into {@link SctpMessage}s with stream 0, unordered delivery and
 * PPID {@link SctpPayloadProtocolId#DIAMETER}. The encoded payload must be byte-identical
 * to the TCP encoders' output (same {@code writeTo} path).
 */
class SctpAnswerEncoderTest {

    private static SctpMessage encodeAnswer(final OutgoingAnswer answer) {
        final EmbeddedChannel channel = new EmbeddedChannel(new SctpAnswerEncoder());
        channel.writeOutbound(answer);
        final SctpMessage message = channel.readOutbound();
        channel.finish();
        return message;
    }

    private static SctpMessage encodeRequest(final DeviceWatchdogRequest.Out request,
                                             final HopByHopId hopByHop, final EndToEndId endToEnd) {
        final EmbeddedChannel channel = new EmbeddedChannel(new SctpRequestEncoder());
        channel.writeOutbound(new OutgoingRequestEnvelope(request, hopByHop, endToEnd));
        final SctpMessage message = channel.readOutbound();
        channel.finish();
        return message;
    }

    private static IncomingCommand incomingDwr() {
        return DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false,
                new HopByHopId(0x0A), new EndToEndId(0x0B), false);
    }

    @Test
    void it_wraps_an_answer_in_an_unordered_stream_zero_message_with_ppid_46() {
        // GIVEN
        final OutgoingAnswer answer = DiameterMessageFactory.createAnswer(
                (DeviceWatchdogRequest.In) incomingDwr(), DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        final SctpMessage message = encodeAnswer(answer);

        // THEN
        assertThat(message.protocolIdentifier()).isEqualTo(SctpPayloadProtocolId.DIAMETER);
        assertThat(message.streamIdentifier()).isZero();
        assertThat(message.isUnordered()).isTrue();
        assertThat(message.content().readableBytes()).isGreaterThanOrEqualTo(20); // Diameter header
    }

    @Test
    void it_wraps_a_request_in_an_unordered_stream_zero_message_with_ppid_46() {
        // GIVEN
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();

        // WHEN
        final SctpMessage message = encodeRequest(dwr, new HopByHopId(0xBEEF), new EndToEndId(0xCAFE));

        // THEN
        assertThat(message.protocolIdentifier()).isEqualTo(SctpPayloadProtocolId.DIAMETER);
        assertThat(message.streamIdentifier()).isZero();
        assertThat(message.isUnordered()).isTrue();
        assertThat(message.content().readableBytes()).isGreaterThanOrEqualTo(20);
    }

    @Test
    void it_produces_wire_payload_identical_to_the_tcp_encoder() {
        // GIVEN
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final HopByHopId hopByHop = new HopByHopId(0xBEEF);
        final EndToEndId endToEnd = new EndToEndId(0xCAFE);

        // WHEN: same request through the TCP encoder and the SCTP encoder
        final EmbeddedChannel tcpChannel = new EmbeddedChannel(new OutgoingRequestEncoder());
        tcpChannel.writeOutbound(new OutgoingRequestEnvelope(dwr, hopByHop, endToEnd));
        final ByteBuf tcpWire = tcpChannel.readOutbound();

        final SctpMessage sctpWire = encodeRequest(dwr, hopByHop, endToEnd);

        // THEN: payload identical; only the transport envelope differs
        assertThat(sctpWire.content()).isEqualTo(tcpWire);

        tcpChannel.finish();
    }
}
