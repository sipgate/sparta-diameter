package com.sipgate.sparta.diameter.transport;

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

import java.io.Closeable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Entry point for the Diameter transport layer.
 * <p>
 * A single {@code DiameterNode} can both accept incoming connections and
 * initiate outgoing ones. The pipeline is identical in both directions:
 * raw bytes arrive, get framed, get decoded into {@link com.sipgate.sparta.diameter.core.Command}
 * objects, and are delivered to a {@link DiameterConnectionListener}.
 * Outbound writes go the other way.
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

    private static final int MAX_FRAME_LENGTH = 16 * 1024 * 1024; // 16 MB
    private static final int LENGTH_FIELD_OFFSET = 1;
    private static final int LENGTH_FIELD_LENGTH = 3;
    private static final int LENGTH_ADJUSTMENT = -4;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public DiameterNode() {
        this.bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    }

    /**
     * Starts listening for incoming Diameter connections on the given port.
     * The factory is called once per accepted connection to produce a fresh listener instance.
     *
     * @param port    the TCP port to bind
     * @param factory called for each accepted connection; must return a new listener instance
     * @return a {@link ChannelFuture} that completes once the port is bound
     */
    public ChannelFuture listen(final int port, final Supplier<DiameterConnectionListener> factory) {
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(newInitializer(factory))
                .bind(port);
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port.
     * <p>
     * The factory receives a {@code reconnect} runnable it may call when the transport
     * drops unexpectedly. Calling {@code reconnect} creates a new session instance and
     * re-establishes the connection. The factory must not call {@code reconnect} during
     * construction.
     * </p>
     *
     * @param host    the remote host
     * @param port    the remote TCP port
     * @param factory receives the reconnect callback; must return a new listener instance
     * @return a {@link ChannelFuture} that completes once the initial connection is established
     */
    public ChannelFuture connect(final String host, final int port,
                                 final Function<Runnable, DiameterConnectionListener> factory) {
        return doConnect(host, port, factory);
    }

    private ChannelFuture doConnect(final String host, final int port,
                                    final Function<Runnable, DiameterConnectionListener> factory) {
        final Runnable reconnect = () -> doConnect(host, port, factory);
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(newInitializer(() -> factory.apply(reconnect)))
                .connect(host, port);
    }

    private ChannelInitializer<SocketChannel> newInitializer(final Supplier<DiameterConnectionListener> factory) {
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
                        .addLast(new DiameterMessageDecoder())
                        .addLast(new DiameterMessageEncoder())
                        .addLast(new DiameterPeerHandler(factory.get()));
            }
        };
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
