package com.sipgate.sparta.diameter.base.transport;

import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Binds real TCP server sockets on the loopback interface (no connection is made).
 */
class DiameterNodeTcpListenTest {

    @Test
    void it_binds_the_tcp_listener_to_the_given_local_address_only() {
        // GIVEN
        final InetAddress loopback = InetAddress.getLoopbackAddress();
        try (DiameterNode node = new DiameterNode()) {

            // WHEN — port 0: any free port
            final ChannelFuture bound = node.listen(loopback, 0, () -> null).syncUninterruptibly();

            // THEN — not the wildcard address
            final InetSocketAddress local = (InetSocketAddress) bound.channel().localAddress();
            assertThat(local.getAddress()).isEqualTo(loopback);
            bound.channel().close().syncUninterruptibly();
        }
    }

    @Test
    void it_binds_the_wildcard_address_by_default() {
        // GIVEN
        try (DiameterNode node = new DiameterNode()) {

            // WHEN
            final ChannelFuture bound = node.listen(0, () -> null).syncUninterruptibly();

            // THEN
            final InetSocketAddress local = (InetSocketAddress) bound.channel().localAddress();
            assertThat(local.getAddress().isAnyLocalAddress()).isTrue();
            bound.channel().close().syncUninterruptibly();
        }
    }
}
