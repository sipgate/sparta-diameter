package com.sipgate.sparta.diameter.base.transport;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

final class DiameterTransportMeters {

    static final String DIRECTION_INBOUND = "inbound";
    static final String DIRECTION_OUTBOUND = "outbound";
    static final String COMMAND_TYPE_REQUEST = "request";
    static final String COMMAND_TYPE_ANSWER = "answer";

    private static final String PREFIX = "diameter.";
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_APPLICATION_ID = "application_id";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_COMMAND_TYPE = "command_type";

    private final MeterRegistry registry;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicInteger activeInboundConnections = new AtomicInteger(0);
    private final AtomicInteger activeOutboundConnections = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> activeConnectionsByAppId = new ConcurrentHashMap<>();

    DiameterTransportMeters(final MeterRegistry registry) {
        this.registry = registry;
        Gauge.builder(PREFIX + "connections.active", activeConnections, AtomicInteger::get)
                .description("Number of currently open TCP connections.")
                .register(registry);
        Gauge.builder(PREFIX + "connections.active", activeInboundConnections, AtomicInteger::get)
                .description("Number of currently open TCP connections.")
                .tag(TAG_DIRECTION, DIRECTION_INBOUND)
                .register(registry);
        Gauge.builder(PREFIX + "connections.active", activeOutboundConnections, AtomicInteger::get)
                .description("Number of currently open TCP connections.")
                .tag(TAG_DIRECTION, DIRECTION_OUTBOUND)
                .register(registry);
        Counter.builder(PREFIX + "connections")
                .description("TCP connections established; a direction=inbound/outbound tag indicates which side initiated. Does not imply a successful Diameter CER/CEA handshake — only that the TCP SYN/ACK completed.")
                .register(registry);
        Counter.builder(PREFIX + "disconnections")
                .description("TCP disconnections observed; does not correlate with a clean Diameter DPR/DPA exchange.")
                .register(registry);
        Counter.builder(PREFIX + "decode.errors")
                .description("Messages that could not be decoded; command_code and application_id are unavailable at decode time, so no tags are attached.")
                .register(registry);
    }

    /**
     * @param direction one of DIRECTION_*
     */
    void recordConnected(final String direction) {
        activeConnections.incrementAndGet();
        if (DIRECTION_INBOUND.equals(direction)) {
            activeInboundConnections.incrementAndGet();
        } else {
            activeOutboundConnections.incrementAndGet();
        }
        registry.counter(PREFIX + "connections", TAG_DIRECTION, direction).increment();
    }

    /**
     * @param direction one of DIRECTION_*
     */
    void recordDisconnected(final String direction) {
        activeConnections.decrementAndGet();
        if (DIRECTION_INBOUND.equals(direction)) {
            activeInboundConnections.decrementAndGet();
        } else {
            activeOutboundConnections.decrementAndGet();
        }
        registry.counter(PREFIX + "disconnections", TAG_DIRECTION, direction).increment();
    }

    void recordActiveApplicationIds(final Collection<Long> applicationIds) {
        for (final long appId : applicationIds) {
            final String appIdStr = String.valueOf(appId);
            activeConnectionsByAppId
                    .computeIfAbsent(appIdStr, id -> {
                        final var counter = new AtomicInteger(0);
                        registry.gauge(PREFIX + "connections.active",
                                List.of(Tag.of(TAG_APPLICATION_ID, id)), counter);
                        return counter;
                    })
                    .incrementAndGet();
        }
    }

    void recordInactiveApplicationIds(final Collection<Long> applicationIds) {
        for (final long appId : applicationIds) {
            final var counter = activeConnectionsByAppId.get(String.valueOf(appId));
            if (counter != null) {
                counter.decrementAndGet();
            }
        }
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId  as specified in diameter
     * @param commandType one of COMMAND_TYPE_*
     */
    void recordSent(final int commandCode, final int applicationId, final String commandType) {
        registry.counter(PREFIX + "commands.sent",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId),
                TAG_COMMAND_TYPE, commandType).increment();
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId  as specified in diameter
     * @param commandType one of COMMAND_TYPE_*
     */
    void recordReceived(final int commandCode, final int applicationId, final String commandType) {
        registry.counter(PREFIX + "commands.received",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId),
                TAG_COMMAND_TYPE, commandType).increment();
    }

    void recordDecodeError() {
        registry.counter(PREFIX + "decode.errors").increment();
    }
}
