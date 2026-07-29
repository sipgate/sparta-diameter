package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.Answer;
import com.sipgate.sparta.diameter.base.core.ErrorAnswer;

/**
 * Thrown (via {@code CompletableFuture.completeExceptionally}) when a peer responds with an
 * E-bit error answer (RFC 6733 §7.2) instead of the expected command-specific answer.
 *
 * <p>Callers of {@link DiameterSession#send} that must handle protocol errors pattern-match
 * on this exception in their {@code .handle()} or {@code .exceptionally()} callbacks:
 *
 * <pre>{@code
 * session.send(request).handle((answer, ex) -> {
 *     if (ex instanceof DiameterErrorAnswerException e) {
 *         // inspect e.getAnswer()
 *     } else if (ex != null) {
 *         // unexpected failure
 *     } else {
 *         // normal answer
 *     }
 * });
 * }</pre>
 *
 * <p>A request handler signals a protocol error by completing its future exceptionally with
 * {@code new DiameterErrorAnswerException(DiameterMessageFactory.createErrorAnswer(request, resultCode))}.
 */
public final class DiameterErrorAnswerException extends Exception {

    private final Answer answer;

    public DiameterErrorAnswerException(final Answer answer) {
        this.answer = answer;
    }

    /**
     * The answer this exception carries.
     * <p>
     * Inbound: the {@link ErrorAnswer.In} a peer sent instead of the expected answer.
     * Outbound: the answer a request handler wants sent instead of the normal one - either an
     * {@link ErrorAnswer.Out} for protocol errors (E-bit, RFC 6733 §7.2) or a regular typed
     * answer carrying an Experimental-Result (§7.6), which must not have the E-bit set.
     * </p>
     */
    public Answer getAnswer() {
        return answer;
    }
}
