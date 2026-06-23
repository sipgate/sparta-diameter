package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class S6aMessageFactoryTest {

    private final S6aMessageFactory factory = new S6aMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_each_command_code_to_the_matching_incoming_command() {
        // GIVEN/WHEN/THEN
        assertThat(factory.createForParsing(S6aConstants.CMD_UPDATE_LOCATION, S6aConstants.APP_ID_S6A_S6D, true, hopByHop, endToEnd, false))
            .isInstanceOf(UpdateLocationRequest.In.class);
        assertThat(factory.createForParsing(S6aConstants.CMD_CANCEL_LOCATION, S6aConstants.APP_ID_S6A_S6D, false, hopByHop, endToEnd, false))
            .isInstanceOf(CancelLocationAnswer.In.class);
        assertThat(factory.createForParsing(S6aConstants.CMD_PURGE_UE, S6aConstants.APP_ID_S6A_S6D, true, hopByHop, endToEnd, false))
            .isInstanceOf(PurgeUeRequest.In.class);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN an unknown command code / WHEN parsing / THEN null
        assertThat(factory.createForParsing(9999, S6aConstants.APP_ID_S6A_S6D, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(9999, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_returns_null_for_a_foreign_application_id() {
        // GIVEN a foreign application id (Cx/Dx = 16777216)
        final int cxDxApplicationId = 16777216;

        // WHEN
        final IncomingCommand parsed = factory.createForParsing(
                S6aConstants.CMD_UPDATE_LOCATION, cxDxApplicationId, true, hopByHop, endToEnd, false);
        final OutgoingAnswer answer = factory.createAnswer(
                S6aConstants.CMD_UPDATE_LOCATION, cxDxApplicationId, hopByHop, endToEnd);

        // THEN: the factory declines so foreign messages are not stolen (see AGENTS.md \"Diameter Message Factories\")
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }
}
