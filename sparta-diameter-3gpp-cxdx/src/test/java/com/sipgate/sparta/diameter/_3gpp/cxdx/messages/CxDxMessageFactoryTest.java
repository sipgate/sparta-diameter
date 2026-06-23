package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CxDxMessageFactoryTest {

    private final CxDxMessageFactory factory = new CxDxMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_each_command_code_to_the_matching_incoming_command() {
        // GIVEN/WHEN/THEN requests
        assertThat(factory.createForParsing(CxDxConstants.CMD_SERVER_ASSIGNMENT, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false))
            .isInstanceOf(ServerAssignmentRequest.In.class);
        assertThat(factory.createForParsing(CxDxConstants.CMD_MULTIMEDIA_AUTH, CxDxConstants.APP_ID_CX_DX, false, hopByHop, endToEnd, false))
            .isInstanceOf(MultimediaAuthAnswer.In.class);
        assertThat(factory.createForParsing(CxDxConstants.CMD_REGISTRATION_TERMINATION, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false))
            .isInstanceOf(RegistrationTerminationRequest.In.class);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN an unknown command code / WHEN parsing / THEN null
        assertThat(factory.createForParsing(9999, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(9999, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_returns_null_for_a_foreign_application_id() {
        // GIVEN a foreign application id (SWx = 16777265 shares command codes 301/303/304/305 with Cx/Dx)
        final int swxApplicationId = 16777265;

        // WHEN
        final var parsed = factory.createForParsing(
                CxDxConstants.CMD_SERVER_ASSIGNMENT, swxApplicationId, true, hopByHop, endToEnd, false);
        final var answer = factory.createAnswer(
                CxDxConstants.CMD_SERVER_ASSIGNMENT, swxApplicationId, hopByHop, endToEnd);

        // THEN: the factory declines so SWx messages are not stolen (see AGENTS.md "Diameter Message Factories")
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }

    @Test
    void it_builds_answers_with_auth_session_state_not_maintained() {
        // GIVEN/WHEN an answer is created
        final var saa = factory.createAnswer(CxDxConstants.CMD_SERVER_ASSIGNMENT, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);

        // THEN it is an SAA carrying NO_STATE_MAINTAINED (TS 29.229 §5.3)
        assertThat(saa).isInstanceOf(ServerAssignmentAnswer.Out.class);
        assertThat(((ServerAssignmentAnswer.Out) saa).getAuthSessionState())
            .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
    }
}
