package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class S6aMessageFactoryTest {

    private final S6aMessageFactory factory = new S6aMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(0x11111111);
    private final EndToEndId endToEnd = new EndToEndId(0x22222222);

    @Test
    void it_creates_the_matching_incoming_request_for_each_command_code() {
        // WHEN / THEN
        assertThat(parseRequest(S6aConstants.CMD_UPDATE_LOCATION)).isInstanceOf(UpdateLocationRequest.In.class);
        assertThat(parseRequest(S6aConstants.CMD_CANCEL_LOCATION)).isInstanceOf(CancelLocationRequest.In.class);
        assertThat(parseRequest(S6aConstants.CMD_AUTHENTICATION_INFORMATION)).isInstanceOf(AuthenticationInformationRequest.In.class);
        assertThat(parseRequest(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA)).isInstanceOf(InsertSubscriberDataRequest.In.class);
        assertThat(parseRequest(S6aConstants.CMD_PURGE_UE)).isInstanceOf(PurgeUeRequest.In.class);
        assertThat(parseRequest(S6aConstants.CMD_NOTIFY)).isInstanceOf(NotifyRequest.In.class);
    }

    @Test
    void it_creates_the_matching_incoming_answer_for_each_command_code() {
        // WHEN / THEN
        assertThat(parseAnswer(S6aConstants.CMD_UPDATE_LOCATION)).isInstanceOf(UpdateLocationAnswer.In.class);
        assertThat(parseAnswer(S6aConstants.CMD_CANCEL_LOCATION)).isInstanceOf(CancelLocationAnswer.In.class);
        assertThat(parseAnswer(S6aConstants.CMD_AUTHENTICATION_INFORMATION)).isInstanceOf(AuthenticationInformationAnswer.In.class);
        assertThat(parseAnswer(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA)).isInstanceOf(InsertSubscriberDataAnswer.In.class);
        assertThat(parseAnswer(S6aConstants.CMD_PURGE_UE)).isInstanceOf(PurgeUeAnswer.In.class);
        assertThat(parseAnswer(S6aConstants.CMD_NOTIFY)).isInstanceOf(NotifyAnswer.In.class);
    }

    @Test
    void it_creates_outgoing_answers_with_auth_session_state_not_maintained() {
        // WHEN
        final OutgoingAnswer answer = factory.createAnswer(
                S6aConstants.CMD_UPDATE_LOCATION, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);

        // THEN
        assertThat(answer).isInstanceOf(UpdateLocationAnswer.Out.class);
        assertThat(((UpdateLocationAnswer.Out) answer).getAuthSessionState())
                .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // WHEN / THEN
        assertThat(factory.createForParsing(99999, S6aConstants.APP_ID_S6A_S6D, true, hopByHop, endToEnd, false))
                .isNull();
        assertThat(factory.createAnswer(99999, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd))
                .isNull();
    }

    private IncomingCommand parseRequest(final int commandCode) {
        return factory.createForParsing(commandCode, S6aConstants.APP_ID_S6A_S6D, true, hopByHop, endToEnd, false);
    }

    private IncomingCommand parseAnswer(final int commandCode) {
        return factory.createForParsing(commandCode, S6aConstants.APP_ID_S6A_S6D, false, hopByHop, endToEnd, false);
    }
}
