package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Encode→decode round trips per command pair. Proves the wire encoder/decoder and the AVP registry
 * resolve every in-scope AVP, including deeply nested grouped AVPs (no 5001).
 */
class S6aRoundTripTest {

    private static final HopByHopId HOP_BY_HOP = new HopByHopId(0x0a0b0c0d);
    private static final EndToEndId END_TO_END = new EndToEndId(0x01020304);
    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Test
    void it_round_trips_a_ula_with_a_full_subscription_data_tree() throws Exception {
        // GIVEN — Subscription-Data → APN-Configuration-Profile → APN-Configuration →
        //         {EPS-Subscribed-QoS-Profile→ARP, AMBR, Service-Selection, MIP6-Agent-Info}
        final AVP arp = AVP.create(new AVPKey(_3gppConstants.AVP_ALLOCATION_RETENTION_PRIORITY, V), List.of(
            AVP.create(new AVPKey(_3gppConstants.AVP_PRIORITY_LEVEL, V), 5L),
            AVP.create(new AVPKey(_3gppConstants.AVP_PRE_EMPTION_CAPABILITY, V), 1),
            AVP.create(new AVPKey(_3gppConstants.AVP_PRE_EMPTION_VULNERABILITY, V), 0)));
        final AVP qosProfile = AVP.create(new AVPKey(S6aConstants.AVP_EPS_SUBSCRIBED_QOS_PROFILE, V), List.of(
            AVP.create(new AVPKey(_3gppConstants.AVP_QOS_CLASS_IDENTIFIER, V), 9),
            arp));
        final AVP ambr = AVP.create(new AVPKey(S6aConstants.AVP_AMBR, V), List.of(
            AVP.create(new AVPKey(_3gppConstants.AVP_MAX_REQUESTED_BANDWIDTH_UL, V), 100_000L),
            AVP.create(new AVPKey(_3gppConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, V), 200_000L)));
        final AVP mip6 = AVP.create(new AVPKey(486, 0), List.of(
            AVP.create(new AVPKey(334, 0), (InetAddress) Inet4Address.getByName("10.20.30.40"))));
        final AVP apnConfig = AVP.create(new AVPKey(S6aConstants.AVP_APN_CONFIGURATION, V), List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, V), 1L),
            AVP.create(new AVPKey(S6aConstants.AVP_PDN_TYPE, V), 0),
            AVP.create(new AVPKey(493, 0), "internet"),
            qosProfile, ambr, mip6));
        final AVP apnProfile = AVP.create(new AVPKey(S6aConstants.AVP_APN_CONFIGURATION_PROFILE, V), List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, V), 1L),
            AVP.create(new AVPKey(S6aConstants.AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR, V), 0),
            apnConfig));
        final List<AVP> subscriptionData = List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_SUBSCRIBER_STATUS, V), 0),
            AVP.create(new AVPKey(_3gppConstants.AVP_MSISDN, V), new byte[] {0x12, 0x34}),
            AVP.create(new AVPKey(S6aConstants.AVP_NETWORK_ACCESS_MODE, V), 0),
            ambr, apnProfile);

        final var out = new UpdateLocationAnswer.Out(HOP_BY_HOP, END_TO_END);
        out.setOriginHost("hss.example.com");
        out.setOriginRealm("example.com");
        out.setResultCode(2001L);
        out.setUlaFlags(1L);
        out.setSubscriptionData(subscriptionData);

        // WHEN
        final var in = (UpdateLocationAnswer.In) roundTrip(out);

        // THEN — every nested definition resolved and values survived the round trip
        assertThat(in.getUlaFlags()).isEqualTo(1L);
        final AVPContainer sd = in.getSubscriptionData();
        assertThat(sd).isNotNull();
        final AVPContainer profile = (AVPContainer) sd.findAVP(new AVPKey(S6aConstants.AVP_APN_CONFIGURATION_PROFILE, V));
        final AVPContainer config = (AVPContainer) profile.findAVP(new AVPKey(S6aConstants.AVP_APN_CONFIGURATION, V));
        assertThat(config.findAVP(new AVPKey(493, 0)).getDataAsString()).isEqualTo("internet");
        final AVPContainer agentInfo = (AVPContainer) config.findAVP(new AVPKey(486, 0));
        assertThat(agentInfo.findAVP(new AVPKey(334, 0)).getDataAsIPAddress())
            .isEqualTo(Inet4Address.getByName("10.20.30.40"));
        final AVPContainer qos = (AVPContainer) config.findAVP(new AVPKey(S6aConstants.AVP_EPS_SUBSCRIBED_QOS_PROFILE, V));
        assertThat(qos.findAVP(new AVPKey(_3gppConstants.AVP_QOS_CLASS_IDENTIFIER, V)).getDataAsEnumerated()).isEqualTo(9);
        final AVPContainer decodedArp = (AVPContainer) qos.findAVP(new AVPKey(_3gppConstants.AVP_ALLOCATION_RETENTION_PRIORITY, V));
        assertThat(decodedArp.findAVP(new AVPKey(_3gppConstants.AVP_PRIORITY_LEVEL, V)).getDataAsUnsignedInt()).isEqualTo(5L);
        final AVPContainer decodedAmbr = (AVPContainer) config.findAVP(new AVPKey(S6aConstants.AVP_AMBR, V));
        assertThat(decodedAmbr.findAVP(new AVPKey(_3gppConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, V)).getDataAsUnsignedInt())
            .isEqualTo(200_000L);
    }

    @Test
    void it_round_trips_an_air_request() throws Exception {
        // GIVEN
        final var out = new AuthenticationInformationRequest.Out();
        out.setOriginHost("mme.example.com");
        out.setOriginRealm("example.com");
        out.setUserName("001010000000001");
        out.setVisitedPlmnId(new byte[] {0x00, 0x11, 0x22});
        out.setRequestedEutranAuthenticationInfo(List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, V), 3L),
            AVP.create(new AVPKey(S6aConstants.AVP_IMMEDIATE_RESPONSE_PREFERRED, V), 1L)));

        // WHEN
        final var in = (AuthenticationInformationRequest.In) roundTrip(out);

        // THEN
        assertThat(in.getUserName()).isEqualTo("001010000000001");
        assertThat(in.getVisitedPlmnId()).containsExactly(0x00, 0x11, 0x22);
        final AVPContainer reqInfo = in.getRequestedEutranAuthenticationInfo();
        assertThat(reqInfo.findAVP(new AVPKey(S6aConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, V)).getDataAsUnsignedInt())
            .isEqualTo(3L);
    }

    @Test
    void it_round_trips_an_aia_with_authentication_info_and_an_e_utran_vector() throws Exception {
        // GIVEN — Authentication-Info → E-UTRAN-Vector → {Item-Number, RAND, XRES, AUTN, KASME}
        final AVP eutranVector = AVP.create(new AVPKey(S6aConstants.AVP_E_UTRAN_VECTOR, V), List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_ITEM_NUMBER, V), 1L),
            AVP.create(new AVPKey(S6aConstants.AVP_RAND, V), new byte[16]),
            AVP.create(new AVPKey(S6aConstants.AVP_XRES, V), new byte[] {1, 2, 3, 4, 5, 6, 7, 8}),
            AVP.create(new AVPKey(S6aConstants.AVP_AUTN, V), new byte[16]),
            AVP.create(new AVPKey(S6aConstants.AVP_KASME, V), new byte[32])));

        final var out = new AuthenticationInformationAnswer.Out(HOP_BY_HOP, END_TO_END);
        out.setOriginHost("hss.example.com");
        out.setOriginRealm("example.com");
        out.setResultCode(2001L);
        out.setAuthenticationInfo(List.of(eutranVector));

        // WHEN
        final var in = (AuthenticationInformationAnswer.In) roundTrip(out);

        // THEN
        final AVPContainer authInfo = in.getAuthenticationInfo();
        assertThat(authInfo).isNotNull();
        final AVPContainer vector = (AVPContainer) authInfo.findAVP(new AVPKey(S6aConstants.AVP_E_UTRAN_VECTOR, V));
        assertThat(vector.findAVP(new AVPKey(S6aConstants.AVP_ITEM_NUMBER, V)).getDataAsUnsignedInt()).isEqualTo(1L);
        assertThat(vector.findAVP(new AVPKey(S6aConstants.AVP_KASME, V)).getDataAsOctetString()).hasSize(32);
    }

    @Test
    void it_round_trips_a_clr_with_cancellation_type_and_flags() throws Exception {
        // GIVEN
        final var out = new CancelLocationRequest.Out();
        out.setOriginHost("hss.example.com");
        out.setOriginRealm("example.com");
        out.setUserName("001010000000001");
        out.setCancellationType(S6aConstants.CANCELLATION_TYPE_SUBSCRIPTION_WITHDRAWAL);
        out.setClrFlags(2L);

        // WHEN
        final var in = (CancelLocationRequest.In) roundTrip(out);

        // THEN
        assertThat(in.getCancellationType()).isEqualTo(S6aConstants.CANCELLATION_TYPE_SUBSCRIPTION_WITHDRAWAL);
        assertThat(in.getClrFlags()).isEqualTo(2L);
    }

    @Test
    void it_round_trips_an_idr_with_subscription_data_and_flags() throws Exception {
        // GIVEN
        final var out = new InsertSubscriberDataRequest.Out();
        out.setOriginHost("hss.example.com");
        out.setOriginRealm("example.com");
        out.setUserName("001010000000001");
        out.setIdrFlags(4L);
        out.setSubscriptionData(List.of(
            AVP.create(new AVPKey(S6aConstants.AVP_SUBSCRIBER_STATUS, V), 0)));

        // WHEN
        final var in = (InsertSubscriberDataRequest.In) roundTrip(out);

        // THEN
        assertThat(in.getIdrFlags()).isEqualTo(4L);
        assertThat(in.getSubscriptionData()).isNotNull();
    }

    @Test
    void it_round_trips_a_pur_and_a_pua() throws Exception {
        // GIVEN
        final var request = new PurgeUeRequest.Out();
        request.setOriginHost("mme.example.com");
        request.setOriginRealm("example.com");
        request.setUserName("001010000000001");

        final var answer = new PurgeUeAnswer.Out(HOP_BY_HOP, END_TO_END);
        answer.setOriginHost("hss.example.com");
        answer.setOriginRealm("example.com");
        answer.setResultCode(2001L);
        answer.setPuaFlags(1L);

        // WHEN / THEN
        assertThat(roundTrip(request)).isInstanceOf(PurgeUeRequest.In.class);
        final var in = (PurgeUeAnswer.In) roundTrip(answer);
        assertThat(in.getPuaFlags()).isEqualTo(1L);
    }

    @Test
    void it_round_trips_a_nor_and_a_noa() throws Exception {
        // GIVEN
        final var request = new NotifyRequest.Out();
        request.setOriginHost("mme.example.com");
        request.setOriginRealm("example.com");
        request.setUserName("001010000000001");

        final var answer = new NotifyAnswer.Out(HOP_BY_HOP, END_TO_END);
        answer.setOriginHost("hss.example.com");
        answer.setOriginRealm("example.com");
        answer.setResultCode(2001L);

        // WHEN / THEN
        assertThat(roundTrip(request)).isInstanceOf(NotifyRequest.In.class);
        assertThat(roundTrip(answer)).isInstanceOf(NotifyAnswer.In.class);
    }

    private static Command roundTrip(final OutgoingRequest<?> request) throws Exception {
        final var baos = new ByteArrayOutputStream();
        request.writeTo(new DataOutputStream(baos), HOP_BY_HOP, END_TO_END);
        return (Command) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    private static Command roundTrip(final OutgoingAnswer answer) throws Exception {
        final var baos = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(baos));
        return (Command) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }
}
