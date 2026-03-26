package com.sipgate.sparta.diameter.base.transport;

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
}
