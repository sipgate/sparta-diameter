package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.annotations.DiameterResponse;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageFactoryTest {

    private static final String MESSAGES_PACKAGE = "com.sipgate.sparta.diameter.base.messages";
    private static final HopByHopId DUMMY_HOP_BY_HOP = new HopByHopId(0xAB12);
    private static final EndToEndId DUMMY_END_TO_END = new EndToEndId(0xCD34);

    // -------------------------------------------------------------------------
    // Constructor contract
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("rawtypes")
    void it_all_answer_classes_have_a_private_HopByHopId_EndToEndId_constructor() throws Exception {
        final Set<Class<?>> answerClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterResponse.class);
        assertThat(answerClasses).isNotEmpty();

        for (final Class<?> cls : answerClasses) {
            assertThat(Answer.class.isAssignableFrom(cls))
                    .as("%s must extend Answer", cls.getSimpleName())
                    .isTrue();

            final Constructor<?> ctor = cls.getDeclaredConstructor(HopByHopId.class, EndToEndId.class);
            assertThat(Modifier.isPrivate(ctor.getModifiers()))
                    .as("%s constructor(HopByHopId, EndToEndId) must be private", cls.getSimpleName())
                    .isTrue();
        }
    }

    // -------------------------------------------------------------------------
    // Factory instantiation
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void it_can_create_all_registered_request_types() {
        final Set<Class<?>> requestClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterRequest.class);
        assertThat(requestClasses).isNotEmpty();

        for (final Class<?> cls : requestClasses) {
            final IncomingCommand request = DiameterMessageFactory.createForParsing(
                    cls, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END, false);

            assertThat(request)
                    .as("%s.createForParsing should produce a non-null instance", cls.getSimpleName())
                    .isNotNull();
            assertThat(request.hopByHopId())
                    .as("%s hop-by-hop", cls.getSimpleName())
                    .isEqualTo(DUMMY_HOP_BY_HOP);
            assertThat(request.endToEndId())
                    .as("%s end-to-end", cls.getSimpleName())
                    .isEqualTo(DUMMY_END_TO_END);
            assertThat(((Command<?>) request).isRequest())
                    .as("%s R-bit", cls.getSimpleName())
                    .isTrue();
            assertThat(((Command<?>) request).isRetransmitted())
                    .as("%s T-bit", cls.getSimpleName())
                    .isFalse();
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void it_can_create_retransmitted_variants_of_all_registered_request_types() {
        final Set<Class<?>> requestClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterRequest.class);
        assertThat(requestClasses).isNotEmpty();

        for (final Class<?> cls : requestClasses) {
            final IncomingCommand request = DiameterMessageFactory.createForParsing(
                    cls, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END, true);

            assertThat(((Command<?>) request).isRetransmitted())
                    .as("%s T-bit must be set", cls.getSimpleName())
                    .isTrue();
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void it_can_create_answers_for_all_registered_request_types() {
        // Build commandCode → In-request class map so we can pair each answer with its request.
        final Map<Integer, Class> requestByCode = new HashMap<>();
        for (final Class<?> cls : new Reflections(MESSAGES_PACKAGE).getTypesAnnotatedWith(DiameterRequest.class)) {
            requestByCode.put(cls.getAnnotation(DiameterRequest.class).value(), cls);
        }

        final Set<Class<?>> answerClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterResponse.class);
        assertThat(answerClasses).isNotEmpty();

        for (final Class<?> cls : answerClasses) {
            final int commandCode = cls.getAnnotation(DiameterResponse.class).value();
            final Class requestClass = requestByCode.get(commandCode);
            assertThat(requestClass)
                    .as("No @DiameterRequest found for command code %d (needed for %s)", commandCode, cls.getSimpleName())
                    .isNotNull();

            final IncomingRequest request = (IncomingRequest) DiameterMessageFactory.createForParsing(
                    requestClass, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END, false);
            final Answer<?> answer = DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_SUCCESS);

            assertThat(answer)
                    .as("%s answer should be non-null", cls.getSimpleName())
                    .isNotNull();
            assertThat(((OutgoingAnswer<?>) answer).hopByHopId())
                    .as("%s hop-by-hop must match request", cls.getSimpleName())
                    .isEqualTo(DUMMY_HOP_BY_HOP);
            assertThat(((OutgoingAnswer<?>) answer).endToEndId())
                    .as("%s end-to-end must match request", cls.getSimpleName())
                    .isEqualTo(DUMMY_END_TO_END);
            assertThat(answer.getResultCode())
                    .as("%s result code", cls.getSimpleName())
                    .isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
            assertThat(((Command<?>) answer).isRequest())
                    .as("%s R-bit must be clear", cls.getSimpleName())
                    .isFalse();
        }
    }
}
