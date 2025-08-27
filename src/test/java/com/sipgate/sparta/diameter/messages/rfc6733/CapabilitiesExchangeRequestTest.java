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
        final Answer answer = request.createAnswer(successCode);

        // THEN
        assertThat(answer).isInstanceOf(CapabilitiesExchangeAnswer.class);
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(successCode);
        assertThat(answer.getHopByHopIdentifier()).isEqualTo(12345);
        assertThat(answer.getEndToEndIdentifier()).isEqualTo(67890);
    }

    @Test
    void it_creates_error_answer_with_protocol_error_result_code() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(11111, 22222);
        final long protocolErrorCode = DiameterConstants.RES_DIAMETER_UNSUPPORTED_VERSION;

        // WHEN
        final Answer answer = request.createAnswer(protocolErrorCode);

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(protocolErrorCode);
        assertThat(answer.getHopByHopIdentifier()).isEqualTo(11111);
        assertThat(answer.getEndToEndIdentifier()).isEqualTo(22222);
    }

    @Test
    void it_creates_error_answer_with_transient_failure_result_code() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(33333, 44444);
        final long transientFailureCode = DiameterConstants.RES_DIAMETER_OUT_OF_SPACE;

        // WHEN
        final Answer answer = request.createAnswer(transientFailureCode);

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(transientFailureCode);
    }

    @Test
    void it_creates_error_answer_with_permanent_failure_result_code() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(55555, 66666);
        final long permanentFailureCode = DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION;

        // WHEN
        final Answer answer = request.createAnswer(permanentFailureCode);

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(permanentFailureCode);
    }

    @Test
    void it_creates_normal_answer_with_limited_success_result_code() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(77777, 88888);
        final long limitedSuccessCode = DiameterConstants.RES_DIAMETER_LIMITED_SUCCESS;

        // WHEN
        final Answer answer = request.createAnswer(limitedSuccessCode);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(limitedSuccessCode);
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

    @Test
    void it_handles_edge_case_result_codes_correctly() {
        // GIVEN
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(10001, 20002);
        final int borderlineErrorCode = 3000; // First error code
        final int borderlineSuccessCode = 2999; // Last success code

        // WHEN
        final Answer errorAnswer = request.createAnswer(borderlineErrorCode);
        final Answer successAnswer = request.createAnswer(borderlineSuccessCode);

        // THEN
        assertThat(errorAnswer.isError()).isTrue();
        assertThat(successAnswer.isError()).isFalse();
    }
}
