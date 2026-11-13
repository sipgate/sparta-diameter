package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
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
        assertThat(((Command) result).isRequest()).isTrue();
        assertThat(((Command) result).isRetransmitted()).isFalse();
    }

    @Test
    void it_creates_a_parse_error_answer_for_5001_without_E_bit() {
        // GIVEN: an unknown M-bit AVP in a request (5001 is a permanent failure, RFC 6733 §7.1.5)
        final AVP offending = AVP.createRaw(new AVPKey(1504, 10415), true, true, false, new byte[4]);
        final var cause = new com.sipgate.sparta.diameter.base.core.avp.AVPParseException(
                DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED, 316, true, 16777251, HOP, END, offending, "sid");

        // WHEN
        final OutgoingAnswer answer = DiameterMessageFactory.createParseErrorAnswer(cause);

        // THEN
        assertThat(answer.isError()).isFalse();
        assertThat(answer.isRequest()).isFalse();
        assertThat(answer.isProxiable()).isTrue();
        assertThat(answer.getCommandCode()).isEqualTo(316);
        assertThat(answer.getApplicationId()).isEqualTo(16777251);
        assertThat(answer.hopByHopId()).isEqualTo(HOP);
        assertThat(answer.endToEndId()).isEqualTo(END);
        assertThat(answer.getSessionId()).isEqualTo("sid");
        assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED);
        assertThat(answer.findAVP(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0))).isNotNull();
    }

    @Test
    void it_creates_a_parse_error_answer_for_a_3xxx_protocol_error_with_E_bit() {
        // GIVEN
        final var cause = new DiameterResultCodeException(
                DiameterConstants.RES_DIAMETER_INVALID_HDR_BITS, 316, true, 16777251, HOP, END, null);

        // WHEN
        final OutgoingAnswer answer = DiameterMessageFactory.createParseErrorAnswer(cause);

        // THEN
        assertThat(answer).isInstanceOf(ErrorAnswer.Out.class);
        assertThat(answer.isError()).isTrue();
    }

    @Test
    void it_creates_a_retransmitted_request() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, true);

        // THEN
        assertThat(((Command) result).isRetransmitted()).isTrue();
    }

    @Test
    void it_creates_an_incoming_answer_by_command_code() {
        // GIVEN / WHEN
        final IncomingCommand result = DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, false, true, false, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(DeviceWatchdogAnswer.In.class);
        assertThat(((Command) result).isRequest()).isFalse();
    }

    @Test
    void it_creates_an_outgoing_answer_from_an_incoming_request() {
        // GIVEN
        final DeviceWatchdogRequest.In request = (DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(
                        DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, false);

        // WHEN
        final OutgoingAnswer answer = DiameterMessageFactory.createAnswer(request, RES_DIAMETER_SUCCESS);

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

    @Nested
    class ErrorAnswerMirrorsRequestAvps {
        private DeviceWatchdogRequest.In request;

        @BeforeEach
        void setUp() {
            request = spy((DeviceWatchdogRequest.In) DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, HOP, END, false));
        }

        @Test
        void it_copies_auth_session_state_and_vendor_specific_application_id() {
            // GIVEN a request carrying both AVPs, as every 3GPP peer sends them
            // (stubbed: a wire-parsed incoming command cannot be mutated)
            when(request.findAVP(new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0)))
                .thenReturn(AVP.create(
                    new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0),
                    DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED));
            when(request.findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0)))
                .thenReturn(AVP.create(
                    new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0),
                    List.of(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), 16777251L))));

            // WHEN
            final var answer = DiameterMessageFactory.createErrorAnswer(request, RES_DIAMETER_SUCCESS);

            // THEN the E-bit answer is not truncated - the MME needs both
            assertThat(answer.getAuthSessionState())
                .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
            assertThat(answer.getVendorSpecificApplicationId()).isNotNull();
            assertThat(answer.getVendorSpecificApplicationId()
                .findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0)).getDataAsUnsignedInt())
                .isEqualTo(16777251L);
        }

        @Test
        void it_omits_what_the_request_did_not_carry() {
            // GIVEN a request without those AVPs (e.g. a base-protocol command)
            // WHEN
            final var answer = DiameterMessageFactory.createErrorAnswer(request, RES_DIAMETER_SUCCESS);

            // THEN nothing is invented
            assertThat(answer.getAuthSessionState()).isEqualTo(-1);
            assertThat(answer.getVendorSpecificApplicationId()).isNull();
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
    void it_returns_a_GenericRequest_for_an_unknown_request_command_code() {
        // GIVEN
        final var commandCode = 99999;

        // WHEN
        final var result = DiameterMessageFactory.createForParsing(
            commandCode, 0, true, true, false, HOP, END, false);

        // THEN — a real IncomingRequest, so sessions answer or relay it instead of dropping it
        assertThat(result).isInstanceOf(GenericRequest.class);
        assertThat(result.getCommandCode()).isEqualTo(commandCode);
        assertThat(result.hopByHopId()).isEqualTo(HOP);
        assertThat(result.endToEndId()).isEqualTo(END);
    }

    @Test
    void it_returns_a_GenericAnswer_for_an_unknown_answer_command_code() {
        // GIVEN
        final var commandCode = 99999;

        // WHEN
        final var result = DiameterMessageFactory.createForParsing(
            commandCode, 0, false, true, false, HOP, END, false);

        // THEN — an IncomingAnswer, so it completes a pending or relayed request
        assertThat(result).isInstanceOf(GenericAnswer.In.class);
        assertThat(((Command) result).isProxiable()).isTrue();
    }

    @Test
    void it_creates_an_answer_for_a_GenericRequest() {
        // GIVEN
        final var request = new GenericRequest(99999, true, false, 16777217, HOP, END);

        // WHEN
        final GenericAnswer.Out answer =
            DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);

        // THEN
        assertThat(answer.getCommandCode()).isEqualTo(99999);
        assertThat(answer.getApplicationId()).isEqualTo(16777217);
        assertThat(answer.isError()).isFalse();
        assertThat(answer.hopByHopId()).isEqualTo(HOP);
        assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);
    }

    @Test
    void it_creates_an_experimental_result_answer_without_E_bit_for_any_answer_type() {
        // GIVEN — a GenericAnswer does not declare the Experimental-Result mixin
        final var request = new GenericRequest(99999, true, false, 16777251, HOP, END);

        // WHEN
        final GenericAnswer.Out answer = DiameterMessageFactory.createExperimentalResultAnswer(request, 10415L, 5420L);

        // THEN — RFC 6733 §7.6: Experimental-Result answers carry no E-bit
        assertThat(answer.isError()).isFalse();
        assertThat(answer.findAVP(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT, 0))).isNotNull();
        assertThat(answer.findAVP(new AVPKey(DiameterConstants.AVP_RESULT_CODE, 0))).isNull();
    }

    @Nested
    class GenericAnswerMirrorsRequestAvps {

        @Test
        void it_mirrors_auth_session_state_and_vsai_into_an_experimental_result_answer() throws Exception {
            // GIVEN — a command without a typed answer class (e.g. Cx LIR) carrying both AVPs
            final IncomingRequest<?> request = parsedGenericRequest(authSessionState(), vsai());

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createExperimentalResultAnswer(
                    (GenericRequest) request, 10415L, 3003L);

            // THEN — like the typed answers (RFC 6733 §8.11)
            assertThat(answer.findAVP(AUTH_SESSION_STATE_KEY).getDataAsInt())
                    .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
            assertThat(answer.findAVP(VSAI_KEY)).isNotNull();
            assertThat(answer.findAVP(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT, 0))).isNotNull();
        }

        @Test
        void it_mirrors_auth_session_state_into_a_result_code_answer() throws Exception {
            // GIVEN
            final IncomingRequest<?> request = parsedGenericRequest(authSessionState());

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createAnswer(
                    (GenericRequest) request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);

            // THEN — nothing is invented: no VSAI without one in the request
            assertThat(answer.findAVP(AUTH_SESSION_STATE_KEY)).isNotNull();
            assertThat(answer.findAVP(VSAI_KEY)).isNull();
        }

        @Test
        void it_mirrors_the_parsed_auth_session_state_into_a_parse_error_answer() throws Exception {
            // GIVEN — Auth-Session-State parsed before an AVP with an invalid length (5014)
            final RelayForwardFrame frame = new RelayForwardFrame();
            frame.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "sid"));
            frame.addAVP(vsai());
            frame.addAVP(authSessionState());
            final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            frame.writeTo(new java.io.DataOutputStream(bytes), HOP, END);
            final byte[] wire = withTrailingShortAvp(bytes.toByteArray());
            final DiameterResultCodeException cause = catchThrowableOfType(
                    DiameterResultCodeException.class, () -> Command.parseMessage(java.nio.ByteBuffer.wrap(wire)));
            assertThat(cause.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_INVALID_AVP_LENGTH);

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createParseErrorAnswer(cause);

            // THEN — Auth-Session-State only, no VSAI
            assertThat(answer.findAVP(AUTH_SESSION_STATE_KEY).getDataAsInt())
                    .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
            assertThat(answer.findAVP(VSAI_KEY)).isNull();
        }

        @Test
        void it_mirrors_the_parsed_auth_session_state_into_an_E_bit_answer_to_a_parse_failure() throws Exception {
            // GIVEN — Auth-Session-State parsed before an AVP with an invalid length (5014)
            final RelayForwardFrame frame = new RelayForwardFrame();
            frame.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "sid"));
            frame.addAVP(vsai());
            frame.addAVP(authSessionState());
            final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            frame.writeTo(new java.io.DataOutputStream(bytes), HOP, END);
            final byte[] wire = withTrailingShortAvp(bytes.toByteArray());
            final DiameterResultCodeException cause = catchThrowableOfType(
                    DiameterResultCodeException.class, () -> Command.parseMessage(java.nio.ByteBuffer.wrap(wire)));
            assertThat(cause).isNotNull();

            // WHEN — the E-bit answer without Failed-AVP a session subclass can build
            final OutgoingAnswer answer = DiameterMessageFactory.createErrorAnswer(cause);

            // THEN — Auth-Session-State only, no VSAI
            assertThat(answer.findAVP(AUTH_SESSION_STATE_KEY).getDataAsInt())
                    .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
            assertThat(answer.findAVP(VSAI_KEY)).isNull();
        }

        private static final AVPKey AUTH_SESSION_STATE_KEY = new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0);
        private static final AVPKey VSAI_KEY = new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0);

        private static AVP authSessionState() {
            return AVP.create(AUTH_SESSION_STATE_KEY, DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }

        private static AVP vsai() {
            return AVP.create(VSAI_KEY, List.of(
                    AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), 10415L),
                    AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), 16777216L)));
        }

        private static IncomingRequest<?> parsedGenericRequest(final AVP... avps) throws Exception {
            final RelayForwardFrame frame = new RelayForwardFrame();
            frame.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "sid"));
            for (final AVP avp : avps) {
                frame.addAVP(avp);
            }
            final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            frame.writeTo(new java.io.DataOutputStream(bytes), HOP, END);
            return (IncomingRequest<?>) Command.parseMessage(java.nio.ByteBuffer.wrap(bytes.toByteArray()));
        }

        /** Appends an AVP header whose length (4) is below the minimum and patches the message length. */
        private static byte[] withTrailingShortAvp(final byte[] message) {
            final byte[] out = java.util.Arrays.copyOf(message, message.length + 8);
            // AVP code 1 (User-Name), flags M, length 4
            out[message.length + 3] = 1;
            out[message.length + 4] = 0x40;
            out[message.length + 7] = 4;
            final int length = out.length;
            out[1] = (byte) (length >>> 16);
            out[2] = (byte) (length >>> 8);
            out[3] = (byte) length;
            return out;
        }
    }

    @Nested
    class ProxyInfoInLocallyGeneratedAnswers {

        @Test
        void it_copies_every_proxy_info_in_order_into_an_answer() throws Exception {
            // GIVEN
            final IncomingRequest<?> request = requestWithProxyInfos("p1.example.com", "p2.example.com");

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createAnswer(
                    (GenericRequest) request, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);

            // THEN — RFC 6733 §6.2
            assertThat(proxyHosts(answer)).containsExactly("p1.example.com", "p2.example.com");
            assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);
        }

        @Test
        void it_copies_every_proxy_info_in_order_into_an_error_answer() throws Exception {
            // GIVEN
            final IncomingRequest<?> request = requestWithProxyInfos("p1.example.com", "p2.example.com");

            // WHEN
            final ErrorAnswer.Out answer = DiameterMessageFactory.createErrorAnswer(
                    request, DiameterConstants.RES_DIAMETER_UNABLE_TO_DELIVER);

            // THEN
            assertThat(proxyHosts(answer)).containsExactly("p1.example.com", "p2.example.com");
            assertThat(answer.getErrorMessage()).isNull();
        }

        @Test
        void it_copies_every_proxy_info_into_an_experimental_result_answer() throws Exception {
            // GIVEN
            final IncomingRequest<?> request = requestWithProxyInfos("p1.example.com");

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createExperimentalResultAnswer(
                    (GenericRequest) request, 10415L, 5420L);

            // THEN
            assertThat(proxyHosts(answer)).containsExactly("p1.example.com");
        }

        @Test
        void it_adds_no_proxy_info_when_the_request_had_none() throws Exception {
            // GIVEN
            final IncomingRequest<?> request = requestWithProxyInfos();

            // WHEN
            final ErrorAnswer.Out answer = DiameterMessageFactory.createErrorAnswer(
                    request, DiameterConstants.RES_DIAMETER_UNABLE_TO_DELIVER);

            // THEN
            assertThat(proxyHosts(answer)).isEmpty();
        }

        @Test
        void it_copies_parsed_proxy_infos_into_a_parse_error_answer() {
            // GIVEN — Proxy-Info AVPs parsed before the offending AVP
            final AVP offending = AVP.createRaw(new AVPKey(1504, 10415), true, true, false, new byte[4]);
            final var cause = new com.sipgate.sparta.diameter.base.core.avp.AVPParseException(
                    DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED, true, 316, true, 16777251, HOP, END,
                    offending, "sid", List.of(proxyInfo("p1.example.com"), proxyInfo("p2.example.com")), null);

            // WHEN
            final OutgoingAnswer answer = DiameterMessageFactory.createParseErrorAnswer(cause);

            // THEN
            assertThat(proxyHosts(answer)).containsExactly("p1.example.com", "p2.example.com");
        }
    }

    @Test
    void it_records_proxy_infos_parsed_before_an_AVP_parse_error() throws Exception {
        // GIVEN — a known, locally consumed request: Session-Id, Proxy-Info, then an
        // unknown M-bit AVP (5001)
        final DeviceWatchdogRequest.Out frame = new DeviceWatchdogRequest.Out();
        frame.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "sid"));
        frame.addAVP(proxyInfo("p1.example.com"));
        frame.addAVP(AVP.createRaw(new AVPKey(1504, 10415), true, true, false, new byte[4]));
        final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        frame.writeTo(new java.io.DataOutputStream(bytes), HOP, END);

        // WHEN / THEN
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> Command.parseMessage(java.nio.ByteBuffer.wrap(bytes.toByteArray())))
                .isInstanceOfSatisfying(com.sipgate.sparta.diameter.base.core.avp.AVPParseException.class,
                        e -> assertThat(e.getProxyInfos()).hasSize(1));
    }

    @Test
    void it_adds_an_error_message_to_an_error_answer() {
        // GIVEN
        final var request = new GenericRequest(99999, true, false, 16777251, HOP, END);

        // WHEN
        final ErrorAnswer.Out answer = DiameterMessageFactory.createErrorAnswer(
                request, DiameterConstants.RES_DIAMETER_UNABLE_TO_DELIVER, "timeout");

        // THEN
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_UNABLE_TO_DELIVER);
        assertThat(answer.getErrorMessage()).isEqualTo("timeout");
    }

    @Test
    void it_creates_an_E_bit_answer_without_Failed_AVP_from_an_AVP_parse_failure() {
        // GIVEN — 5014 invalid AVP length
        final AVP offending = AVP.createRaw(new AVPKey(1, 0), false, true, false, new byte[0]);
        final var cause = new com.sipgate.sparta.diameter.base.core.avp.AVPParseException(
                DiameterConstants.RES_DIAMETER_INVALID_AVP_LENGTH, 316, true, 16777251, HOP, END, offending, "sid");

        // WHEN
        final OutgoingAnswer answer = DiameterMessageFactory.createErrorAnswer(cause);

        // THEN
        assertThat(answer).isInstanceOf(ErrorAnswer.Out.class);
        assertThat(answer.isError()).isTrue();
        assertThat(answer.getSessionId()).isEqualTo("sid");
        assertThat(answer.getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_INVALID_AVP_LENGTH);
        assertThat(answer.findAVP(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0))).isNull();
    }

    /** Wire-parses an unknown-command request carrying one Proxy-Info per given Proxy-Host. */
    private static IncomingRequest<?> requestWithProxyInfos(final String... proxyHosts) throws Exception {
        final RelayForwardFrame frame = new RelayForwardFrame();
        frame.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), "sid"));
        for (final String proxyHost : proxyHosts) {
            frame.addAVP(proxyInfo(proxyHost));
        }
        final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        frame.writeTo(new java.io.DataOutputStream(bytes), HOP, END);
        return (IncomingRequest<?>) Command.parseMessage(java.nio.ByteBuffer.wrap(bytes.toByteArray()));
    }

    private static AVP proxyInfo(final String proxyHost) {
        return AVP.create(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0), List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_PROXY_HOST, 0), proxyHost),
                AVP.create(new AVPKey(DiameterConstants.AVP_PROXY_STATE, 0), new byte[]{1, 2})));
    }

    private static List<String> proxyHosts(final Command answer) {
        final List<String> hosts = new java.util.ArrayList<>();
        for (final AVP avp : answer.findAVPs(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0))) {
            hosts.add(((com.sipgate.sparta.diameter.base.core.avp.GroupedAVP) avp)
                    .findAVP(new AVPKey(DiameterConstants.AVP_PROXY_HOST, 0)).getDataAsString());
        }
        return hosts;
    }

    /** A minimal outgoing request for an unknown proxiable command, used to build wire input. */
    @SuppressWarnings("rawtypes")
    private static final class RelayForwardFrame extends OutgoingRequest {
        RelayForwardFrame() {
            super(99999, true, 16777251);
        }
    }

    @Test
    void it_keeps_the_P_bit_of_a_received_error_answer() {
        // GIVEN — an answer header with E and P bits set
        final boolean isRequest = false;
        final boolean proxiable = true;
        final boolean isError = true;

        // WHEN
        final var result = DiameterMessageFactory.createForParsing(
            318, 16777251, isRequest, proxiable, isError, HOP, END, false);

        // THEN
        assertThat(result).isInstanceOf(ErrorAnswer.In.class);
        assertThat(((Command) result).isProxiable()).isTrue();
    }
}
