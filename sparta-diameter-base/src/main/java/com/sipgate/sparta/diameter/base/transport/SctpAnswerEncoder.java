package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.DataOutputStream;
import java.util.List;

/**
 * Outbound adapter for the SCTP transport: serializes an {@link OutgoingAnswer} into a
 * single {@link SctpMessage} on stream 0 with ordered delivery and the payload protocol
 * identifier {@link SctpPayloadProtocolId#DIAMETER}. Ordered delivery keeps the base-protocol
 * state machines safe: a CEA cannot be overtaken by a request sent right after it, and a DPA
 * cannot overtake answers still in flight (see ADR-0014).
 *
 * <p>Replaces {@link OutgoingAnswerEncoder} in SCTP pipelines — the wire format is
 * identical, only the transport envelope differs. Installed after
 * {@link DiameterPeerHandler} (outbound flow), together with {@link SctpRequestEncoder}.</p>
 */
public final class SctpAnswerEncoder extends MessageToMessageEncoder<OutgoingAnswer> {

    private static final int DIAMETER_STREAM = 0;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final OutgoingAnswer answer,
                          final List<Object> out) throws Exception {
        final ByteBuf buffer = ctx.alloc().ioBuffer();
        try {
            answer.writeTo(new DataOutputStream(new ByteBufOutputStream(buffer)));
        } catch (final Exception e) {
            buffer.release();
            throw e;
        }
        // Netty ctor (protocolIdentifier, streamIdentifier, content): ordered delivery
        out.add(new SctpMessage(SctpPayloadProtocolId.DIAMETER, DIAMETER_STREAM, buffer));
    }
}
