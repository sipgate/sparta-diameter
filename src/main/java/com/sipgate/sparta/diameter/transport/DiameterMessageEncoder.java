package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.DataOutputStream;

/**
 * Encodes a {@link Command} into its wire representation as a {@link ByteBuf}.
 */
public final class DiameterMessageEncoder extends MessageToByteEncoder<Command<?>> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Command<?> msg, final ByteBuf out) throws Exception {
        try (final DataOutputStream dos = new DataOutputStream(new ByteBufOutputStream(out))) {
            msg.writeTo(dos);
        }
    }
}
