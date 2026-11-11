package com.sipgate.sparta.diameter.base.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Inbound adapter for the SCTP transport: unwraps the Diameter payload of an
 * {@link SctpMessage} into a {@link io.netty.buffer.ByteBuf} so that the same
 * {@link DiameterMessageDecoder} as on the TCP path can parse it.
 *
 * <p>Must be placed after a
 * {@link io.netty.handler.codec.sctp.SctpMessageCompletionHandler}, which reassembles
 * fragmented SCTP messages. SCTP preserves message boundaries, so — unlike TCP — no
 * {@code LengthFieldBasedFrameDecoder} is used.</p>
 *
 * <p>Only messages with payload protocol identifier
 * {@link SctpPayloadProtocolId#DIAMETER} or {@link SctpPayloadProtocolId#UNSPECIFIED}
 * (0 — sent by many peers instead of 46) are processed; anything else (e.g.
 * {@link SctpPayloadProtocolId#DTLS}) is dropped with a warning. The stream identifier
 * and the unordered-delivery flag are logged at debug level.</p>
 */
public final class SctpMessageDecoder extends MessageToMessageDecoder<SctpMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SctpMessageDecoder.class);

    @Override
    protected void decode(final ChannelHandlerContext ctx, final SctpMessage msg, final List<Object> out) {
        final int payloadProtocolId = msg.protocolIdentifier();
        if (payloadProtocolId != SctpPayloadProtocolId.DIAMETER
                && payloadProtocolId != SctpPayloadProtocolId.UNSPECIFIED) {
            LOGGER.warn("dropping SCTP message with unsupported payload protocol id {} on stream {} (unordered: {})",
                    payloadProtocolId, msg.streamIdentifier(), msg.isUnordered());
            return;
        }
        if (!msg.isComplete()) {
            // defensive: the SctpMessageCompletionHandler upstream must have merged the fragments
            LOGGER.warn("dropping incomplete SCTP Diameter message on stream {} (unordered: {})",
                    msg.streamIdentifier(), msg.isUnordered());
            return;
        }
        LOGGER.debug("processing SCTP Diameter message (payload protocol id {}) on stream {} (unordered: {})",
                payloadProtocolId, msg.streamIdentifier(), msg.isUnordered());
        // MessageToMessageDecoder releases the (refcounted) message after decode;
        // retain the payload so it survives into the next handler.
        out.add(msg.content().retain());
    }
}
