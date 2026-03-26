package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageDecoderTest {

    private static final int MAX_FRAME_LENGTH = 16 * 1024 * 1024;

    private static EmbeddedChannel newDecoder() {
        return new EmbeddedChannel(
            new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 1, 3, -4, 0),
            new DiameterMessageDecoder()
        );
    }

    private static ByteBuf serialize(final OutgoingRequest request,
                                     final HopByHopId hopByHop,
                                     final EndToEndId endToEnd) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(baos), hopByHop, endToEnd);
        return Unpooled.wrappedBuffer(baos.toByteArray());
    }

    @Test
    void it_decodes_framed_bytes_to_a_command() throws Exception {
        // GIVEN
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final ByteBuf wire = serialize(dwr, new HopByHopId(0x0000BEEF), new EndToEndId(0x0000CAFE));

        // WHEN
        decoder.writeInbound(wire);
        final IncomingCommand decoded = decoder.readInbound();

        // THEN
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(new HopByHopId(0x0000BEEF));
        assertThat(decoded.endToEndId()).isEqualTo(new EndToEndId(0x0000CAFE));
        assertThat(((Command<?>) decoded).isRequest()).isTrue();

        decoder.finish();
    }

    @Test
    void it_decodes_multiple_messages_from_a_single_buffer() throws Exception {
        // GIVEN
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest.Out first = new DeviceWatchdogRequest.Out();
        final DeviceWatchdogRequest.Out second = new DeviceWatchdogRequest.Out();

        final ByteBuf wire = Unpooled.buffer();
        wire.writeBytes(serialize(first, new HopByHopId(1), new EndToEndId(2)));
        wire.writeBytes(serialize(second, new HopByHopId(3), new EndToEndId(4)));

        // WHEN
        decoder.writeInbound(wire);

        // THEN
        final IncomingCommand decodedFirst = decoder.readInbound();
        final IncomingCommand decodedSecond = decoder.readInbound();

        assertThat(decodedFirst).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decodedFirst.hopByHopId()).isEqualTo(new HopByHopId(1));

        assertThat(decodedSecond).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decodedSecond.hopByHopId()).isEqualTo(new HopByHopId(3));

        decoder.finish();
    }

    @Test
    void it_holds_back_an_incomplete_frame_until_the_rest_arrives() throws Exception {
        // GIVEN: a single DWR split across two TCP segments
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final ByteBuf serialized = serialize(dwr, new HopByHopId(0x42), new EndToEndId(0x43));
        final byte[] bytes = new byte[serialized.readableBytes()];
        serialized.readBytes(bytes);

        final int split = bytes.length / 2;
        final ByteBuf firstSegment = Unpooled.wrappedBuffer(bytes, 0, split);
        final ByteBuf secondSegment = Unpooled.wrappedBuffer(bytes, split, bytes.length - split);

        // WHEN
        decoder.writeInbound(firstSegment);
        final Object afterFirstSegment = decoder.readInbound();

        decoder.writeInbound(secondSegment);
        final IncomingCommand decoded = decoder.readInbound();

        // THEN
        assertThat(afterFirstSegment).isNull(); // framer held back the incomplete message
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(decoded.hopByHopId()).isEqualTo(new HopByHopId(0x42));

        decoder.finish();
    }
}
