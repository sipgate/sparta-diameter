package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
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

    private final DiameterTransportMeters transportMeters;

    DiameterMessageDecoder(final DiameterTransportMeters transportMeters) {
        this.transportMeters = transportMeters;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg,
                          final List<Object> out) throws Exception {
        final ByteBuffer buffer = msg.nioBuffer();
        try {
            out.add(Command.parseMessage(buffer));
        } catch (final Exception e) {
            transportMeters.recordDecodeError();
            throw e;
        }
    }
}
