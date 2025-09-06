package com.sipgate.sparta.diameter;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.core.avp.CoreAVPProvider;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;

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

        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 1);
        cer.setOriginHost("myapp.mnc003.mcc262.3gppnetwork.org");
        cer.setOriginRealm("mnc003.mcc262.3gppnetwork.org");
        cer.setDestinationRealm("mnc003.mcc262.3gppnetwork.org");
        cer.setProductName("MyDiameterApp");
        cer.setFirmwareRevision(1L);
        cer.setVendorId(10415L); // 3GPP
        cer.setAuthApplicationId(16777216L); // Diameter Credit Control Application

        final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        cer.writeTo(dos);

        System.out.println("Sent CER " + cer);

        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        final byte[] temp = new byte[4096];
        final int read = socket.getInputStream().read(temp);
        System.out.println("Read " + read + " bytes from server");
        if (read > 0) {
            buffer.put(temp, 0, read);
            buffer.flip();
            final CapabilitiesExchangeAnswer cea = (CapabilitiesExchangeAnswer) Command.parseMessage(buffer);
            System.out.println("Received CEA " + cea);
        }

        socket.close();
    }

}


