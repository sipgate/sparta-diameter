package com.sipgate.sparta.diameter.transport;

import com.sipgate.sparta.diameter.core.OutgoingAnswer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.DataOutputStream;

public final class OutgoingAnswerEncoder extends MessageToByteEncoder<OutgoingAnswer> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final OutgoingAnswer answer,
                          final ByteBuf out) throws Exception {
        answer.writeTo(new DataOutputStream(new ByteBufOutputStream(out)));
    }
}
