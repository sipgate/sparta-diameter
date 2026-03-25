package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.Command;
import com.sipgate.sparta.diameter.core.IncomingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Decodes a framed {@link ByteBuf} into an {@link IncomingCommand}.
 * Must be placed after a {@link io.netty.handler.codec.LengthFieldBasedFrameDecoder}
 * in the pipeline so that each {@code msg} contains exactly one complete
 * Diameter message.
 */
public final class DiameterMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg,
                          final List<Object> out) throws Exception {
        final ByteBuffer buffer = msg.nioBuffer();
        out.add(Command.parseMessage(buffer));
    }
}
