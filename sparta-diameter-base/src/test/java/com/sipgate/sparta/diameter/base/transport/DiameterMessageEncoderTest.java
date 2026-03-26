package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageEncoderTest {

    @Test
    void it_encodes_a_request_to_wire_format() throws Exception {
        // GIVEN
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // WHEN
        dwr.writeTo(new DataOutputStream(baos), new HopByHopId(0x0000BEEF), new EndToEndId(0x0000CAFE));
        final byte[] encoded = baos.toByteArray();

        // THEN
        assertThat(encoded.length).isGreaterThanOrEqualTo(20); // at minimum the 20-byte header
        assertThat(encoded[0]).isEqualTo((byte) 0x01); // Diameter version
    }

    @Test
    void it_produces_bytes_parseable_by_the_command_layer() throws Exception {
        // GIVEN
        final DeviceWatchdogRequest.Out dwr = new DeviceWatchdogRequest.Out();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // WHEN
        dwr.writeTo(new DataOutputStream(baos), new HopByHopId(0x1234), new EndToEndId(0x5678));
        final IncomingCommand parsed = Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));

        // THEN
        assertThat(parsed).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(parsed.hopByHopId()).isEqualTo(new HopByHopId(0x1234));
        assertThat(parsed.endToEndId()).isEqualTo(new EndToEndId(0x5678));
        assertThat(((Command<?>) parsed).isRequest()).isTrue();
    }
}
