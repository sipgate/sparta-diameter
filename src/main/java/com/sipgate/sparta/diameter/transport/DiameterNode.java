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
     *
     * @param port     the TCP port to bind
     * @param listener receives lifecycle and message events for every accepted peer
     * @return a {@link ChannelFuture} that completes once the port is bound
     */
    public ChannelFuture listen(final int port, final DiameterConnectionListener listener) {
        return new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(newInitializer(listener))
            .bind(port);
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port.
     *
     * @param host     the remote host
     * @param port     the remote TCP port
     * @param listener receives lifecycle and message events for the connected peer
     * @return a {@link ChannelFuture} that completes once the connection is established
     */
    public ChannelFuture connect(final String host, final int port, final DiameterConnectionListener listener) {
        return new Bootstrap()
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .handler(newInitializer(listener))
            .connect(host, port);
    }

    private ChannelInitializer<SocketChannel> newInitializer(final DiameterConnectionListener listener) {
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
                    .addLast(new DiameterPeerHandler(listener));
            }
        };
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
