package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
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
            return new ErrorAnswer.In(commandCode, proxiable, applicationId, hopByHop, endToEnd);
        }
        for (final var factory : FACTORIES) {
            final var result = factory.createForParsing(
                    commandCode, applicationId, isRequest, hopByHop, endToEnd, retransmitted);
            if (result != null) {
                return result;
            }
        }

        // Debug only: unknown commands are legitimate traffic for relay agents.
        LOGGER.debug("unknown command code {} for application id {}", commandCode, applicationId);
        if (isRequest) {
            return new GenericRequest(commandCode, proxiable, retransmitted, applicationId, hopByHop, endToEnd);
        }
        return new GenericAnswer.In(commandCode, proxiable, applicationId, hopByHop, endToEnd);
    }

    /**
     * Creates an outgoing answer for the given incoming request.
     * <p>
     * The answer receives the same hop-by-hop and end-to-end identifiers as the request
     * (required by RFC 6733 §3) and has its Result-Code set to {@code resultCode}. Every
     * Proxy-Info AVP of the request is copied, in order (RFC 6733 §6.2 MUST). For a
     * {@link GenericRequest} (no typed answer class) the answer is a {@link GenericAnswer.Out}
     * that mirrors the request's Auth-Session-State and Vendor-Specific-Application-Id, if present.
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set on the answer
     * @param <A>        the outgoing answer type
     * @return the constructed answer
     * @throws IllegalArgumentException if no factory handles the request's command code
     *         and it is not a {@link GenericRequest}
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
     * {@code resultCode}. Per RFC 6733, only 3xxx result codes are valid here. Every Proxy-Info
     * AVP of the request is copied, in order (RFC 6733 §6.2 MUST).
     * </p>
     *
     * @param request    the received request to answer
     * @param resultCode the Result-Code AVP value to set (should be a 3xxx protocol error code)
     * @return the constructed error answer
     */
    public static ErrorAnswer.Out createErrorAnswer(
            final IncomingRequest<?> request,
            final long resultCode) {
        return createErrorAnswer(request, resultCode, null);
    }

    /**
     * Like {@link #createErrorAnswer(IncomingRequest, long)}, additionally carrying an
     * Error-Message AVP (RFC 6733 §7.3) with a human-readable reason.
     *
     * @param request      the received request to answer
     * @param resultCode   the Result-Code AVP value to set (should be a 3xxx protocol error code)
     * @param errorMessage the Error-Message text; {@code null} omits the AVP
     * @return the constructed error answer
     */
    public static ErrorAnswer.Out createErrorAnswer(
            final IncomingRequest<?> request,
            final long resultCode,
            final String errorMessage) {
        return createErrorAnswer(request, errorAnswer -> {
            errorAnswer.setResultCode(resultCode);
            if (errorMessage != null) {
                errorAnswer.setErrorMessage(errorMessage);
            }
        });
    }

    public static ErrorAnswer.Out createErrorAnswer(
        final IncomingRequest<?> request,
        final Consumer<ErrorAnswer.Out> initializer
    ) {
        return createErrorAnswer(
            request.getCommandCode(),
            request.isProxiable(),
            request.getApplicationId(),
            request.hopByHopId(),
            request.endToEndId(),
            request.getSessionId(),
            errorAnswer -> {
                mirrorSessionAvps(request, errorAnswer);
                initializer.accept(errorAnswer);
                copyProxyInfos(request, errorAnswer);
            }
        );
    }

    /**
     * Appends every Proxy-Info AVP of the request to the answer, in order. RFC 6733 §6.2: a
     * locally generated answer MUST carry the Proxy-Info AVPs of the request, in the same order.
     */
    private static void copyProxyInfos(final Command request, final Command answer) {
        final List<AVP> proxyInfos = request.findAVPs(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0));
        for (final AVP proxyInfo : proxyInfos) {
            answer.addAVP(proxyInfo);
        }
    }

    /**
     * Copies the session-scoped AVPs of the request onto an E-bit answer or a
     * {@link GenericAnswer.Out}.
     * <p>
     * Both are built without the per-application factory, so they miss what that factory would
     * add - for S6a/Cx that is Auth-Session-State and Vendor-Specific-Application-Id.
     * 3GPP peers expect both on answers (Auth-Session-State must match the request per RFC 6733
     * §8.11), and without them an MME sees a truncated AIA/ULA.
     * </p>
     */
    private static void mirrorSessionAvps(final IncomingRequest<?> request, final Command answer) {
        for (final var code : new int[]{
            DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID,
            DiameterConstants.AVP_AUTH_SESSION_STATE}
        ) {
            final var avp = request.findAVP(new AVPKey(code, 0));
            if (avp != null) {
                answer.setAVP(avp);
            }
        }
    }


    public static ErrorAnswer.Out createErrorAnswer(final DiameterResultCodeException cause) {
        return createErrorAnswer(cause.getCommandCode(), cause.isProxiable(), cause.getApplicationId(), cause.getHopByHop(), cause.getEndToEnd(), cause.getSessionId(), errorAnswer -> {
            errorAnswer.setResultCode(cause.getResultCode());
            if (cause.getAuthSessionState() != null) {
                errorAnswer.setAVP(cause.getAuthSessionState());
            }
            for (final AVP proxyInfo : cause.getProxyInfos()) {
                errorAnswer.addAVP(proxyInfo);
            }
        });
    }

    /**
     * Creates the answer to a request that failed to parse.
     * <p>
     * Only protocol errors (3xxx) are E-bit error answers (RFC 6733 §7.1/§7.2); a permanent
     * failure such as {@code DIAMETER_AVP_UNSUPPORTED} (5001) or
     * {@code DIAMETER_INVALID_AVP_LENGTH} (5014) is a regular answer without the E-bit
     * (RFC 6733 §7.1.5). The request could not be decoded into its typed form, so the
     * non-E-bit variant is a {@link GenericAnswer.Out} with the request's header fields.
     * An {@link com.sipgate.sparta.diameter.base.core.avp.AVPParseException} contributes a
     * {@code Failed-AVP} wrapping the offending AVP. Proxy-Info AVPs parsed before the failure
     * ({@link DiameterResultCodeException#getProxyInfos()}) are copied in order (RFC 6733 §6.2),
     * an Auth-Session-State parsed before it ({@link DiameterResultCodeException#getAuthSessionState()})
     * is mirrored (RFC 6733 §8.11).
     * </p>
     *
     * @param cause the parse failure of a request ({@link DiameterResultCodeException#isRequest()})
     * @return the answer to send
     */
    public static OutgoingAnswer createParseErrorAnswer(final DiameterResultCodeException cause) {
        return createParseErrorAnswer(cause, false);
    }

    /**
     * Creates the answer to a request that failed to parse, optionally in the style of
     * peers that flag every parse-error answer as an error.
     *
     * @param cause                    the parse failure of a request
     * @param errorBitWithoutFailedAvp {@code false} (RFC 6733, see
     *                                 {@link #createParseErrorAnswer(DiameterResultCodeException)}):
     *                                 E-bit only for 3xxx, {@code Failed-AVP} for an
     *                                 {@link com.sipgate.sparta.diameter.base.core.avp.AVPParseException};
     *                                 {@code true} (Yate-compatible): always an E-bit
     *                                 {@link ErrorAnswer.Out} and never a {@code Failed-AVP}
     * @return the answer to send
     */
    public static OutgoingAnswer createParseErrorAnswer(final DiameterResultCodeException cause,
                                                        final boolean errorBitWithoutFailedAvp) {
        final OutgoingAnswer answer;
        if (errorBitWithoutFailedAvp || isProtocolError(cause.getResultCode())) {
            answer = createErrorAnswer(cause);
        } else {
            answer = new GenericAnswer.Out(cause.getCommandCode(), cause.isProxiable(),
                    cause.getApplicationId(), cause.getHopByHop(), cause.getEndToEnd());
            if (cause.getSessionId() != null) {
                answer.unshiftAvp(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), cause.getSessionId()));
            }
            answer.setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_RESULT_CODE, 0), cause.getResultCode()));
            if (cause.getAuthSessionState() != null) {
                answer.setAVP(cause.getAuthSessionState());
            }
            for (final AVP proxyInfo : cause.getProxyInfos()) {
                answer.addAVP(proxyInfo);
            }
        }
        if (!errorBitWithoutFailedAvp
                && cause instanceof final com.sipgate.sparta.diameter.base.core.avp.AVPParseException avpEx) {
            answer.setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0),
                    List.of(avpEx.getOffendingAvp())));
        }
        return answer;
    }

    private static boolean isProtocolError(final long resultCode) {
        return resultCode >= 3000 && resultCode < 4000;
    }

    private static ErrorAnswer.Out createErrorAnswer(
        final int commandCode,
        final boolean isProxiable,
        final int applicationId,
        final HopByHopId hopByHop,
        final EndToEndId endToEnd,
        final String sessionId,
        final Consumer<ErrorAnswer.Out> initializer
    ) {
        final var errorAnswer= new ErrorAnswer.Out(commandCode, isProxiable, applicationId, hopByHop, endToEnd);
        if (sessionId != null) {
            errorAnswer.unshiftAvp(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_ID, 0), sessionId));
        }
        initializer.accept(errorAnswer);
        return errorAnswer;

    }

    /**
     * Creates an outgoing answer carrying an Experimental-Result for the given incoming request.
     * <p>
     * Use this for vendor-specific results (RFC 6733 §7.6). The answer is a normal typed answer
     * (no E-bit) with the Experimental-Result grouped AVP set. Per RFC 6733, Result-Code and
     * Experimental-Result are mutually exclusive — this method sets only Experimental-Result.
     * Every Proxy-Info AVP of the request is copied, in order (RFC 6733 §6.2 MUST); a
     * {@link GenericRequest} gets a {@link GenericAnswer.Out} mirroring the request's
     * Auth-Session-State and Vendor-Specific-Application-Id, if present.
     * </p>
     *
     * @param request                the received request to answer
     * @param vendorId               the 3GPP or other vendor ID to include in Experimental-Result
     * @param experimentalResultCode the vendor-assigned result code
     * @param <A>                    the outgoing answer type
     * @return the constructed answer
     * @throws IllegalArgumentException if no factory handles the request's command code
     */
    public static <A extends OutgoingAnswer> A createExperimentalResultAnswer(
            final IncomingRequest<A> request,
            final long vendorId,
            final long experimentalResultCode) {
        // setAVP instead of the HasExperimentalResultAVP mixin: works for every answer type,
        // including base-protocol and generic answers that do not declare the mixin.
        return createAnswer(request, answer -> answer.setAVP(AVP.create(
                new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT, 0),
                List.of(
                    AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), vendorId),
                    AVP.create(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0), experimentalResultCode)
                ))));
    }

    public static ErrorAnswer.Out createExperimentalErrorAnswer(
        final IncomingRequest<?> request,
        final long vendorId,
        final long experimentalResultCode) {
        return createErrorAnswer(request, errorAnswer -> {
            errorAnswer.setExperimentalResult(List.of(
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
                copyProxyInfos(request, answer);
                return answer;
            }
        }

        if (request instanceof GenericRequest) {
            @SuppressWarnings("unchecked")
            final A answer = (A) new GenericAnswer.Out(
                    commandCode, request.isProxiable(), applicationId, request.hopByHopId(), request.endToEndId());
            prependSessionId(request, answer);
            // no per-application factory adds them: mirror what the request carried, as the
            // typed answers do (Auth-Session-State is mandatory in 3GPP answers, RFC 6733 §8.11)
            mirrorSessionAvps(request, answer);
            initializer.accept(answer);
            copyProxyInfos(request, answer);
            return answer;
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
