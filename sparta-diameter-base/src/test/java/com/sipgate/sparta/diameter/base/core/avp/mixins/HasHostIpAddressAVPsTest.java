package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasHostIpAddressAVPsTest {

    @Test
    void it_accumulates_multiple_host_ip_addresses() throws UnknownHostException {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        final var addr1 = InetAddress.getByName("192.0.2.1");
        final var addr2 = InetAddress.getByName("192.0.2.2");

        // WHEN
        cer.addHostIpAddress(addr1);
        cer.addHostIpAddress(addr2);

        // THEN
        assertThat(cer.getHostIpAddresses()).containsExactly(addr1, addr2);
    }

    @Test
    void it_returns_empty_list_when_no_host_ip_address_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getHostIpAddresses()).isEmpty();
    }

    @Test
    void it_returns_first_host_ip_address() throws UnknownHostException {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        final var addr1 = InetAddress.getByName("10.0.0.1");
        final var addr2 = InetAddress.getByName("10.0.0.2");

        // WHEN
        cer.addHostIpAddress(addr1);
        cer.addHostIpAddress(addr2);

        // THEN
        assertThat(cer.getFirstHostIpAddress()).isEqualTo(addr1);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstHostIpAddress()).isNull();
    }

    @Test
    void it_adds_all_host_ip_addresses_from_collection() throws UnknownHostException {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        final var addr1 = InetAddress.getByName("172.16.0.1");
        final var addr2 = InetAddress.getByName("172.16.0.2");

        // WHEN
        cer.addAllHostIpAddresses(List.of(addr1, addr2));

        // THEN
        assertThat(cer.getHostIpAddresses()).containsExactly(addr1, addr2);
    }
}
