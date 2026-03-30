package com.sipgate.sparta.diameter.base.session;

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
 * {@code new DiameterErrorAnswerException(new ErrorAnswer.Out(...))}.
 */
public final class DiameterErrorAnswerException extends Exception {

    private final ErrorAnswer<?> answer;

    public DiameterErrorAnswerException(final ErrorAnswer<?> answer) {
        this.answer = answer;
    }

    public ErrorAnswer<?> getAnswer() {
        return answer;
    }
}
