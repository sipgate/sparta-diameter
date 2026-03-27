package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TestApp {

    public static void main(String[] args) throws IOException, DiameterException {
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

        System.out.println("Sent CER " + cer);

        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        final byte[] temp = new byte[4096];
        final int read = socket.getInputStream().read(temp);
        System.out.println("Read " + read + " bytes from server");
        if (read > 0) {
            buffer.put(temp, 0, read);
            buffer.flip();
            final IncomingCommand msg = Command.parseMessage(buffer);
            System.out.println("Received CEA " + msg);
        }

        socket.close();
    }
}
