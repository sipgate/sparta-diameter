package com.sipgate.sparta.diameter.core;

import java.lang.reflect.Constructor;

/**
 * Central factory for creating Diameter request and answer messages.
 * <p>
 * All message classes carry a private constructor following a fixed convention:
 * <ul>
 *   <li>Requests: {@code (boolean retransmitted, int hopByHop, int endToEnd)}</li>
 *   <li>Answers: {@code (int hopByHop, int endToEnd)}</li>
 * </ul>
 * This factory reaches those constructors via reflection, so per-class static factory
 * methods are no longer needed.
 * </p>
 */
public final class DiameterMessageFactory {

    private DiameterMessageFactory() {}

    /**
     * Creates a new request of the given type.
     *
     * @param type               the concrete request class (must have a private {@code (boolean, int, int)} constructor)
     * @param hopByHopIdentifier the hop-by-hop identifier
     * @param endToEndIdentifier the end-to-end identifier
     * @param <R>                the request type
     * @return a new request instance with retransmitted flag cleared
     */
    public static <R extends Request<R, ?>> R create(
            final Class<R> type,
            final int hopByHopIdentifier,
            final int endToEndIdentifier) {
        return newRequest(type, false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted request of the given type.
     *
     * @param type               the concrete request class (must have a private {@code (boolean, int, int)} constructor)
     * @param hopByHopIdentifier the hop-by-hop identifier
     * @param endToEndIdentifier the end-to-end identifier
     * @param <R>                the request type
     * @return a new request instance with retransmitted flag set
     */
    public static <R extends Request<R, ?>> R createRetransmitted(
            final Class<R> type,
            final int hopByHopIdentifier,
            final int endToEndIdentifier) {
        return newRequest(type, true, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an answer for the given request.
     * <p>
     * The answer receives the same hop-by-hop and end-to-end identifiers as the
     * request (required by RFC 6733 §3), has its Result-Code set to
     * {@code resultCode}, and has its Destination-Host and Destination-Realm
     * populated from the request's Origin-Host and Origin-Realm respectively
     * so the reply is routed back to the originator.
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set on the answer
     * @param <R>        the request type
     * @param <A>        the answer type (inferred from the request)
     * @return the constructed answer
     * @throws IllegalArgumentException if no answer type is registered for the request's command code
     */
    public static <R extends Request<R, A>, A extends Answer<A>> A createAnswer(
            final R request,
            final long resultCode) {
        @SuppressWarnings("unchecked")
        final Class<A> answerClass = (Class<A>) Command.ANSWER_TYPES.get(request.getCommandCode());
        if (answerClass == null) {
            throw new IllegalArgumentException(
                    "No answer type registered for command code: " + request.getCommandCode());
        }
        final A answer = newAnswer(answerClass, request.getHopByHopIdentifier(), request.getEndToEndIdentifier());
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

    /**
     * Creates a command (request or answer) for wire-parsing purposes.
     * <p>
     * Package-private: called only by {@link Command#parseMessage}.
     * </p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Command instantiateForParsing(
            final Class<? extends Command> commandClass,
            final boolean retransmitted,
            final int hopByHopId,
            final int endToEndId) {
        if (Request.class.isAssignableFrom(commandClass)) {
            final Class<Request> requestClass = (Class<Request>) commandClass;
            return retransmitted
                    ? createRetransmitted(requestClass, hopByHopId, endToEndId)
                    : create(requestClass, hopByHopId, endToEndId);
        }
        final Class<Answer> answerClass = (Class<Answer>) commandClass;
        return newAnswer(answerClass, hopByHopId, endToEndId);
    }

    private static <R extends Request<R, ?>> R newRequest(
            final Class<R> type,
            final boolean retransmitted,
            final int hopByHopIdentifier,
            final int endToEndIdentifier) {
        try {
            final Constructor<R> ctor = type.getDeclaredConstructor(boolean.class, int.class, int.class);
            ctor.setAccessible(true);
            return ctor.newInstance(retransmitted, hopByHopIdentifier, endToEndIdentifier);
        } catch (final Exception e) {
            throw new IllegalStateException(
                    "Cannot instantiate " + type.getSimpleName()
                            + ": expected private (boolean, int, int) constructor",
                    e);
        }
    }

    private static <A extends Answer<A>> A newAnswer(
            final Class<A> type,
            final int hopByHopIdentifier,
            final int endToEndIdentifier) {
        try {
            final Constructor<A> ctor = type.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            return ctor.newInstance(hopByHopIdentifier, endToEndIdentifier);
        } catch (final Exception e) {
            throw new IllegalStateException(
                    "Cannot instantiate " + type.getSimpleName()
                            + ": expected private (int, int) constructor",
                    e);
        }
    }
}
