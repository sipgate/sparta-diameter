package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DeviceWatchdogRequestTest {

    @Test
    void it_creates_normal_answer_with_success_result_code() {
        // GIVEN
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(12345, 67890);
        final int successCode = DiameterConstants.RES_DIAMETER_SUCCESS;

        // WHEN
        final Answer answer = request.createAnswer(successCode);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(successCode);
        assertThat(answer.getHopByHopIdentifier()).isEqualTo(12345);
        assertThat(answer.getEndToEndIdentifier()).isEqualTo(67890);
    }

    @Test
    void it_creates_error_answer_with_protocol_error_result_code() {
        // GIVEN
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(11111, 22222);
        final int protocolErrorCode = DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED;

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
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(33333, 44444);
        final int transientFailureCode = DiameterConstants.RES_DIAMETER_AUTHENTICATION_REJECTED;

        // WHEN
        final Answer answer = request.createAnswer(transientFailureCode);

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(transientFailureCode);
    }

    @Test
    void it_creates_error_answer_with_permanent_failure_result_code() {
        // GIVEN
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(55555, 66666);
        final int permanentFailureCode = DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED;

        // WHEN
        final Answer answer = request.createAnswer(permanentFailureCode);

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(permanentFailureCode);
    }

    @Test
    void it_creates_normal_answer_with_informational_result_code() {
        // GIVEN
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(77777, 88888);
        final int informationalCode = DiameterConstants.RES_DIAMETER_MULTI_ROUND_AUTH;

        // WHEN
        final Answer answer = request.createAnswer(informationalCode);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.getResultCode()).isEqualTo(informationalCode);
    }

    @Test
    void it_creates_request_with_correct_identifiers() {
        // GIVEN
        final int hopByHopId = 12345;
        final int endToEndId = 67890;

        // WHEN
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.create(hopByHopId, endToEndId);

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
        final DeviceWatchdogRequest request = DeviceWatchdogRequest.createRetransmitted(hopByHopId, endToEndId);

        // THEN
        assertThat(request.getHopByHopIdentifier()).isEqualTo(hopByHopId);
        assertThat(request.getEndToEndIdentifier()).isEqualTo(endToEndId);
        assertThat(request.isRequest()).isTrue();
        assertThat(request.isProxiable()).isFalse();
        assertThat(request.isRetransmitted()).isTrue();
    }
}
