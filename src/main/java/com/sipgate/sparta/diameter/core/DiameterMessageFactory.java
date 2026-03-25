package com.sipgate.sparta.diameter.core;

import java.lang.reflect.Constructor;

/**
 * Central factory for creating Diameter request and answer messages.
 * <p>
 * Constructor conventions (enforced via reflection):
 * <ul>
 *   <li>Incoming requests (parsing): {@code In(HopByHopId, EndToEndId, boolean retransmitted)}</li>
 *   <li>Incoming answers (parsing): {@code In(HopByHopId, EndToEndId)}</li>
 *   <li>Outgoing answers: {@code Out(HopByHopId, EndToEndId)} — identifiers copied from the
 *       originating request.</li>
 * </ul>
 * </p>
 */
public final class DiameterMessageFactory {

    private DiameterMessageFactory() {}

    /**
     * Creates an incoming command instance from parsed wire data.
     * <p>
     * Called by {@link Command#parseMessage} during normal decoding, and by tests
     * that need to simulate inbound messages without going through a real network.
     * </p>
     */
    @SuppressWarnings({"rawtypes"})
    public static IncomingCommand createForParsing(
            final Class type,
            final HopByHopId hopByHop,
            final EndToEndId endToEnd,
            final boolean retransmitted) {
        if (Request.class.isAssignableFrom(type)) {
            return instantiateInRequest(type, hopByHop, endToEnd, retransmitted);
        }
        return instantiateInAnswer(type, hopByHop, endToEnd);
    }

    /**
     * Creates an outgoing answer for the given incoming request.
     * <p>
     * The answer receives the same hop-by-hop and end-to-end identifiers as the
     * request (required by RFC 6733 §3), has its Result-Code set to {@code resultCode},
     * and has Destination-Host / Destination-Realm populated from the request's origin.
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set on the answer
     * @param <A>        the outgoing answer type
     * @return the constructed answer
     * @throws IllegalArgumentException if no answer type is registered for the request's command code
     */
    @SuppressWarnings({"rawtypes"})
    public static <A extends OutgoingAnswer<A>> A createAnswer(
            final IncomingRequest<?, ?> request,
            final long resultCode) {
        final int commandCode = request.getCommandCode();

        final Class<? extends Answer> inClass = Command.ANSWER_TYPES.get(commandCode);
        if (inClass == null) {
            throw new IllegalArgumentException(
                    "No answer type registered for command code: " + commandCode);
        }

        final Class<A> outClass = findOutClass(inClass);

        final A answer = instantiateOutAnswer(outClass, request.hopByHopId(), request.endToEndId());
        answer.setResultCode(resultCode);

        final String originHost = request.getOriginHost();
        if (originHost != null) {
            answer.setDestinationHost(originHost);
        }
        final String originRealm = request.getOriginRealm();
        if (originRealm != null) {
            answer.setDestinationRealm(originRealm);
        }
        return answer;
    }

    @SuppressWarnings("unchecked")
    private static <A extends OutgoingAnswer<A>> Class<A> findOutClass(
            final Class<?> inClass) {
        final Class<?> enclosing = inClass.getEnclosingClass();
        if (enclosing == null) {
            throw new IllegalStateException(
                    inClass.getSimpleName() + " has no enclosing class — expected In nested in XxxAnswer");
        }
        for (final Class<?> nested : enclosing.getDeclaredClasses()) {
            if (OutgoingAnswer.class.isAssignableFrom(nested)) {
                return (Class<A>) nested;
            }
        }
        throw new IllegalStateException(
                "No Out class found inside " + enclosing.getSimpleName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static IncomingCommand instantiateInRequest(
            final Class type,
            final HopByHopId hopByHop,
            final EndToEndId endToEnd,
            final boolean retransmitted) {
        try {
            final var ctor = type.getDeclaredConstructor(
                    HopByHopId.class, EndToEndId.class, boolean.class);
            ctor.setAccessible(true);
            return (IncomingCommand) ctor.newInstance(hopByHop, endToEnd, retransmitted);
        } catch (final Exception e) {
            throw new IllegalStateException(
                    "Cannot instantiate " + type.getSimpleName()
                            + ": expected private (HopByHopId, EndToEndId, boolean) constructor", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static IncomingCommand instantiateInAnswer(
            final Class type,
            final HopByHopId hopByHop,
            final EndToEndId endToEnd) {
        try {
            final var ctor = type.getDeclaredConstructor(HopByHopId.class, EndToEndId.class);
            ctor.setAccessible(true);
            return (IncomingCommand) ctor.newInstance(hopByHop, endToEnd);
        } catch (final Exception e) {
            throw new IllegalStateException(
                    "Cannot instantiate " + type.getSimpleName()
                            + ": expected private (HopByHopId, EndToEndId) constructor", e);
        }
    }

    private static <A extends OutgoingAnswer<A>> A instantiateOutAnswer(
            final Class<A> type,
            final HopByHopId hopByHop,
            final EndToEndId endToEnd) {
        try {
            final Constructor<A> ctor = type.getDeclaredConstructor(
                    HopByHopId.class, EndToEndId.class);
            ctor.setAccessible(true);
            return ctor.newInstance(hopByHop, endToEnd);
        } catch (final Exception e) {
            throw new IllegalStateException(
                    "Cannot instantiate " + type.getSimpleName()
                            + ": expected private (HopByHopId, EndToEndId) constructor", e);
        }
    }
}
