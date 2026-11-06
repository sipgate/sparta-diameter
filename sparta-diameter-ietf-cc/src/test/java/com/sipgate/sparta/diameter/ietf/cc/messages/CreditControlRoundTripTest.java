package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreditControlRoundTripTest {

    private static final HopByHopId HOP_BY_HOP = new HopByHopId(0x1234);
    private static final EndToEndId END_TO_END = new EndToEndId(0x5678);
    private static final String IMSI = "262011234567890";
    private static final int RATING_GROUP = 100;
    private static final BigInteger REQUESTED_OCTETS = BigInteger.valueOf(1_048_576L);
    private static final BigInteger GRANTED_OCTETS = BigInteger.valueOf(524_288L);
    private static final BigInteger USED_OCTETS = new BigInteger("18446744073709551615");

    @Test
    void it_round_trips_an_initial_credit_control_request_with_a_multiple_services_credit_control() throws Exception {
        // GIVEN
        final var request = new CreditControlRequest.Out();
        request.setSessionId("pgw.example.org;1;2");
        request.setOriginHost("pgw.example.org");
        request.setOriginRealm("example.org");
        request.setDestinationRealm("ocs.example.org");
        request.setAuthApplicationId(CcConstants.APP_ID_CREDIT_CONTROL);
        request.setServiceContextId("32251@3gpp.org");
        request.setCcRequestType(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);
        request.setCcRequestNumber(0L);
        request.addSubscriptionId(List.of(
            AVP.create(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_TYPE, 0), CcConstants.SUBSCRIPTION_ID_TYPE_END_USER_IMSI),
            AVP.create(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_DATA, 0), IMSI)));
        request.addMultipleServicesCreditControl(List.of(
            AVP.create(new AVPKey(CcConstants.AVP_REQUESTED_SERVICE_UNIT, 0), List.of(
                AVP.create(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0), REQUESTED_OCTETS))),
            AVP.create(new AVPKey(CcConstants.AVP_USED_SERVICE_UNIT, 0), List.of(
                AVP.create(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0), USED_OCTETS))),
            AVP.create(new AVPKey(CcConstants.AVP_RATING_GROUP, 0), (long) RATING_GROUP)));

        // WHEN
        final var parsed = (CreditControlRequest.In) Command.parseMessage(ByteBuffer.wrap(encode(request)));

        // THEN
        assertThat(parsed.getCommandCode()).isEqualTo(CcConstants.CMD_CREDIT_CONTROL);
        assertThat(parsed.getApplicationId()).isEqualTo(CcConstants.APP_ID_CREDIT_CONTROL);
        assertThat(parsed.isRequest()).isTrue();
        assertThat(parsed.hopByHopId()).isEqualTo(HOP_BY_HOP);
        assertThat(parsed.endToEndId()).isEqualTo(END_TO_END);
        assertThat(parsed.getSessionId()).isEqualTo("pgw.example.org;1;2");
        assertThat(parsed.getServiceContextId()).isEqualTo("32251@3gpp.org");
        assertThat(parsed.getCcRequestType()).isEqualTo(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);
        assertThat(parsed.getCcRequestNumber()).isZero();

        final var subscriptionIds = parsed.getSubscriptionIds();
        assertThat(subscriptionIds).hasSize(1);
        assertThat(subscriptionIds.get(0).findAVP(new AVPKey(CcConstants.AVP_SUBSCRIPTION_ID_DATA, 0)).getDataAsString())
            .isEqualTo(IMSI);

        final var servicesCreditControls = parsed.getMultipleServicesCreditControls();
        assertThat(servicesCreditControls).hasSize(1);
        final AVPContainer servicesCreditControl = servicesCreditControls.get(0);
        assertThat(servicesCreditControl.findAVP(new AVPKey(CcConstants.AVP_RATING_GROUP, 0)).getDataAsUnsignedInt())
            .isEqualTo(RATING_GROUP);
        assertThat(totalOctetsOf(servicesCreditControl, CcConstants.AVP_REQUESTED_SERVICE_UNIT)).isEqualTo(REQUESTED_OCTETS);
        assertThat(totalOctetsOf(servicesCreditControl, CcConstants.AVP_USED_SERVICE_UNIT)).isEqualTo(USED_OCTETS);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_round_trips_a_credit_control_answer_with_granted_units_and_final_unit_indication() throws Exception {
        // GIVEN
        final var outgoingRequest = new CreditControlRequest.Out();
        outgoingRequest.setSessionId("pgw.example.org;1;2");
        final var incomingRequest = (IncomingRequest<CreditControlAnswer.Out>) Command.parseMessage(
            ByteBuffer.wrap(encode(outgoingRequest)));
        final CreditControlAnswer.Out answer = DiameterMessageFactory.createAnswer(
            incomingRequest, DiameterConstants.RES_DIAMETER_SUCCESS);
        answer.setOriginHost("ocs.example.org");
        answer.setOriginRealm("example.org");
        answer.setCcRequestType(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);
        answer.setCcRequestNumber(0L);
        answer.addMultipleServicesCreditControl(List.of(
            AVP.create(new AVPKey(CcConstants.AVP_GRANTED_SERVICE_UNIT, 0), List.of(
                AVP.create(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0), GRANTED_OCTETS))),
            AVP.create(new AVPKey(CcConstants.AVP_RATING_GROUP, 0), (long) RATING_GROUP),
            AVP.create(new AVPKey(CcConstants.AVP_VALIDITY_TIME, 0), 600L),
            AVP.create(new AVPKey(CcConstants.AVP_FINAL_UNIT_INDICATION, 0), List.of(
                AVP.create(new AVPKey(CcConstants.AVP_FINAL_UNIT_ACTION, 0), CcConstants.FINAL_UNIT_ACTION_TERMINATE)))));

        // WHEN
        final var parsed = (CreditControlAnswer.In) Command.parseMessage(ByteBuffer.wrap(encode(answer)));

        // THEN
        assertThat(parsed.isRequest()).isFalse();
        assertThat(parsed.getSessionId()).isEqualTo("pgw.example.org;1;2");
        assertThat(parsed.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
        assertThat(parsed.getAuthApplicationId()).isEqualTo(CcConstants.APP_ID_CREDIT_CONTROL);
        assertThat(parsed.getCcRequestType()).isEqualTo(CcConstants.CC_REQUEST_TYPE_INITIAL_REQUEST);

        final var servicesCreditControls = parsed.getMultipleServicesCreditControls();
        assertThat(servicesCreditControls).hasSize(1);
        final AVPContainer servicesCreditControl = servicesCreditControls.get(0);
        assertThat(totalOctetsOf(servicesCreditControl, CcConstants.AVP_GRANTED_SERVICE_UNIT)).isEqualTo(GRANTED_OCTETS);
        assertThat(servicesCreditControl.findAVP(new AVPKey(CcConstants.AVP_VALIDITY_TIME, 0)).getDataAsUnsignedInt())
            .isEqualTo(600L);
        final var finalUnitIndication = (AVPContainer) servicesCreditControl.findAVP(
            new AVPKey(CcConstants.AVP_FINAL_UNIT_INDICATION, 0));
        assertThat(finalUnitIndication.findAVP(new AVPKey(CcConstants.AVP_FINAL_UNIT_ACTION, 0)).getDataAsEnumerated())
            .isEqualTo(CcConstants.FINAL_UNIT_ACTION_TERMINATE);
    }

    private static BigInteger totalOctetsOf(final AVPContainer servicesCreditControl, final int serviceUnitCode) {
        final var serviceUnit = (AVPContainer) servicesCreditControl.findAVP(new AVPKey(serviceUnitCode, 0));
        return serviceUnit.findAVP(new AVPKey(CcConstants.AVP_CC_TOTAL_OCTETS, 0)).getDataAsUnsignedLong();
    }

    private static byte[] encode(final CreditControlRequest.Out request) throws Exception {
        final var bytes = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(bytes), HOP_BY_HOP, END_TO_END);
        return bytes.toByteArray();
    }

    private static byte[] encode(final CreditControlAnswer.Out answer) throws Exception {
        final var bytes = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(bytes));
        return bytes.toByteArray();
    }
}
