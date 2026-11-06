package com.sipgate.sparta.diameter._3gpp.gy;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.messages.CreditControlRequest;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GyCreditControlRoundTripTest {

    private static final String IMSI_MCC_MNC = "26201";
    private static final String SGSN_MCC_MNC = "26202";
    private static final String APN = "ims";
    private static final byte[] CHARGING_ID = {0x00, 0x00, 0x30, 0x39};
    private static final byte[] RAT_TYPE = {0x06};
    private static final byte[] USER_LOCATION_INFO = {0x01, 0x62, (byte) 0xF2, 0x10, 0x01, 0x23, 0x45};
    private static final byte[] MS_TIME_ZONE = {0x40, 0x00};

    @Test
    void it_round_trips_service_information_with_ps_information_inside_a_ccr() throws Exception {
        // GIVEN
        final var pdpAddress = InetAddress.getByAddress(new byte[]{10, 20, 30, 40});
        final var request = new CreditControlRequest.Out();
        request.setSessionId("pgw.example.org;1;2");
        request.addAVP(AVP.create(gy(GyConstants.AVP_SERVICE_INFORMATION), List.of(
            AVP.create(gy(GyConstants.AVP_PS_INFORMATION), List.of(
                AVP.create(gy(GyConstants.AVP_3GPP_CHARGING_ID), CHARGING_ID),
                AVP.create(gy(GyConstants.AVP_3GPP_IMSI_MCC_MNC), IMSI_MCC_MNC),
                AVP.create(gy(GyConstants.AVP_3GPP_SGSN_MCC_MNC), SGSN_MCC_MNC),
                AVP.create(gy(GyConstants.AVP_3GPP_RAT_TYPE), RAT_TYPE),
                AVP.create(gy(GyConstants.AVP_3GPP_USER_LOCATION_INFO), USER_LOCATION_INFO),
                AVP.create(gy(GyConstants.AVP_3GPP_MS_TIMEZONE), MS_TIME_ZONE),
                AVP.create(new AVPKey(DiameterNasConstants.AVP_CALLED_STATION_ID, 0), APN),
                AVP.create(gy(GyConstants.AVP_PDP_ADDRESS), pdpAddress))))));
        final var bytes = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(bytes), new HopByHopId(1), new EndToEndId(2));

        // WHEN
        final var parsed = (CreditControlRequest.In) Command.parseMessage(ByteBuffer.wrap(bytes.toByteArray()));

        // THEN
        final var serviceInformation = (AVPContainer) parsed.findAVP(gy(GyConstants.AVP_SERVICE_INFORMATION));
        final var psInformation = (AVPContainer) serviceInformation.findAVP(gy(GyConstants.AVP_PS_INFORMATION));
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_CHARGING_ID)).getDataAsOctetString())
            .isEqualTo(CHARGING_ID);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_IMSI_MCC_MNC)).getDataAsString())
            .isEqualTo(IMSI_MCC_MNC);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_SGSN_MCC_MNC)).getDataAsString())
            .isEqualTo(SGSN_MCC_MNC);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_RAT_TYPE)).getDataAsOctetString())
            .isEqualTo(RAT_TYPE);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_USER_LOCATION_INFO)).getDataAsOctetString())
            .isEqualTo(USER_LOCATION_INFO);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_3GPP_MS_TIMEZONE)).getDataAsOctetString())
            .isEqualTo(MS_TIME_ZONE);
        assertThat(psInformation.findAVP(new AVPKey(DiameterNasConstants.AVP_CALLED_STATION_ID, 0)).getDataAsString())
            .isEqualTo(APN);
        assertThat(psInformation.findAVP(gy(GyConstants.AVP_PDP_ADDRESS)).getDataAsIPAddress())
            .isEqualTo(pdpAddress);
        assertThat(parsed.getCommandCode()).isEqualTo(CcConstants.CMD_CREDIT_CONTROL);
    }

    private static AVPKey gy(final int code) {
        return new AVPKey(code, _3gppConstants.VENDOR_ID_3GPP);
    }
}
