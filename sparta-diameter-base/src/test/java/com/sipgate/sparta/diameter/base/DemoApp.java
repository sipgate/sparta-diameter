package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class DemoApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApp.class);

    public static void main(final String[] args) throws IOException, DiameterException {
        // Connect to the Diameter server
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 3868), 5000);

        final CapabilitiesExchangeRequest.Out cer =
                new CapabilitiesExchangeRequest.Out();
        cer.setOriginHost("myapp.test.realm");
        cer.setOriginRealm("test.realm");
        cer.setDestinationRealm("test.realm");
        cer.setProductName("MyDiameterApp");
        cer.setFirmwareRevision(1L);
        cer.setVendorId(10415L); // 3GPP
        cer.addAuthApplicationId(4294967295L); // Relay

        final HopByHopId hopByHop = new HopByHopId(1);
        final EndToEndId endToEnd = new EndToEndId(1);

        final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        cer.writeTo(dos, hopByHop, endToEnd);

        LOGGER.debug("Sent CER {}", cer);

        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        final byte[] temp = new byte[4096];
        final int read = socket.getInputStream().read(temp);
        LOGGER.debug("Read {} bytes from server", read);
        if (read > 0) {
            buffer.put(temp, 0, read);
            buffer.flip();
            final IncomingCommand msg = Command.parseMessage(buffer);
            LOGGER.debug("Received CEA {}", msg);
        }

        socket.close();
    }
}
