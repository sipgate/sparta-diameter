package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CapabilitiesExchangeRequestTest {

    @Test
    void it_creates_normal_answer_with_success_result_code() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(12345, 67890);
        final long successCode = DiameterConstants.RES_DIAMETER_SUCCESS;

        // WHEN
        final CapabilitiesExchangeAnswer answer = request.createAnswer(successCode);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(successCode);
        assertThat(answer.getHopByHopIdentifier()).isEqualTo(12345);
        assertThat(answer.getEndToEndIdentifier()).isEqualTo(67890);
    }

    @Test
    void it_creates_request_with_correct_identifiers() {
        // GIVEN
        final int hopByHopId = 12345;
        final int endToEndId = 67890;

        // WHEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(hopByHopId, endToEndId);

        // THEN
        assertThat(request.getHopByHopIdentifier()).isEqualTo(hopByHopId);
        assertThat(request.getEndToEndIdentifier()).isEqualTo(endToEndId);
        assertThat(request.isRequest()).isTrue();
        assertThat(request.isProxiable()).isFalse();
        assertThat(request.isRetransmitted()).isFalse();
    }

    @Test
    void it_creates_retransmitted_request_with_correct_flags() {
        // GIVEN
        final int hopByHopId = 99999;
        final int endToEndId = 11111;

        // WHEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.createRetransmitted(hopByHopId, endToEndId);

        // THEN
        assertThat(request.getHopByHopIdentifier()).isEqualTo(hopByHopId);
        assertThat(request.getEndToEndIdentifier()).isEqualTo(endToEndId);
        assertThat(request.isRequest()).isTrue();
        assertThat(request.isProxiable()).isFalse();
        assertThat(request.isRetransmitted()).isTrue();
    }
}
