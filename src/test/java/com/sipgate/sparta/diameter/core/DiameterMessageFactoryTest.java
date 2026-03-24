package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterMessageFactoryTest {

    private static final String MESSAGES_PACKAGE = "com.sipgate.sparta.diameter.messages";
    private static final int DUMMY_HOP_BY_HOP = 0xAB12;
    private static final int DUMMY_END_TO_END = 0xCD34;

    // -------------------------------------------------------------------------
    // Constructor contract
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("rawtypes")
    void it_all_request_classes_have_a_private_boolean_int_int_constructor() throws Exception {
        final Set<Class<?>> requestClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterRequest.class);
        assertThat(requestClasses).isNotEmpty();

        for (final Class<?> cls : requestClasses) {
            assertThat(Request.class.isAssignableFrom(cls))
                    .as("%s must extend Request", cls.getSimpleName())
                    .isTrue();

            final Constructor<?> ctor = cls.getDeclaredConstructor(boolean.class, int.class, int.class);
            assertThat(Modifier.isPrivate(ctor.getModifiers()))
                    .as("%s constructor(boolean, int, int) must be private", cls.getSimpleName())
                    .isTrue();
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    void it_all_answer_classes_have_a_private_int_int_constructor() throws Exception {
        final Set<Class<?>> answerClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterResponse.class);
        assertThat(answerClasses).isNotEmpty();

        for (final Class<?> cls : answerClasses) {
            assertThat(Answer.class.isAssignableFrom(cls))
                    .as("%s must extend Answer", cls.getSimpleName())
                    .isTrue();

            final Constructor<?> ctor = cls.getDeclaredConstructor(int.class, int.class);
            assertThat(Modifier.isPrivate(ctor.getModifiers()))
                    .as("%s constructor(int, int) must be private", cls.getSimpleName())
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
            final Class requestClass = cls;
            final Request<?, ?> request = DiameterMessageFactory.create(requestClass, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END);

            assertThat(request)
                    .as("%s.create should produce a non-null instance", cls.getSimpleName())
                    .isNotNull();
            assertThat(request.getHopByHopIdentifier())
                    .as("%s hop-by-hop", cls.getSimpleName())
                    .isEqualTo(DUMMY_HOP_BY_HOP);
            assertThat(request.getEndToEndIdentifier())
                    .as("%s end-to-end", cls.getSimpleName())
                    .isEqualTo(DUMMY_END_TO_END);
            assertThat(request.isRequest()).as("%s R-bit", cls.getSimpleName()).isTrue();
            assertThat(request.isRetransmitted()).as("%s T-bit", cls.getSimpleName()).isFalse();
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void it_can_create_retransmitted_variants_of_all_registered_request_types() {
        final Set<Class<?>> requestClasses = new Reflections(MESSAGES_PACKAGE)
                .getTypesAnnotatedWith(DiameterRequest.class);
        assertThat(requestClasses).isNotEmpty();

        for (final Class<?> cls : requestClasses) {
            final Class requestClass = cls;
            final Request<?, ?> request = DiameterMessageFactory.createRetransmitted(
                    requestClass, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END);

            assertThat(request.isRetransmitted())
                    .as("%s T-bit must be set", cls.getSimpleName())
                    .isTrue();
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void it_can_create_answers_for_all_registered_request_types() {
        // Build commandCode → requestClass map so we can pair each answer with its request.
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

            final Request request = DiameterMessageFactory.create(requestClass, DUMMY_HOP_BY_HOP, DUMMY_END_TO_END);
            final Answer<?> answer = DiameterMessageFactory.createAnswer(request, DiameterConstants.RES_DIAMETER_SUCCESS);

            assertThat(answer)
                    .as("%s answer should be non-null", cls.getSimpleName())
                    .isNotNull();
            assertThat(answer.getHopByHopIdentifier())
                    .as("%s hop-by-hop must match request", cls.getSimpleName())
                    .isEqualTo(DUMMY_HOP_BY_HOP);
            assertThat(answer.getEndToEndIdentifier())
                    .as("%s end-to-end must match request", cls.getSimpleName())
                    .isEqualTo(DUMMY_END_TO_END);
            assertThat(answer.getResultCode())
                    .as("%s result code", cls.getSimpleName())
                    .isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
            assertThat(answer.isRequest()).as("%s R-bit must be clear", cls.getSimpleName()).isFalse();
        }
    }
}
