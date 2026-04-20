package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BaseMessageFactoryTest {

    private static final HopByHopId HOP = new HopByHopId(0);
    private static final EndToEndId END = new EndToEndId(0);

    private final BaseMessageFactory factory = new BaseMessageFactory();

    @Test
    @SuppressWarnings("unchecked")
    void it_handles_every_discovered_request_type() throws Exception {
        // GIVEN
        final Reflections reflections =
                new Reflections("com.sipgate.sparta.diameter.base.messages");
        final Set<Class<? extends IncomingRequest>> requestTypes =
                reflections.getSubTypesOf(IncomingRequest.class);
        assertThat(requestTypes).isNotEmpty();

        for (final Class<? extends IncomingRequest> cls : requestTypes) {
            // WHEN
            final IncomingRequest<?> instance =
                    (IncomingRequest<?>) cls.getDeclaredConstructors()[0]
                            .newInstance(HOP, END, false);
            final IncomingCommand result = factory.createForParsing(
                    instance.getCommandCode(), 0, true, HOP, END, false);

            // THEN
            assertThat(result)
                    .as("factory must handle command code for %s", cls.getSimpleName())
                    .isNotNull();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_handles_every_discovered_answer_type() throws Exception {
        // GIVEN
        final Reflections reflections =
                new Reflections("com.sipgate.sparta.diameter.base.messages");
        final Set<Class<? extends IncomingAnswer>> answerTypes =
                reflections.getSubTypesOf(IncomingAnswer.class);
        assertThat(answerTypes).isNotEmpty();

        for (final Class<? extends IncomingAnswer> cls : answerTypes) {
            // WHEN
            final IncomingAnswer instance =
                    (IncomingAnswer) cls.getDeclaredConstructors()[0]
                            .newInstance(HOP, END);
            final IncomingCommand result = factory.createForParsing(
                    instance.getCommandCode(), 0, false, HOP, END, false);

            // THEN
            assertThat(result)
                    .as("factory must handle command code for %s", cls.getSimpleName())
                    .isNotNull();
        }
    }
}
