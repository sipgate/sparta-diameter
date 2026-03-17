package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogRequest;
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

    private static ByteBuf serialize(final Command<?> command) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        command.writeTo(new DataOutputStream(baos));
        return Unpooled.wrappedBuffer(baos.toByteArray());
    }

    @Test
    void it_decodes_framed_bytes_to_a_command() throws Exception {
        // GIVEN
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest dwr = DeviceWatchdogRequest.create(0x0000BEEF, 0x0000CAFE);
        final ByteBuf wire = serialize(dwr);

        // WHEN
        decoder.writeInbound(wire);
        final Command<?> decoded = decoder.readInbound();

        // THEN
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.class);
        assertThat(decoded.getHopByHopIdentifier()).isEqualTo(0x0000BEEF);
        assertThat(decoded.getEndToEndIdentifier()).isEqualTo(0x0000CAFE);
        assertThat(decoded.isRequest()).isTrue();

        decoder.finish();
    }

    @Test
    void it_decodes_multiple_messages_from_a_single_buffer() throws Exception {
        // GIVEN
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest first = DeviceWatchdogRequest.create(1, 2);
        final DeviceWatchdogRequest second = DeviceWatchdogRequest.create(3, 4);

        final ByteBuf wire = Unpooled.buffer();
        wire.writeBytes(serialize(first));
        wire.writeBytes(serialize(second));

        // WHEN
        decoder.writeInbound(wire);

        // THEN
        final Command<?> decodedFirst = decoder.readInbound();
        final Command<?> decodedSecond = decoder.readInbound();

        assertThat(decodedFirst).isInstanceOf(DeviceWatchdogRequest.class);
        assertThat(decodedFirst.getHopByHopIdentifier()).isEqualTo(1);

        assertThat(decodedSecond).isInstanceOf(DeviceWatchdogRequest.class);
        assertThat(decodedSecond.getHopByHopIdentifier()).isEqualTo(3);

        decoder.finish();
    }

    @Test
    void it_holds_back_an_incomplete_frame_until_the_rest_arrives() throws Exception {
        // GIVEN: a single DWR split across two TCP segments
        final EmbeddedChannel decoder = newDecoder();
        final DeviceWatchdogRequest dwr = DeviceWatchdogRequest.create(0x42, 0x43);
        final ByteBuf serialized = serialize(dwr);
        final byte[] bytes = new byte[serialized.readableBytes()];
        serialized.readBytes(bytes);

        final int split = bytes.length / 2;
        final ByteBuf firstSegment = Unpooled.wrappedBuffer(bytes, 0, split);
        final ByteBuf secondSegment = Unpooled.wrappedBuffer(bytes, split, bytes.length - split);

        // WHEN
        decoder.writeInbound(firstSegment);
        final Object afterFirstSegment = decoder.readInbound();

        decoder.writeInbound(secondSegment);
        final Command<?> decoded = decoder.readInbound();

        // THEN
        assertThat(afterFirstSegment).isNull(); // framer held back the incomplete message
        assertThat(decoded).isInstanceOf(DeviceWatchdogRequest.class);
        assertThat(decoded.getHopByHopIdentifier()).isEqualTo(0x42);

        decoder.finish();
    }
}
