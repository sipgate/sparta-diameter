package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CcMessageFactoryTest {

    private static final int FOREIGN_APPLICATION_ID = 16777216;

    private final CcMessageFactory factory = new CcMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_a_request_to_an_incoming_ccr() {
        // GIVEN/WHEN
        final var parsed = factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, CcConstants.APP_ID_CREDIT_CONTROL, true, hopByHop, endToEnd, false);

        // THEN
        assertThat(parsed).isInstanceOf(CreditControlRequest.In.class);
    }

    @Test
    void it_parses_an_answer_to_an_incoming_cca() {
        // GIVEN/WHEN
        final var parsed = factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, CcConstants.APP_ID_CREDIT_CONTROL, false, hopByHop, endToEnd, false);

        // THEN
        assertThat(parsed).isInstanceOf(CreditControlAnswer.In.class);
    }

    @Test
    void it_creates_a_cca_that_carries_the_credit_control_application_id() {
        // GIVEN/WHEN
        final var answer = factory.createAnswer(
            CcConstants.CMD_CREDIT_CONTROL, CcConstants.APP_ID_CREDIT_CONTROL, hopByHop, endToEnd);

        // THEN RFC 4006 §3.2 requires Auth-Application-Id in every CCA
        assertThat(answer).isInstanceOf(CreditControlAnswer.Out.class);
        assertThat(((CreditControlAnswer.Out) answer).getAuthApplicationId())
            .isEqualTo(CcConstants.APP_ID_CREDIT_CONTROL);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN/WHEN/THEN
        assertThat(factory.createForParsing(9999, CcConstants.APP_ID_CREDIT_CONTROL, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(9999, CcConstants.APP_ID_CREDIT_CONTROL, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_returns_null_for_a_foreign_application_id() {
        // GIVEN/WHEN
        final var parsed = factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, FOREIGN_APPLICATION_ID, true, hopByHop, endToEnd, false);
        final var answer = factory.createAnswer(
            CcConstants.CMD_CREDIT_CONTROL, FOREIGN_APPLICATION_ID, hopByHop, endToEnd);

        // THEN the factory declines so it does not steal other applications' messages
        assertThat(parsed).isNull();
        assertThat(answer).isNull();
    }
}
