package com.sipgate.sparta.diameter._3gpp.gx.outside;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter._3gpp.gx.messages.GxReAuthAnswer;
import com.sipgate.sparta.diameter._3gpp.gx.messages.GxReAuthRequest;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Lives outside the messages package: the Gx RAR and RAA must be constructible by consumers. */
class GxReAuthVisibilityTest {

    @Test
    void it_lets_other_packages_build_a_gx_rar_and_raa() {
        // GIVEN/WHEN
        final var request = new GxReAuthRequest.In(new HopByHopId(1), new EndToEndId(2), false);
        final var incomingAnswer = new GxReAuthAnswer.In(new HopByHopId(1), new EndToEndId(2));
        final var outgoingAnswer = new GxReAuthAnswer.Out(new HopByHopId(1), new EndToEndId(2));

        // THEN
        assertThat(request.getApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
        assertThat(incomingAnswer.getApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
        assertThat(outgoingAnswer.getApplicationId()).isEqualTo(GxConstants.APP_ID_GX);
    }
}
