package com.sipgate.sparta.diameter.base.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.DataOutputStream;
import java.util.List;

/**
 * Outbound adapter for the SCTP transport: serializes an {@link OutgoingRequestEnvelope}
 * into a single {@link SctpMessage} on stream 0 with the unordered delivery flag set and
 * the payload protocol identifier {@link SctpPayloadProtocolId#DIAMETER}. Hop-by-hop and
 * end-to-end identifiers are stamped into the header at encode time, exactly like
 * {@link OutgoingRequestEncoder} does for TCP.
 */
public final class SctpRequestEncoder extends MessageToMessageEncoder<OutgoingRequestEnvelope> {

    private static final int DIAMETER_STREAM = 0;
    private static final boolean DIAMETER_UNORDERED = true;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final OutgoingRequestEnvelope envelope,
                          final List<Object> out) throws Exception {
        final ByteBuf buffer = ctx.alloc().ioBuffer();
        try {
            envelope.request().writeTo(
                    new DataOutputStream(new ByteBufOutputStream(buffer)),
                    envelope.hopByHop(),
                    envelope.endToEnd());
        } catch (final Exception e) {
            buffer.release();
            throw e;
        }
        // Netty 4.2 ctor: (protocolIdentifier, streamIdentifier, unordered, content)
        out.add(new SctpMessage(SctpPayloadProtocolId.DIAMETER, DIAMETER_STREAM, DIAMETER_UNORDERED, buffer));
    }
}
