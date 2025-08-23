package com.sipgate.sparta.diameter.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ResultCodeUtilTest {

    @Test
    void it_identifies_error_codes_correctly() {
        // GIVEN
        final int successCode = DiameterConstants.RES_DIAMETER_SUCCESS;
        final int informationalCode = DiameterConstants.RES_DIAMETER_MULTI_ROUND_AUTH;
        final int protocolError = DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED;
        final int transientFailure = DiameterConstants.RES_DIAMETER_AUTHENTICATION_REJECTED;
        final int permanentFailure = DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED;

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
        final int justBeforeErrors = 2999;
        final int firstError = 3000;
        final int lastError = 5999;
        final int justAfterErrors = 6000;

        // WHEN & THEN
        assertThat(ResultCodeUtil.isErrorCode(justBeforeErrors)).isFalse();
        assertThat(ResultCodeUtil.isErrorCode(firstError)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(lastError)).isTrue();
        assertThat(ResultCodeUtil.isErrorCode(justAfterErrors)).isFalse();
    }
}
