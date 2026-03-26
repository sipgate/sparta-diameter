package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;

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
     * Sends an outgoing answer to the peer.
     * The answer carries its own hop-by-hop and end-to-end identifiers.
     *
     * @param answer the answer to send
     * @return a {@link ChannelFuture} that completes when the write is done
     */
    public ChannelFuture send(final OutgoingAnswer<?> answer) {
        return channel.writeAndFlush(answer);
    }

    /**
     * Sends an outgoing request to the peer, injecting the supplied identifiers
     * into the Diameter header at encode time.
     *
     * @param request  the request to send
     * @param hopByHop the hop-by-hop identifier for this transmission
     * @param endToEnd the end-to-end identifier for this transmission
     * @return a {@link ChannelFuture} that completes when the write is done
     */
    public ChannelFuture send(final OutgoingRequest<?, ?> request,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return channel.writeAndFlush(new OutgoingRequestEnvelope(request, hopByHop, endToEnd));
    }

    /**
     * Closes the connection to this peer.
     *
     * @return a {@link ChannelFuture} that completes when the channel is closed
     */
    public ChannelFuture close() {
        return channel.close();
    }

    public EventLoop eventLoop() {
        return channel.eventLoop();
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
