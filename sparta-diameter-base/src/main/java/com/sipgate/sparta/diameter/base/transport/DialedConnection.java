package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.session.DiameterInitiatorSession;
import io.netty.channel.ChannelFuture;

/**
 * A dialled Diameter connection: the initiator session created by the caller's factory
 * and the future of the initial connect attempt.
 *
 * <p>The session exists even when the initial attempt fails; it owns the Tc reconnect
 * via the {@code reconnect} consumer handed to the factory. The future only reports the
 * first attempt, so callers that need the session and the connect outcome do not have
 * to smuggle the session out through the factory callback.</p>
 *
 * @param session        the initiator session of this dial
 * @param connectFuture  the future of the initial connect attempt
 */
public record DialedConnection(DiameterInitiatorSession session, ChannelFuture connectFuture) {
}
