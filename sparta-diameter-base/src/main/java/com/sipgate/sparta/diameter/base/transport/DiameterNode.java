package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.session.DiameterInitiatorSession;
import com.sipgate.sparta.diameter.base.session.DiameterResponderSession;
import com.sipgate.sparta.diameter.base.session.DiameterSession;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Entry point for the Diameter transport layer.
 * <p>
 * A single {@code DiameterNode} can both accept incoming connections and
 * initiate outgoing ones, over TCP or SCTP. The pipeline decodes inbound
 * frames into {@link com.sipgate.sparta.diameter.base.core.IncomingCommand} objects and
 * encodes outbound {@link com.sipgate.sparta.diameter.base.core.OutgoingAnswer} /
 * {@link OutgoingRequestEnvelope} objects back to bytes.
 * </p>
 *
 * <p><strong>TCP</strong> (default): classic byte-stream framing per RFC 6733 &sect;2.2.1 &mdash;
 * {@link LengthFieldBasedFrameDecoder} followed by {@link DiameterMessageDecoder}.</p>
 *
 * <p><strong>SCTP</strong> (plain, no DTLS &mdash; see {@code specs/dtls-sctp}): SCTP preserves
 * message boundaries, so no length framing is needed. Diameter messages travel as
 * individual {@link io.netty.channel.sctp.SctpMessage}s on stream 0 with ordered
 * delivery and the payload protocol identifier {@link SctpPayloadProtocolId#DIAMETER}
 * (46), per RFC 6733 &sect;2.1.1. Inbound, a
 * {@link io.netty.handler.codec.sctp.SctpMessageCompletionHandler} reassembles any
 * fragmented messages before {@link SctpMessageDecoder} unwraps the payload.
 * SCTP multi-homing is supported: a server binds its primary address via
 * {@code ServerBootstrap.bind(...)} and adds every further address with
 * {@code NioSctpServerChannel.bindAddress(InetAddress)} (the {@code sctp_bindx}
 * equivalent); a client binds its primary address locally via
 * {@code Bootstrap.localAddress(...)} (honoured by {@code NioSctpChannel.doConnect})
 * and adds further addresses with {@code NioSctpChannel.bindAddress(InetAddress)}
 * once the association is established.</p>
 *
 * <p><strong>Event loop groups:</strong> both transports share the same
 * {@link MultiThreadIoEventLoopGroup} with {@link NioIoHandler}. SCTP channels are
 * NIO handles registered on the same {@link java.nio.channels.Selector} as TCP channels
 * ({@code NioIoHandler.isCompatible} accepts any NIO handle; the UNSAFE of every
 * {@code AbstractNioChannel} &mdash; including {@code NioSctpChannel} &mdash; implements
 * {@code NioIoHandle}), so no separate event loop group is required.</p>
 *
 * <pre>
 * Diameter message framing on TCP (RFC 6733):
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

    /**
     * Transport selector for the {@code listen}/{@code connect} overloads.
     */
    public enum Transport {
        /** Byte-stream transport per RFC 6733 &sect;2.2.1 (default). */
        TCP,
        /** Plain SCTP transport per RFC 6733 &sect;2.1.1 (no DTLS). */
        SCTP
    }

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final DiameterTransportMeters transportMeters;
    private final boolean relay;

    public DiameterNode() {
        this(new SimpleMeterRegistry());
    }

    public DiameterNode(final MeterRegistry meterRegistry) {
        this(meterRegistry, false);
    }

    /**
     * @param relay {@code true} for a relay agent: proxiable messages with an
     *              unrecognized M-bit AVP are decoded and kept opaque instead of being
     *              rejected with {@code DIAMETER_AVP_UNSUPPORTED} (5001) — a relay forwards
     *              AVPs it does not understand (RFC 6733 §2.8.1, §6.1). Endpoints keep the
     *              default {@code false} (strict).
     */
    public DiameterNode(final MeterRegistry meterRegistry, final boolean relay) {
        this.bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        this.transportMeters = new DiameterTransportMeters(meterRegistry);
        this.relay = relay;
    }

    /**
     * Starts listening for incoming Diameter connections on the given port, over TCP.
     *
     * <pre>{@code
     * node.listen(3868, () -> new DiameterResponderSession(config));
     * }</pre>
     */
    public ChannelFuture listen(final int port, final Supplier<DiameterResponderSession> factory) {
        return listen(port, factory, Transport.TCP);
    }

    /**
     * Starts listening for incoming Diameter connections on the given port.
     *
     * @param transport the wire transport; see the class Javadoc for the SCTP pipeline
     * @throws NullPointerException for {@link Transport#SCTP} when {@code serverLocalAddresses}
     *                              is null (the 3-arg overload forwards null; the 5-arg
     *                              overload throws IllegalArgumentException when it is empty)
     */
    public ChannelFuture listen(final int port, final Supplier<DiameterResponderSession> factory,
                                final Transport transport) {
        return listen(port, factory, transport, null);
    }

    /**
     * Starts listening for incoming Diameter connections on the given port, with explicit
     * SCTP multi-homing addresses.
     *
     * @param transport            the wire transport; must be {@link Transport#SCTP} when
     *                             {@code serverLocalAddresses} is non-null
     * @param serverLocalAddresses local addresses for SCTP multi-homing; the first entry is the
     *                             primary bind address, further entries are added with
     *                             {@code NioSctpServerChannel.bindAddress(InetAddress)} once the
     *                             server channel is active (the {@code sctp_bindx} equivalent).
     *                             Must be {@code null} for TCP and non-null, non-empty for SCTP
     * @throws NullPointerException     for {@link Transport#SCTP} when {@code serverLocalAddresses}
     *                                  is null
     * @throws IllegalArgumentException for {@link Transport#SCTP} when {@code serverLocalAddresses}
     *                                  is empty, or for {@link Transport#TCP} when it is non-null
     */
    public ChannelFuture listen(final int port, final Supplier<DiameterResponderSession> factory,
                                final Transport transport, final List<InetAddress> serverLocalAddresses) {
        LOGGER.info("starting to listen on port {} over {}", port, transport);
        if (transport == Transport.SCTP) {
            return doListenSctp(port, factory, serverLocalAddresses);
        }
        if (serverLocalAddresses != null) {
            throw new IllegalArgumentException("serverLocalAddresses requires Transport.SCTP");
        }
        return doListenTcp(new InetSocketAddress(port), factory);
    }

    /**
     * Starts listening for incoming Diameter connections over TCP on one local address only
     * (instead of the wildcard address the other TCP overloads bind to).
     *
     * <pre>{@code
     * node.listen(InetAddress.getByName("10.0.0.1"), 3868, () -> new DiameterResponderSession(config));
     * }</pre>
     *
     * @param bindAddress the local address to bind; {@code null} binds the wildcard address
     *                    (same as {@link #listen(int, Supplier)})
     */
    public ChannelFuture listen(final InetAddress bindAddress, final int port,
                                final Supplier<DiameterResponderSession> factory) {
        LOGGER.info("starting to listen on {}:{} over {}", bindAddress, port, Transport.TCP);
        return doListenTcp(new InetSocketAddress(bindAddress, port), factory);
    }

    private ChannelFuture doListenTcp(final InetSocketAddress localAddress,
                                      final Supplier<DiameterResponderSession> factory) {
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TcpInitializer(factory, DiameterTransportMeters.DIRECTION_INBOUND))
                .bind(localAddress)
                .addListener(f -> {
                    if (!f.isSuccess()) {
                        transportMeters.recordListenError(f.cause());
                    }
                });
    }

    private ChannelFuture doListenSctp(final int port, final Supplier<DiameterResponderSession> factory,
                                       final List<InetAddress> serverLocalAddresses) {
        Objects.requireNonNull(serverLocalAddresses, "serverLocalAddresses must not be null for SCTP");
        if (serverLocalAddresses.isEmpty()) {
            throw new IllegalArgumentException("serverLocalAddresses must not be empty for SCTP");
        }
        final var primary = new InetSocketAddress(serverLocalAddresses.get(0), port);
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(io.netty.channel.sctp.nio.NioSctpServerChannel.class)
                .childHandler(new SctpInitializer(factory, DiameterTransportMeters.DIRECTION_INBOUND))
                // primary bind with the FIRST address; NioSctpServerChannel.doBind() calls
                // SctpServerChannel.bind(address, backlog).
                .bind(primary)
                .addListener((final ChannelFuture bound) -> {
                    if (!bound.isSuccess()) {
                        transportMeters.recordListenError(bound.cause());
                        return;
                    }
                    final io.netty.channel.sctp.nio.NioSctpServerChannel server =
                            (io.netty.channel.sctp.nio.NioSctpServerChannel) bound.channel();
                    // sctp_bindx equivalent: bind the remaining addresses onto the live server channel.
                    for (int i = 1; i < serverLocalAddresses.size(); i++) {
                        final InetAddress address = serverLocalAddresses.get(i);
                        server.bindAddress(address)
                                .addListener(f -> {
                                    if (!f.isSuccess()) {
                                        LOGGER.error("failed to bind SCTP multi-homing address {}", address, f.cause());
                                        transportMeters.recordListenError(f.cause());
                                    }
                                });
                    }
                });
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port, over TCP.
     */
    public ChannelFuture connect(final String host, final int port,
                                 final Function<Consumer<DiameterInitiatorSession>, DiameterInitiatorSession> factory) {
        return connect(host, port, factory, Transport.TCP);
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port.
     */
    public ChannelFuture connect(final String host, final int port,
                                 final Function<Consumer<DiameterInitiatorSession>, DiameterInitiatorSession> factory,
                                 final Transport transport) {
        return connect(host, port, factory, transport, null);
    }

    /**
     * Initiates an outgoing Diameter connection to the given host and port, with explicit
     * SCTP client multi-homing addresses.
     *
     * @param transport            the wire transport; must be {@link Transport#SCTP} when
     *                             {@code clientLocalAddresses} is non-null
     * @param clientLocalAddresses local addresses for SCTP client multi-homing; the first entry is
     *                             bound as the primary local address before connecting
     *                             (via {@code Bootstrap.localAddress(...)}, honoured by
     *                             {@code NioSctpChannel.doConnect}), further entries are added
     *                             with {@code NioSctpChannel.bindAddress(InetAddress)} once the
     *                             channel is active. Must be {@code null} for TCP and non-null,
     *                             non-empty for SCTP
     * @throws NullPointerException     for {@link Transport#SCTP} when {@code clientLocalAddresses}
     *                                  is null
     * @throws IllegalArgumentException for {@link Transport#SCTP} when {@code clientLocalAddresses}
     *                                  is empty, or for {@link Transport#TCP} when it is non-null
     */
    public ChannelFuture connect(final String host, final int port,
                                 final Function<Consumer<DiameterInitiatorSession>, DiameterInitiatorSession> factory,
                                 final Transport transport, final List<InetAddress> clientLocalAddresses) {
        final Consumer<DiameterInitiatorSession> reconnect = (session) -> doConnect(host, port, session,
                transport, clientLocalAddresses);
        final var session = factory.apply(reconnect);
        return doConnect(host, port, session, transport, clientLocalAddresses);
    }

    private ChannelFuture doConnect(final String host, final int port, final DiameterInitiatorSession session,
                                    final Transport transport, final List<InetAddress> clientLocalAddresses) {
        LOGGER.info("connecting to {}:{} over {}", host, port, transport);
        if (transport == Transport.SCTP) {
            return doConnectSctp(host, port, session, clientLocalAddresses);
        }
        if (clientLocalAddresses != null) {
            throw new IllegalArgumentException("clientLocalAddresses requires Transport.SCTP");
        }
        return doConnectTcp(host, port, session);
    }

    private ChannelFuture doConnectTcp(final String host, final int port, final DiameterInitiatorSession session) {
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new TcpInitializer(() -> session, DiameterTransportMeters.DIRECTION_OUTBOUND))
                .connect(host, port)
                .addListener(newConnectListener(host, port, session));
    }

    private ChannelFuture doConnectSctp(final String host, final int port, final DiameterInitiatorSession session,
                                        final List<InetAddress> clientLocalAddresses) {
        Objects.requireNonNull(clientLocalAddresses, "clientLocalAddresses must not be null for SCTP");
        if (clientLocalAddresses.isEmpty()) {
            throw new IllegalArgumentException("clientLocalAddresses must not be empty for SCTP");
        }
        final var bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(io.netty.channel.sctp.nio.NioSctpChannel.class)
                // primary local bind address; Bootstrap localAddress is honoured by NioSctpChannel
                // doConnect(remote, local) -> SctpChannel.bind(local). SCTP_NODELAY is deliberately
                // NOT set: it is obsolete for SCTP.
                .localAddress(clientLocalAddresses.get(0), 0)
                .handler(new SctpInitializer(() -> session, DiameterTransportMeters.DIRECTION_OUTBOUND));
        final io.netty.util.concurrent.GenericFutureListener<io.netty.util.concurrent.Future<Void>> listener =
                newConnectListener(host, port, session);
        return bootstrap
                .connect(host, port)
                .addListener((final ChannelFuture connected) -> {
                    listener.operationComplete(connected);
                    if (!connected.isSuccess()) {
                        return;
                    }
                    final io.netty.channel.sctp.nio.NioSctpChannel channel =
                            (io.netty.channel.sctp.nio.NioSctpChannel) connected.channel();
                    for (int i = 1; i < clientLocalAddresses.size(); i++) {
                        final InetAddress address = clientLocalAddresses.get(i);
                        channel.bindAddress(address)
                                .addListener(f -> {
                                    if (!f.isSuccess()) {
                                        LOGGER.error("failed to bind SCTP client multi-homing address {}", address, f.cause());
                                    }
                                });
                    }
                });
    }

    private io.netty.util.concurrent.GenericFutureListener<io.netty.util.concurrent.Future<Void>> newConnectListener(
            final String host, final int port, final DiameterInitiatorSession session) {
        return (final io.netty.util.concurrent.Future<Void> future) -> {
            if (!future.isSuccess()) {
                transportMeters.recordConnectError(future.cause());
                LOGGER.warn("failed to connect to {}:{}", host, port, future.cause());
                session.scheduleReconnect();
            }
        };
    }

    private final class TcpInitializer extends ChannelInitializer<SocketChannel> {

        private final Supplier<? extends DiameterSession> factory;
        private final String direction;

        TcpInitializer(final Supplier<? extends DiameterSession> factory, final String direction) {
            this.factory = factory;
            this.direction = direction;
        }

        @Override
        protected void initChannel(final SocketChannel ch) {
            ch.pipeline()
                    .addLast(new LengthFieldBasedFrameDecoder(
                            MAX_FRAME_LENGTH,
                            LENGTH_FIELD_OFFSET,
                            LENGTH_FIELD_LENGTH,
                            LENGTH_ADJUSTMENT,
                            INITIAL_BYTES_TO_STRIP))
                    .addLast(new DiameterMessageDecoder(transportMeters, relay))
                    .addLast(new OutgoingAnswerEncoder())
                    .addLast(new OutgoingRequestEncoder())
                    .addLast(new DiameterPeerHandler(factory.get(), direction, transportMeters));
        }
    }

    /**
     * Pipeline for SCTP channels. Mirrors the TCP pipeline except that:
     * <ul>
     *     <li>no {@code LengthFieldBasedFrameDecoder}: SCTP preserves message boundaries</li>
     *     <li>{@link SctpMessageCompletionHandler} reassembles fragmented SCTP messages</li>
     *     <li>{@link SctpMessageDecoder} unwraps {@code SctpMessage}s (PPID check) into
     *     {@code ByteBuf}s for the shared {@link DiameterMessageDecoder}</li>
     *     <li>{@link SctpAnswerEncoder}/{@link SctpRequestEncoder} wrap outgoing messages
     *     in {@code SctpMessage}s (stream 0, ordered, PPID 46)</li>
     * </ul>
     */
    private final class SctpInitializer extends ChannelInitializer<Channel> {

        private final Supplier<? extends DiameterSession> factory;
        private final String direction;

        SctpInitializer(final Supplier<? extends DiameterSession> factory, final String direction) {
            this.factory = factory;
            this.direction = direction;
        }

        @Override
        protected void initChannel(final Channel ch) {
            ch.pipeline()
                    .addLast(new SctpMessageCompletionHandler())
                    .addLast(new SctpMessageDecoder())
                    .addLast(new DiameterMessageDecoder(transportMeters, relay))
                    .addLast(new SctpAnswerEncoder())
                    .addLast(new SctpRequestEncoder())
                    .addLast(new DiameterPeerHandler(factory.get(), direction, transportMeters));
        }
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
