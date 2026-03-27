package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilitiesExchangeRequestTest {

    private static final HopByHopId HBH = new HopByHopId(12345);
    private static final EndToEndId E2E = new EndToEndId(67890);

    @Test
    void it_creates_normal_answer_with_success_result_code() {
        // GIVEN
        final IncomingRequest request = (IncomingRequest)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, 0, true, HBH, E2E, false);
        final long successCode = DiameterConstants.RES_DIAMETER_SUCCESS;

        // WHEN
        final CapabilitiesExchangeAnswer.Out answer = DiameterMessageFactory.createAnswer(request, successCode);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(successCode);
        assertThat(answer.hopByHopId()).isEqualTo(HBH);
        assertThat(answer.endToEndId()).isEqualTo(E2E);
    }

    @Test
    void it_creates_request_with_correct_identifiers() {
        // GIVEN / WHEN
        final CapabilitiesExchangeRequest.In request = (CapabilitiesExchangeRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, 0, true, HBH, E2E, false);

        // THEN
        assertThat(request.hopByHopId()).isEqualTo(HBH);
        assertThat(request.endToEndId()).isEqualTo(E2E);
        assertThat(request.isRequest()).isTrue();
        assertThat(request.isProxiable()).isFalse();
        assertThat(request.isRetransmitted()).isFalse();
    }

    @Test
    void it_creates_retransmitted_request_with_correct_flags() {
        // GIVEN / WHEN
        final CapabilitiesExchangeRequest.In request = (CapabilitiesExchangeRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, 0, true, HBH, E2E, true);

        // THEN
        assertThat(request.hopByHopId()).isEqualTo(HBH);
        assertThat(request.endToEndId()).isEqualTo(E2E);
        assertThat(request.isRequest()).isTrue();
        assertThat(request.isProxiable()).isFalse();
        assertThat(request.isRetransmitted()).isTrue();
    }
}
