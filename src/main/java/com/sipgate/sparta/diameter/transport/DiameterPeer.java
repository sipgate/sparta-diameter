package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;

/**
 * Represents an established Diameter peer connection.
 * Wraps the underlying Netty {@link Channel} and hides transport details from
 * application code. The same abstraction is used regardless of which side
 * initiated the TCP connection.
 */
public final class DiameterPeer {

    private final Channel channel;

    DiameterPeer(final Channel channel) {
        this.channel = channel;
    }

    /**
     * Writes a Diameter command to the peer and flushes immediately.
     *
     * @param command the command to send
     * @return a {@link ChannelFuture} that completes when the write is done
     */
    public ChannelFuture send(final Command<?> command) {
        return channel.writeAndFlush(command);
    }

    /**
     * Closes the connection to this peer.
     *
     * @return a {@link ChannelFuture} that completes when the channel is closed
     */
    public ChannelFuture close() {
        return channel.close();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    public SocketAddress localAddress() {
        return channel.localAddress();
    }
}
