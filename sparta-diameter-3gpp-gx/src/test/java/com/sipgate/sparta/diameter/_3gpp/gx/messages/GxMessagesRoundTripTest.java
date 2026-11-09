package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GxMessagesRoundTripTest {

    private static final int APPLICATION_ID_OFFSET = 8;
    private static final long APN_AMBR_UPLINK = 50_000_000L;
    private static final long APN_AMBR_DOWNLINK = 100_000_000L;

    @Test
    void it_round_trips_a_ccr_through_the_discovered_factory() throws Exception {
        // GIVEN
        final var request = new GxCreditControlRequest.Out();
        request.setSessionId("smf.example.org;1;2");
        request.setAuthApplicationId(GxConstants.APP_ID_GX);
        request.setCcRequestType(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);
        request.setCcRequestNumber(0);
        request.setIpCanType(5);
        request.setRatType(1004);

        // WHEN
        final var parsed = (GxCreditControlRequest.In) Command.parseMessage(ByteBuffer.wrap(wire(request)));

        // THEN
        assertThat(parsed.getApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
        assertThat(parsed.getCommandCode()).isEqualTo(CcConstants.CMD_CREDIT_CONTROL);
        assertThat(parsed.getCcRequestType()).isEqualTo(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);
        assertThat(parsed.getIpCanType()).isEqualTo(5);
        assertThat(parsed.getRatType()).isEqualTo(1004);
    }

    @Test
    void it_answers_a_ccr_with_the_rules_and_qos_the_pcrf_decided() throws Exception {
        // GIVEN
        final var request = new GxCreditControlRequest.Out();
        request.setSessionId("smf.example.org;1;2");
        final var incoming = (GxCreditControlRequest.In) Command.parseMessage(ByteBuffer.wrap(wire(request)));

        // WHEN
        final var answer = DiameterMessageFactory.createAnswer(incoming, DiameterConstants.RES_DIAMETER_SUCCESS);
        answer.setQosInformation(List.of(
            AVP.create(new AVPKey(GxConstants.AVP_APN_AGGREGATE_MAX_BITRATE_UL, _3gppConstants.VENDOR_ID_3GPP), APN_AMBR_UPLINK),
            AVP.create(new AVPKey(GxConstants.AVP_APN_AGGREGATE_MAX_BITRATE_DL, _3gppConstants.VENDOR_ID_3GPP), APN_AMBR_DOWNLINK)));
        answer.addChargingRuleInstall(List.of());
        answer.addChargingRuleInstall(List.of());
        final var parsed = (GxCreditControlAnswer.In) Command.parseMessage(ByteBuffer.wrap(wire(answer)));

        // THEN
        assertThat(parsed.getAuthApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
        assertThat(parsed.getChargingRuleInstalls()).hasSize(2);
        assertThat(parsed.getQosInformation().findAVP(
            new AVPKey(GxConstants.AVP_APN_AGGREGATE_MAX_BITRATE_DL, _3gppConstants.VENDOR_ID_3GPP)).getDataAsUnsignedInt())
            .isEqualTo(APN_AMBR_DOWNLINK);
    }

    @Test
    void it_sends_a_rar_with_the_gx_application_id_that_the_base_factory_parses() throws Exception {
        // GIVEN
        final var request = new GxReAuthRequest.Out();
        request.setSessionId("smf.example.org;1;2");
        request.setAuthApplicationId(GxConstants.APP_ID_GX);
        request.setReAuthRequestType(0);
        request.addChargingRuleRemove(List.of());

        // WHEN
        final var bytes = wire(request);
        final var parsed = Command.parseMessage(ByteBuffer.wrap(bytes));

        // THEN the header carries the Gx application id (bytes 8 to 11), which the base ReAuthRequest.In does not keep
        assertThat(ByteBuffer.wrap(bytes).getInt(APPLICATION_ID_OFFSET)).isEqualTo(GxConstants.APP_ID_GX);
        assertThat(parsed).isInstanceOf(ReAuthRequest.In.class);
    }

    private static byte[] wire(final OutgoingRequest<?> request) throws IOException {
        final var bytes = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(bytes), new HopByHopId(1), new EndToEndId(2));
        return bytes.toByteArray();
    }

    private static byte[] wire(final OutgoingAnswer answer) throws IOException {
        final var bytes = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(bytes));
        return bytes.toByteArray();
    }
}
