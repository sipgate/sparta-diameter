package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageEncoderTest {

    @Test
    void it_encodes_a_command_to_wire_format() {
        // GIVEN
        final EmbeddedChannel encoder = new EmbeddedChannel(new DiameterMessageEncoder());
        final DeviceWatchdogRequest dwr = DeviceWatchdogRequest.create(0x0000BEEF, 0x0000CAFE);

        // WHEN
        encoder.writeOutbound(dwr);
        final ByteBuf encoded = encoder.readOutbound();

        // THEN
        assertThat(encoded).isNotNull();
        assertThat(encoded.readableBytes()).isGreaterThanOrEqualTo(20); // at minimum the 20-byte header
        assertThat(encoded.getByte(0)).isEqualTo((byte) 0x01); // Diameter version

        encoded.release();
        encoder.finish();
    }

    @Test
    void it_produces_bytes_parseable_by_the_command_layer() throws Exception {
        // GIVEN
        final EmbeddedChannel encoder = new EmbeddedChannel(new DiameterMessageEncoder());
        final DeviceWatchdogRequest dwr = DeviceWatchdogRequest.create(0x1234, 0x5678);

        // WHEN
        encoder.writeOutbound(dwr);
        final ByteBuf encoded = encoder.readOutbound();
        final byte[] bytes = new byte[encoded.readableBytes()];
        encoded.readBytes(bytes);
        final Command<?> parsed = (Command<?>) Command.parseMessage(ByteBuffer.wrap(bytes));

        // THEN
        assertThat(parsed).isInstanceOf(DeviceWatchdogRequest.class);
        assertThat(parsed.getHopByHopIdentifier()).isEqualTo(0x1234);
        assertThat(parsed.getEndToEndIdentifier()).isEqualTo(0x5678);
        assertThat(parsed.isRequest()).isTrue();

        encoded.release();
        encoder.finish();
    }
}
