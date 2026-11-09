package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GxMessageFactoryTest {

    private static final int CC_APPLICATION_ID = 4;

    private final GxMessageFactory factory = new GxMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_a_gx_ccr_and_cca() {
        // GIVEN/WHEN
        final var request = factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, GxConstants.APP_ID_GX, true, hopByHop, endToEnd, false);
        final var answer = factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, GxConstants.APP_ID_GX, false, hopByHop, endToEnd, false);

        // THEN
        assertThat(request).isInstanceOf(GxCreditControlRequest.In.class);
        assertThat(answer).isInstanceOf(GxCreditControlAnswer.In.class);
    }

    @Test
    void it_creates_a_cca_that_carries_the_gx_application_id() {
        // GIVEN/WHEN
        final var answer = factory.createAnswer(
            CcConstants.CMD_CREDIT_CONTROL, GxConstants.APP_ID_GX, hopByHop, endToEnd);

        // THEN TS 29.212 section 5.6.3 requires Auth-Application-Id in the CCA
        assertThat(answer).isInstanceOf(GxCreditControlAnswer.Out.class);
        assertThat(((GxCreditControlAnswer.Out) answer).getAuthApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
    }

    @Test
    void it_leaves_re_auth_to_the_base_factory() {
        // GIVEN/WHEN/THEN the base factory answers RAR/RAA of every application, so claiming them here would race it
        assertThat(factory.createForParsing(
            DiameterConstants.CMD_RE_AUTH, GxConstants.APP_ID_GX, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(
            DiameterConstants.CMD_RE_AUTH, GxConstants.APP_ID_GX, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_returns_null_for_the_credit_control_application() {
        // GIVEN/WHEN/THEN the factory declines so it does not steal Gy messages
        assertThat(factory.createForParsing(
            CcConstants.CMD_CREDIT_CONTROL, CC_APPLICATION_ID, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(
            CcConstants.CMD_CREDIT_CONTROL, CC_APPLICATION_ID, hopByHop, endToEnd)).isNull();
    }
}
