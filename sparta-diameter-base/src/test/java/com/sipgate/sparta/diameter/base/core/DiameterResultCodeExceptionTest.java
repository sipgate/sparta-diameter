package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
import org.junit.jupiter.api.Test;

import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED;
import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_UNSUPPORTED_VERSION;
import static org.assertj.core.api.Assertions.assertThat;

class DiameterResultCodeExceptionTest {

    @Test
    void it_extends_DiameterException() {
        // GIVEN
        final DiameterResultCodeException exception = new DiameterResultCodeException(
                RES_DIAMETER_UNSUPPORTED_VERSION,
                257, true, 0, new HopByHopId(1), new EndToEndId(2), null);

        // THEN
        assertThat(exception).isInstanceOf(DiameterException.class);
    }

    @Test
    void it_carries_all_header_fields() {
        // GIVEN
        final HopByHopId hopByHop = new HopByHopId(0xABCD);
        final EndToEndId endToEnd = new EndToEndId(0x1234);
        final String sessionId = "some-session-id";

        // WHEN
        final DiameterResultCodeException exception = new DiameterResultCodeException(
                RES_DIAMETER_UNSUPPORTED_VERSION,
                257, true, 16777251, hopByHop, endToEnd, sessionId);

        // THEN
        assertThat(exception.getResultCode()).isEqualTo(RES_DIAMETER_UNSUPPORTED_VERSION);
        assertThat(exception.getCommandCode()).isEqualTo(257);
        assertThat(exception.isProxiable()).isTrue();
        assertThat(exception.getApplicationId()).isEqualTo(16777251);
        assertThat(exception.getHopByHop()).isEqualTo(hopByHop);
        assertThat(exception.getEndToEnd()).isEqualTo(endToEnd);
        assertThat(exception.getSessionId()).isEqualTo(sessionId);
    }

    @Test
    void AVPParseException_extends_DiameterResultCodeException() {
        // GIVEN
        final AVP stub = AVP.createRaw(new com.sipgate.sparta.diameter.base.core.avp.AVPKey(9999, 0), false, true, false, new byte[0]);
        final AVPParseException exception = new AVPParseException(
                RES_DIAMETER_AVP_UNSUPPORTED,
                257, false, 0, new HopByHopId(1), new EndToEndId(2),
                stub, null);

        // THEN — inheritance chain is correct
        assertThat(exception)
            .isInstanceOf(DiameterResultCodeException.class)
            .isInstanceOf(DiameterException.class);
    }

    @Test
    void AVPParseException_carries_offending_avp() {
        // GIVEN
        final AVP offendingAvp = AVP.createRaw(new com.sipgate.sparta.diameter.base.core.avp.AVPKey(9999, 0), false, true, false, new byte[4]);

        // WHEN
        final AVPParseException exception = new AVPParseException(
                RES_DIAMETER_AVP_UNSUPPORTED,
                280, false, 0, new HopByHopId(10), new EndToEndId(20),
                offendingAvp, null);

        // THEN
        assertThat(exception.getOffendingAvp()).isSameAs(offendingAvp);
        assertThat(exception.getResultCode()).isEqualTo(RES_DIAMETER_AVP_UNSUPPORTED);
        assertThat(exception.getCommandCode()).isEqualTo(280);
    }
}
