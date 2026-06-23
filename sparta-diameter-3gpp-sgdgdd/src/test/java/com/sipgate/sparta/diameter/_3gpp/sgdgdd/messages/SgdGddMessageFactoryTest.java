package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SgdGddMessageFactoryTest {

    private final SgdGddMessageFactory factory = new SgdGddMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_each_command_code_to_the_matching_incoming_command() {
        // GIVEN/WHEN/THEN
        assertThat(factory.createForParsing(SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE, SgdGddConstants.APP_ID_SGD_GDD, true, hopByHop, endToEnd, false))
            .isInstanceOf(MoForwardShortMessageRequest.In.class);
        assertThat(factory.createForParsing(SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE, SgdGddConstants.APP_ID_SGD_GDD, false, hopByHop, endToEnd, false))
            .isInstanceOf(MtForwardShortMessageAnswer.In.class);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN an unknown command code / WHEN parsing / THEN null
        assertThat(factory.createForParsing(9999, SgdGddConstants.APP_ID_SGD_GDD, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(9999, SgdGddConstants.APP_ID_SGD_GDD, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_returns_null_for_a_foreign_application_id() {
        // GIVEN a foreign application id (Cx/Dx = 16777216)
        final int cxDxApplicationId = 16777216;

        // WHEN
        final IncomingCommand parsed = factory.createForParsing(
                SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE, cxDxApplicationId, true, hopByHop, endToEnd, false);
        final OutgoingAnswer answer = factory.createAnswer(
                SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE, cxDxApplicationId, hopByHop, endToEnd);

        // THEN: the factory declines so foreign messages are not stolen (see AGENTS.md \"Diameter Message Factories\")
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }
}
