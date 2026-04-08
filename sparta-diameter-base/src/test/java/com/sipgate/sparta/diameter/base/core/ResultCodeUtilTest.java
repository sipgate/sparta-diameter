package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResultCodeUtilTest {

    @Test
    void it_identifies_error_codes_correctly() {
        // GIVEN
        final long successCode = DiameterConstants.RES_DIAMETER_SUCCESS;
        final long informationalCode = DiameterConstants.RES_DIAMETER_MULTI_ROUND_AUTH;
        final long protocolError = DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED;
        final long transientFailure = DiameterConstants.RES_DIAMETER_AUTHENTICATION_REJECTED;
        final long permanentFailure = DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED;

        // WHEN & THEN
        assertThat(ResultCodeUtil.isErrorCode(successCode)).isFalse();
        assertThat(ResultCodeUtil.isErrorCode(informationalCode)).isFalse();
        assertThat(ResultCodeUtil.isErrorCode(protocolError)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(transientFailure)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(permanentFailure)).isTrue();
    }

    @Test
    void it_handles_edge_cases_for_error_detection() {
        // GIVEN
        final long justBeforeErrors = 2999;
        final long firstError = 3000;
        final long lastError = 5999;
        final long justAfterErrors = 6000;

        // WHEN & THEN
        assertThat(ResultCodeUtil.isErrorCode(justBeforeErrors)).isFalse();
        assertThat(ResultCodeUtil.isErrorCode(firstError)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(lastError)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(justAfterErrors)).isFalse();
    }

    @Test
    void it_describes_result_code_from_error_answer() {
        // GIVEN
        final var answer = new ErrorAnswer.Out(257, false, 0, new HopByHopId(1), new EndToEndId(1));
        answer.setResultCode(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);

        // WHEN
        final var actual = ResultCodeUtil.describeResultCode(answer);

        // THEN
        assertThat(actual).isEqualTo("Result-Code " + DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);
    }

    @Test
    void it_describes_experimental_result_code_when_present() {
        // GIVEN
        final var answer = new ErrorAnswer.Out(257, false, 0, new HopByHopId(1), new EndToEndId(1));
        answer.setResultCode(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);

        final var vendorId = AVP.create(
                new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), 10415L);
        final var experimentalResultCode = AVP.create(
                new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0), 5001L);
        final var experimentalResult = new GroupedAVP(
                new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT, 0), true, List.of(vendorId, experimentalResultCode));
        answer.setExperimentalResult(experimentalResult);

        // WHEN
        final var actual = ResultCodeUtil.describeResultCode(answer);

        // THEN
        assertThat(actual).isEqualTo("Experimental-Result-Code 5001 (Vendor-Id 10415)");
    }
}
