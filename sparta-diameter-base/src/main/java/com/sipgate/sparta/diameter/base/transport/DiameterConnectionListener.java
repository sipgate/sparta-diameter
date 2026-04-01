package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;

/**
 * Receives lifecycle and message events for a Diameter peer connection.
 * The same interface is used regardless of whether the local node initiated
 * the connection or accepted it.
 */
public interface DiameterConnectionListener {

    void onConnected(DiameterPeer peer);

    void onMessage(DiameterPeer peer, IncomingCommand command);

    void onDisconnected(DiameterPeer peer);

    /**
     * Called when a received message could not be parsed due to a Diameter protocol
     * violation or structural error.
     *
     * <p>Implementations should inspect the exception type to determine the appropriate
     * response:
     * <ul>
     *   <li>{@link com.sipgate.sparta.diameter.base.core.avp.AVPParseException} — send an
     *       error answer with {@code Failed-AVP}; the connection may remain open.</li>
     *   <li>{@link com.sipgate.sparta.diameter.base.core.DiameterResultCodeException} — send
     *       an error answer without {@code Failed-AVP}; close the connection afterwards.</li>
     *   <li>{@link DiameterException} (base) — the stream is corrupt; close the connection
     *       without sending any reply.</li>
     * </ul>
     *
     * @param peer  the peer from which the unparseable message was received
     * @param cause the parse exception carrying context for the error response
     */
    void onParseError(DiameterPeer peer, DiameterException cause);
}
