package com.sipgate.sparta.diameter.base.session.relay;

/**
 * Completes the future returned by
 * {@link com.sipgate.sparta.diameter.base.session.DiameterSession#relay} when no downstream
 * answer was forwarded upstream: the egress connection was not open, the write failed, the
 * request timed out, or the connection was lost. {@link #getCause()} holds the underlying
 * failure ({@code IllegalStateException}, {@code TimeoutException}, the write failure, ...).
 *
 * <p>A request handler that returns a future failed with this exception is answered with
 * {@code DIAMETER_UNABLE_TO_DELIVER} (3002, E-bit) instead of {@code DIAMETER_UNABLE_TO_COMPLY}
 * (RFC 6733 §7.1.3).</p>
 */
public final class RelayFailedException extends RuntimeException {

    public RelayFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
