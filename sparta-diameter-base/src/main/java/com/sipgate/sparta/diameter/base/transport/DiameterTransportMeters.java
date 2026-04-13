package com.sipgate.sparta.diameter.base.transport;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collection;
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
        Gauge.builder(PREFIX + "connections.active.direction", activeInboundConnections, AtomicInteger::get)
                .description("Number of currently open TCP connections by direction.")
                .tag(TAG_DIRECTION, DIRECTION_INBOUND)
                .register(registry);
        Gauge.builder(PREFIX + "connections.active.direction", activeOutboundConnections, AtomicInteger::get)
                .description("Number of currently open TCP connections by direction.")
                .tag(TAG_DIRECTION, DIRECTION_OUTBOUND)
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
        Counter.builder(PREFIX + "connections")
                .description("TCP connections established; a direction=inbound/outbound tag indicates which side initiated. Does not imply a successful Diameter CER/CEA handshake — only that the TCP SYN/ACK completed.")
                .tag(TAG_DIRECTION, direction)
                .register(registry)
                .increment();
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
        Counter.builder(PREFIX + "disconnections")
                .description("TCP disconnections observed; does not correlate with a clean Diameter DPR/DPA exchange.")
                .tag(TAG_DIRECTION, direction)
                .register(registry)
                .increment();
    }

    void recordActiveApplicationIds(final Collection<Long> applicationIds) {
        for (final long appId : applicationIds) {
            final String appIdStr = String.valueOf(appId);
            activeConnectionsByAppId
                    .computeIfAbsent(appIdStr, id -> {
                        final var counter = new AtomicInteger(0);
                        Gauge.builder(PREFIX + "connections.active.application", counter, AtomicInteger::get)
                                .description("Number of currently open TCP connections by application.")
                                .tag(TAG_APPLICATION_ID, id)
                                .register(registry);
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
        Counter.builder(PREFIX + "commands.sent")
                .description("Diameter commands sent by this node; only counted after a successful write — write failures are tracked separately under diameter.commands.send_errors.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_COMMAND_TYPE, commandType)
                .register(registry)
                .increment();
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId  as specified in diameter
     * @param commandType one of COMMAND_TYPE_*
     */
    void recordSendError(final int commandCode, final int applicationId, final String commandType) {
        Counter.builder(PREFIX + "commands.send_errors")
                .description("Diameter commands that failed to write to the wire.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_COMMAND_TYPE, commandType)
                .register(registry)
                .increment();
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId  as specified in diameter
     * @param commandType one of COMMAND_TYPE_*
     */
    void recordReceived(final int commandCode, final int applicationId, final String commandType) {
        Counter.builder(PREFIX + "commands.received")
                .description("Diameter commands received by this node.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_COMMAND_TYPE, commandType)
                .register(registry)
                .increment();
    }

    void recordDecodeError() {
        Counter.builder(PREFIX + "decode.errors")
                .description("Messages that could not be decoded")
                .register(registry)
                .increment();
    }
}
