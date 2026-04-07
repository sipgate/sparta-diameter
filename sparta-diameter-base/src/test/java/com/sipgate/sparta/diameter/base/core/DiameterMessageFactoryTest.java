package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageFactoryTest {

    private static final HopByHopId HOP = new HopByHopId(0xAB12);
    private static final EndToEndId END = new EndToEndId(0xCD34);

    @Test
    void it_creates_an_incoming_request_by_command_code() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(result.hopByHopId()).isEqualTo(HOP);
        assertThat(result.endToEndId()).isEqualTo(END);
        assertThat(((Command<?>) result).isRequest()).isTrue();
        assertThat(((Command<?>) result).isRetransmitted()).isFalse();
    }

    @Test
    void it_creates_a_retransmitted_request() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, true);

        // THEN
        assertThat(((Command<?>) result).isRetransmitted()).isTrue();
    }

    @Test
    void it_creates_an_incoming_answer_by_command_code() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, false, true, false, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(DeviceWatchdogAnswer.In.class);
        assertThat(((Command<?>) result).isRequest()).isFalse();
    }

    @Test
    void it_creates_an_outgoing_answer_from_an_incoming_request() {
        // GIVEN
        final DeviceWatchdogRequest.In request = (DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(
                        DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, false);

        // WHEN
        final OutgoingAnswer<?> answer = DiameterMessageFactory.createAnswer(
                request, DiameterConstants.RES_DIAMETER_SUCCESS);

        // THEN
        assertThat(answer).isInstanceOf(DeviceWatchdogAnswer.Out.class);
        assertThat(answer.hopByHopId()).isEqualTo(HOP);
        assertThat(answer.endToEndId()).isEqualTo(END);
        assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
        assertThat(((Command<?>) answer).isRequest()).isFalse();
    }

    @Test
    void it_creates_ErrorAnswer_In_when_isError_is_true() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_RE_AUTH, 0, false, true, true, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(ErrorAnswer.In.class);
        assertThat(result.hopByHopId()).isEqualTo(HOP);
        assertThat(result.endToEndId()).isEqualTo(END);
    }

    @Test
    void it_returns_a_GenericCommand_for_unknown_command_code() {
        // GIVEN
        final var commandCode = 99999;

        // WHEN
        final var result = DiameterMessageFactory.createForParsing(
            commandCode, 0, true, true, false, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(GenericCommand.In.class);
        assertThat(result.getCommandCode()).isEqualTo(commandCode);
        assertThat(result.hopByHopId()).isEqualTo(HOP);
        assertThat(result.endToEndId()).isEqualTo(END);
    }
}
