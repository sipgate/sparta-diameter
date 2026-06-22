package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwxMessageFactoryTest {

    private final SwxMessageFactory factory = new SwxMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(1);

    @Test
    void it_parses_each_command_code_to_the_matching_incoming_request() {
        // GIVEN/WHEN/THEN — all four SWx request types
        assertThat(factory.createForParsing(SwxConstants.CMD_MULTIMEDIA_AUTH, SwxConstants.APP_ID_SWX, true, hopByHop, endToEnd, false))
            .isInstanceOf(MultimediaAuthRequest.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_SERVER_ASSIGNMENT, SwxConstants.APP_ID_SWX, true, hopByHop, endToEnd, false))
            .isInstanceOf(ServerAssignmentRequest.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_REGISTRATION_TERMINATION, SwxConstants.APP_ID_SWX, true, hopByHop, endToEnd, false))
            .isInstanceOf(RegistrationTerminationRequest.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_PUSH_PROFILE, SwxConstants.APP_ID_SWX, true, hopByHop, endToEnd, false))
            .isInstanceOf(PushProfileRequest.In.class);
    }

    @Test
    void it_parses_answer_variants_for_all_four_commands() {
        // GIVEN/WHEN/THEN — all four SWx answer types
        assertThat(factory.createForParsing(SwxConstants.CMD_MULTIMEDIA_AUTH, SwxConstants.APP_ID_SWX, false, hopByHop, endToEnd, false))
            .isInstanceOf(MultimediaAuthAnswer.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_SERVER_ASSIGNMENT, SwxConstants.APP_ID_SWX, false, hopByHop, endToEnd, false))
            .isInstanceOf(ServerAssignmentAnswer.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_REGISTRATION_TERMINATION, SwxConstants.APP_ID_SWX, false, hopByHop, endToEnd, false))
            .isInstanceOf(RegistrationTerminationAnswer.In.class);
        assertThat(factory.createForParsing(SwxConstants.CMD_PUSH_PROFILE, SwxConstants.APP_ID_SWX, false, hopByHop, endToEnd, false))
            .isInstanceOf(PushProfileAnswer.In.class);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN an unknown command code
        // WHEN
        final IncomingCommand parsed = factory.createForParsing(9999, SwxConstants.APP_ID_SWX, true, hopByHop, endToEnd, false);
        final OutgoingAnswer answer = factory.createAnswer(9999, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);

        // THEN
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }

    @Test
    void it_returns_null_for_a_foreign_application_id() {
        // GIVEN a foreign application id (Cx/Dx = 16777216)
        final int cxApplicationId = 16777216;

        // WHEN
        final IncomingCommand parsed = factory.createForParsing(
                SwxConstants.CMD_SERVER_ASSIGNMENT, cxApplicationId, true,
                new HopByHopId(1), new EndToEndId(1), false);
        final OutgoingAnswer answer = factory.createAnswer(
                SwxConstants.CMD_SERVER_ASSIGNMENT, cxApplicationId,
                new HopByHopId(1), new EndToEndId(1));

        // THEN
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }

    @Test
    void it_builds_answers_with_auth_session_state_not_maintained() {
        // GIVEN/WHEN an answer is created
        final var saa = factory.createAnswer(SwxConstants.CMD_SERVER_ASSIGNMENT, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);

        // THEN it carries NO_STATE_MAINTAINED (TS 29.273 §8 reuses Cx/Dx semantics)
        assertThat(saa).isInstanceOf(ServerAssignmentAnswer.Out.class);
        assertThat(((ServerAssignmentAnswer.Out) saa).getAuthSessionState())
            .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
    }

    @Test
    void it_builds_outgoing_answers_for_all_four_swx_commands() {
        // GIVEN/WHEN/THEN — all four SWx outgoing answer types
        assertThat(factory.createAnswer(SwxConstants.CMD_MULTIMEDIA_AUTH, SwxConstants.APP_ID_SWX, hopByHop, endToEnd))
            .isInstanceOf(MultimediaAuthAnswer.Out.class);
        assertThat(factory.createAnswer(SwxConstants.CMD_SERVER_ASSIGNMENT, SwxConstants.APP_ID_SWX, hopByHop, endToEnd))
            .isInstanceOf(ServerAssignmentAnswer.Out.class);
        assertThat(factory.createAnswer(SwxConstants.CMD_REGISTRATION_TERMINATION, SwxConstants.APP_ID_SWX, hopByHop, endToEnd))
            .isInstanceOf(RegistrationTerminationAnswer.Out.class);
        assertThat(factory.createAnswer(SwxConstants.CMD_PUSH_PROFILE, SwxConstants.APP_ID_SWX, hopByHop, endToEnd))
            .isInstanceOf(PushProfileAnswer.Out.class);
    }
}
