package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.IncomingRequest;
import com.sipgate.sparta.diameter.core.OutgoingAnswer;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for an inbound Diameter request message.
 *
 * <p>Register one per command code via
 * {@link DiameterSession#setHandler(Class, DiameterRequestHandler)}.
 * The session invokes the handler when a matching request arrives in OPEN state
 * and sends the answer returned by the future to the peer.
 *
 * @param <R> the incoming request type
 * @param <A> the outgoing answer type
 */
@FunctionalInterface
public interface DiameterRequestHandler<
        R extends IncomingRequest<R, A>,
        A extends OutgoingAnswer<A>> {

    /**
     * Handles an inbound request and returns a future that resolves to the answer.
     *
     * @param request the inbound request
     * @return a future completed with the answer to send back
     */
    CompletableFuture<A> handle(R request);
}
