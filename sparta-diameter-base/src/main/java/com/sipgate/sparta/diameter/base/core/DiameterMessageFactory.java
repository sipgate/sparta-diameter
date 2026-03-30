package com.sipgate.sparta.diameter.base.core;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * Central factory for creating Diameter request and answer messages.
 * <p>
 * Discovers {@link DiameterPackageFactory} implementations on the classpath at startup via
 * the reflections library. Additional factories can be registered at runtime via
 * {@link #register(DiameterPackageFactory)}.
 * </p>
 */
public final class DiameterMessageFactory {

    static final List<DiameterPackageFactory> FACTORIES = new ArrayList<>();

    static {
        final var reflections = new Reflections("com.sipgate.sparta.diameter");
        for (final var cls : reflections.getSubTypesOf(DiameterPackageFactory.class)) {
            try {
                register(cls.getDeclaredConstructor().newInstance());
            } catch (final Exception e) {
                throw new IllegalStateException("Cannot instantiate " + cls.getName(), e);
            }
        }
    }

    private DiameterMessageFactory() {}

    /**
     * Registers an additional factory at runtime. Intended for embedders that add a Diameter
     * application module after startup. Thread-safety is the caller's responsibility; call
     * before any message is parsed.
     */
    public static void register(final DiameterPackageFactory factory) {
        FACTORIES.add(factory);
    }

    /**
     * Creates an incoming command instance from parsed wire data.
     * <p>
     * Called by {@link Command#parseMessage} during normal decoding, and by tests that need
     * to simulate inbound messages without going through a real network.
     * </p>
     *
     * @throws IllegalArgumentException if no registered factory handles the given combination
     */
    public static IncomingCommand createForParsing(
            final int commandCode,
            final int applicationId,
            final boolean isRequest,
            final boolean isError,
            final HopByHopId hopByHop,
            final EndToEndId endToEnd,
            final boolean retransmitted) {
        if (!isRequest && isError) {
            return new ErrorAnswer.In(commandCode, applicationId, hopByHop, endToEnd);
        }
        for (final var factory : FACTORIES) {
            final var result = factory.createForParsing(
                    commandCode, applicationId, isRequest, hopByHop, endToEnd, retransmitted);
            if (result != null) {
                return result;
            }
        }
        throw new IllegalArgumentException(String.format(
                "No factory handles commandCode=%d applicationId=%d isRequest=%b",
                commandCode, applicationId, isRequest));
    }

    /**
     * Creates an outgoing answer for the given incoming request.
     * <p>
     * The answer receives the same hop-by-hop and end-to-end identifiers as the request
     * (required by RFC 6733 §3), has its Result-Code set to {@code resultCode}, and has
     * Destination-Host / Destination-Realm populated from the request's origin.
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set on the answer
     * @param <A>        the outgoing answer type
     * @return the constructed answer
     * @throws IllegalArgumentException if no factory handles the request's command code
     */
    @SuppressWarnings("unchecked")
    public static <A extends OutgoingAnswer<A>> A createAnswer(
            final IncomingRequest<?, ?> request,
            final long resultCode) {
        final var commandCode = request.getCommandCode();
        final var applicationId = request.getApplicationId();

        OutgoingAnswer<?> answer = null;
        for (final var factory : FACTORIES) {
            answer = factory.createAnswer(commandCode, applicationId,
                    request.hopByHopId(), request.endToEndId());
            if (answer != null) {
                break;
            }
        }
        if (answer == null) {
            throw new IllegalArgumentException(
                    "No factory handles answer for command code: " + commandCode);
        }

        @SuppressWarnings("unchecked")
        final A typedAnswer = (A) answer;
        typedAnswer.setResultCode(resultCode);
        return typedAnswer;
    }

}
