package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Central factory for creating Diameter request and answer messages.
 * <p>
 * Discovers {@link DiameterPackageFactory} implementations on the classpath at startup via
 * the reflections library. Additional factories can be registered at runtime via
 * {@link #register(DiameterPackageFactory)}.
 * </p>
 */
public final class DiameterMessageFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterMessageFactory.class);

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
        LOGGER.debug("registered {}", factory.getClass().getName());
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
        final boolean proxiable,
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

        LOGGER.warn("unknown command code {} for application id {}", commandCode, applicationId);
        return new GenericCommand.In(commandCode, isRequest, proxiable, isError, retransmitted, applicationId, hopByHop, endToEnd);
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
    public static <A extends OutgoingAnswer> A createAnswer(
            final IncomingRequest<A> request,
            final long resultCode) {
        return createAnswer(request, answer -> answer.setResultCode(resultCode));
    }

    /**
     * Creates an outgoing E-bit error answer for the given incoming request.
     * <p>
     * Use this for protocol errors (RFC 6733 §7.2). The answer has the E-bit set, carries the
     * same hop-by-hop and end-to-end identifiers as the request, and its Result-Code is set to
     * {@code resultCode}. Per RFC 6733, only 3xxx result codes are valid here.
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set (should be a 3xxx protocol error code)
     * @return the constructed error answer
     */
    public static ErrorAnswer.Out createErrorAnswer(
            final IncomingRequest<?> request,
            final long resultCode) {
        return createErrorAnswer(
                request.getCommandCode(),
                request.isProxiable(),
                request.getApplicationId(),
                request.hopByHopId(),
                request.endToEndId(),
                request.getSessionId(),
                resultCode
        );
    }

    public static ErrorAnswer.Out createErrorAnswer(final DiameterResultCodeException cause) {
        return createErrorAnswer(cause.getCommandCode(), cause.isProxiable(), cause.getApplicationId(), cause.getHopByHop(), cause.getEndToEnd(), cause.getSessionId(), cause.getResultCode());
    }

    private static ErrorAnswer.Out createErrorAnswer(
        final int commandCode,
        final boolean isProxiable,
        final int applicationId,
        final HopByHopId hopByHop,
        final EndToEndId endToEnd,
        final String sessionId,
        final long resultCode
    ) {
        final var errorAnswer= new ErrorAnswer.Out(commandCode, isProxiable, applicationId, hopByHop, endToEnd);
        if (sessionId != null) {
            errorAnswer.unshiftAvp(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), sessionId));
        }
        errorAnswer.setResultCode(resultCode);
        return errorAnswer;
    }

    /**
     * Creates an outgoing answer carrying an Experimental-Result for the given incoming request.
     * <p>
     * Use this for vendor-specific results (RFC 6733 §7.6). The answer is a normal typed answer
     * (no E-bit) with the Experimental-Result grouped AVP set. Per RFC 6733, Result-Code and
     * Experimental-Result are mutually exclusive — this method sets only Experimental-Result.
     * </p>
     *
     * @param request                the received request to answer
     * @param vendorId               the 3GPP or other vendor ID to include in Experimental-Result
     * @param experimentalResultCode the vendor-assigned result code
     * @param <A>                    the outgoing answer type
     * @return the constructed answer
     * @throws IllegalArgumentException if no factory handles the request's command code
     */
    public static <A extends OutgoingAnswer & HasExperimentalResultAVP> A createExperimentalResultAnswer(
            final IncomingRequest<A> request,
            final long vendorId,
            final long experimentalResultCode) {
        return createAnswer(request, answer -> {
            answer.setExperimentalResult(List.of(
                    AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), vendorId),
                    AVP.create(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0), experimentalResultCode)
                ));
        });
    }

    private static <A extends OutgoingAnswer> A createAnswer(
        final IncomingRequest<A> request,
        final Consumer<A> initializer
    ) {
        final var commandCode = request.getCommandCode();
        final var applicationId = request.getApplicationId();

        for (final var factory : FACTORIES) {
            @SuppressWarnings("unchecked")
            final A answer = (A) factory.createAnswer(commandCode, applicationId, request.hopByHopId(), request.endToEndId());
            if (answer != null) {
                prependSessionId(request, answer);
                initializer.accept(answer);
                return answer;
            }
        }

        throw new IllegalArgumentException("No factory handles answer for command code: " + commandCode);
    }

    private static void prependSessionId(final Request<?> request, final Answer answer) {
        final var sessionId = request.getSessionId();
        if (sessionId != null) {
            answer.unshiftAvp(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), sessionId));
        }
    }

}
