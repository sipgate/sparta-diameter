package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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
        final OutgoingAnswer<?> answer = DiameterMessageFactory.createAnswer(request, RES_DIAMETER_SUCCESS);

        // THEN
        assertThat(answer).isInstanceOf(DeviceWatchdogAnswer.Out.class);
        assertThat(answer.hopByHopId()).isEqualTo(HOP);
        assertThat(answer.endToEndId()).isEqualTo(END);
        assertThat(answer.getResultCode()).isEqualTo(RES_DIAMETER_SUCCESS);
        assertThat(answer.isRequest()).isFalse();
    }

    @Nested
    class SessionId {
        private DeviceWatchdogRequest.In request;

        @BeforeEach
        void setUp() {
            request = spy((DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(
                    DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, false));
        }

        @Nested
        class HasSessionId {
            static final String SESSION_ID = "some-session-id";

            @BeforeEach
            void setUp() {
                when(request.getSessionId()).thenReturn(SESSION_ID);
            }

            @Test
            void create_answer_sets_session_id() {
                // GIVEN: see setup
                // WHEN
                final var answer = DiameterMessageFactory.createAnswer(request, RES_DIAMETER_SUCCESS);

                // THEN
                assertThat(answer.getSessionId()).isEqualTo(SESSION_ID);
            }

            @Test
            void create_error_answer_sets_session_id() {
                // GIVEN: see setup
                // WHEN
                final var answer = DiameterMessageFactory.createErrorAnswer(request, RES_DIAMETER_SUCCESS);

                // THEN
                assertThat(answer).isInstanceOf(ErrorAnswer.class);
                assertThat(answer.getSessionId()).isEqualTo(SESSION_ID);
            }

            @Test
            void create_error_answer_for_result_code_exception_sets_session_id() {
                // GIVEN: a cause with session id
                final var cause = new DiameterResultCodeException(RES_DIAMETER_SUCCESS, 123, false, 456, HOP, END, SESSION_ID);

                // WHEN
                final var answer = DiameterMessageFactory.createErrorAnswer(cause);

                // THEN
                assertThat(answer).isInstanceOf(ErrorAnswer.class);
                assertThat(answer.getSessionId()).isEqualTo(SESSION_ID);
            }
        }

        @Nested
        class NoSessionId {
            @BeforeEach
            void setUp() {
                when(request.getSessionId()).thenReturn(null);
            }

            @Test
            void create_answer_does_not_set_session_id() {
                // GIVEN: see setup
                // WHEN
                final var answer = DiameterMessageFactory.createAnswer(request, RES_DIAMETER_SUCCESS);

                // THEN
                assertThat(answer.getSessionId()).isNull();
            }

            @Test
            void create_error_answer_does_not_set_session_id() {
                // GIVEN: see setup
                // WHEN
                final var answer = DiameterMessageFactory.createErrorAnswer(request, RES_DIAMETER_SUCCESS);

                // THEN
                assertThat(answer).isInstanceOf(ErrorAnswer.class);
                assertThat(answer.getSessionId()).isNull();
            }

            @Test
            void create_error_answer_for_result_code_exception_does_not_set_session_id() {
                // GIVEN: a cause without session id
                final var cause = new DiameterResultCodeException(RES_DIAMETER_SUCCESS, 123, false, 456, HOP, END, null);

                // WHEN
                final var answer = DiameterMessageFactory.createErrorAnswer(cause);

                // THEN
                assertThat(answer).isInstanceOf(ErrorAnswer.class);
                assertThat(answer.getSessionId()).isNull();
            }
        }
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
