package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Application-level inbound handler. One instance per channel.
 * Bridges Netty channel lifecycle events to {@link DiameterConnectionListener}.
 */
final class DiameterPeerHandler extends SimpleChannelInboundHandler<IncomingCommand> {

    private final DiameterConnectionListener listener;
    private DiameterPeer peer;

    DiameterPeerHandler(final DiameterConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        peer = new DiameterPeer(ctx.channel());
        listener.onConnected(peer);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final IncomingCommand msg) {
        listener.onMessage(peer, msg);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        listener.onDisconnected(peer);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        ctx.close();
    }
}
