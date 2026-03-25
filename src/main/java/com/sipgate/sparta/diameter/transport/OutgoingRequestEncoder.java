package com.sipgate.sparta.diameter.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.DataOutputStream;

public final class OutgoingRequestEncoder extends MessageToByteEncoder<OutgoingRequestEnvelope> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final OutgoingRequestEnvelope envelope,
                          final ByteBuf out) throws Exception {
        envelope.request().writeTo(
                new DataOutputStream(new ByteBufOutputStream(out)),
                envelope.hopByHop(),
                envelope.endToEnd());
    }
}
