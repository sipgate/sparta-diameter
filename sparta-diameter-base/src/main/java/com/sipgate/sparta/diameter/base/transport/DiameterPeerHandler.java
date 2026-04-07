package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.session.DiameterSession;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application-level inbound handler. One instance per channel.
 * Bridges Netty channel lifecycle events to {@link DiameterSession}.
 */
final class DiameterPeerHandler extends SimpleChannelInboundHandler<IncomingCommand> {

    private final DiameterSession listener;
    private final String direction;
    private final DiameterTransportMeters transportMeters;
    private Set<Long> negotiatedApplicationIds;

    DiameterPeerHandler(final DiameterSession listener, final String direction,
                        final DiameterTransportMeters transportMeters) {
        this.listener = listener;
        this.direction = direction;
        this.transportMeters = transportMeters;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        transportMeters.recordConnected(direction);
        listener.onConnected(new DiameterPeer(ctx.channel(), transportMeters));
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final IncomingCommand msg) {
        final String commandType = msg instanceof IncomingRequest
                ? DiameterTransportMeters.COMMAND_TYPE_REQUEST
                : DiameterTransportMeters.COMMAND_TYPE_ANSWER;
        transportMeters.recordReceived(msg.getCommandCode(), msg.getApplicationId(), commandType);

        if (msg instanceof final CapabilitiesExchangeAnswer.In cea) {
            negotiatedApplicationIds = mergedAppIds(cea.getAuthApplicationIds(), cea.getAcctApplicationIds());
            transportMeters.recordActiveApplicationIds(negotiatedApplicationIds);
        } else if (msg instanceof final CapabilitiesExchangeRequest.In cer) {
            negotiatedApplicationIds = mergedAppIds(cer.getAuthApplicationIds(), cer.getAcctApplicationIds());
            transportMeters.recordActiveApplicationIds(negotiatedApplicationIds);
        }

        listener.onMessage(msg);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        if (negotiatedApplicationIds != null) {
            transportMeters.recordInactiveApplicationIds(negotiatedApplicationIds);
        }
        transportMeters.recordDisconnected(direction);
        // use a new instance here instead of storing one in channelActive because a channel might never become active
        // (e.g. connection rejected or timed out)
        listener.onDisconnected(new DiameterPeer(ctx.channel(), transportMeters));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause instanceof final DiameterException e) {
            listener.onParseError(new DiameterPeer(ctx.channel(), transportMeters), e);
        } else {
            ctx.close();
        }
    }

    private static Set<Long> mergedAppIds(final List<Long> authIds, final List<Long> acctIds) {
        final Set<Long> ids = new HashSet<>(authIds.size() + acctIds.size());
        ids.addAll(authIds);
        ids.addAll(acctIds);
        return ids;
    }
}
