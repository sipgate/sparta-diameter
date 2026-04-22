package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.session.DiameterInitiatorSession;
import com.sipgate.sparta.diameter.base.session.DiameterResponderSession;
import com.sipgate.sparta.diameter.base.session.DiameterSession;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Entry point for the Diameter transport layer.
 * <p>
 * A single {@code DiameterNode} can both accept incoming connections and
 * initiate outgoing ones. The pipeline decodes inbound bytes into
 * {@link com.sipgate.sparta.diameter.base.core.IncomingCommand} objects and encodes
 * outbound {@link com.sipgate.sparta.diameter.base.core.OutgoingAnswer} /
 * {@link OutgoingRequestEnvelope} objects back to bytes.
 * </p>
 *
 * <pre>
 * Diameter message framing (RFC 6733):
 *   offset 0 : version         (1 byte)
 *   offset 1 : message length  (3 bytes, big-endian, includes full message)
 *   offset 4 : flags + command code + ...
 *
 * LengthFieldBasedFrameDecoder parameters:
 *   lengthFieldOffset  = 1   (skip version)
 *   lengthFieldLength  = 3   (24-bit length)
 *   lengthAdjustment   = -4  (length field value already counts the 4 leading bytes)
 *   initialBytesToStrip = 0  (keep full frame for the decoder)
 * </pre>
 */
public final class DiameterNode implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterNode.class);

    private static final int MAX_FRAME_LENGTH = 16 * 1024 * 1024;
    private static final int LENGTH_FIELD_OFFSET = 1;
    private static final int LENGTH_FIELD_LENGTH = 3;
    private static final int LENGTH_ADJUSTMENT = -4;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final DiameterTransportMeters transportMeters;

    public DiameterNode() {
        this(new SimpleMeterRegistry());
    }

    public DiameterNode(final MeterRegistry meterRegistry) {
        this.bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        this.transportMeters = new DiameterTransportMeters(meterRegistry);
    }

    /**
     * Starts listening for incoming Diameter connections on the given port.
     *
     * <pre>{@code
     * node.listen(3868, () -> new DiameterResponderSession(config));
     * }</pre>
     */
    public ChannelFuture listen(final int port, final Supplier<DiameterResponderSession> factory) {
        LOGGER.info("starting to listen on port {}", port);
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(newInitializer(factory, DiameterTransportMeters.DIRECTION_INBOUND))
                .bind(port);
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port.
     */
    public ChannelFuture connect(final String host, final int port,
                                 final Function<Consumer<DiameterInitiatorSession>, DiameterInitiatorSession> factory) {
        final Consumer<DiameterInitiatorSession> reconnect = (session) -> doConnect(host, port, session);
        final var session = factory.apply(reconnect);
        return doConnect(host, port, session);
    }

    private ChannelFuture doConnect(final String host, final int port,
                                    final DiameterInitiatorSession session) {
        LOGGER.info("connecting to {}:{}", host, port);
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(newInitializer(() -> session, DiameterTransportMeters.DIRECTION_OUTBOUND))
                .connect(host, port)
                .addListener((final ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        LOGGER.warn("failed to connect to {}:{}", host, port, future.cause());
                        session.scheduleReconnect();
                    }
                });
    }

    private ChannelInitializer<SocketChannel> newInitializer(
            final Supplier<? extends DiameterSession> factory, final String direction) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) {
                ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(
                                MAX_FRAME_LENGTH,
                                LENGTH_FIELD_OFFSET,
                                LENGTH_FIELD_LENGTH,
                                LENGTH_ADJUSTMENT,
                                INITIAL_BYTES_TO_STRIP))
                        .addLast(new DiameterMessageDecoder(transportMeters))
                        .addLast(new OutgoingAnswerEncoder())
                        .addLast(new OutgoingRequestEncoder())
                        .addLast(new DiameterPeerHandler(factory.get(), direction, transportMeters));
            }
        };
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
