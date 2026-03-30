# Metrics — Design

## Dependency

Add to `sparta-diameter-base/pom.xml` (compile scope, no backend):

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
```

`SimpleMeterRegistry` is included in `micrometer-core` — no extra dependency for the default constructor.

## Registry injection — two-constructor pattern

Every class that accepts a `MeterRegistry` provides a convenience overload:

```java
// DiameterSession
DiameterSession(final DiameterNodeConfig config) {
    this(config, new SimpleMeterRegistry());
}

DiameterSession(final DiameterNodeConfig config, final MeterRegistry meterRegistry) {
    ...
    this.meters = new DiameterSessionMeters(meterRegistry);
}
```

`DiameterInitiatorSession` and `DiameterResponderSession` mirror this pattern and forward to `super(config, meterRegistry)`.

`DiameterNode` follows the same pattern. It passes the registry into the Netty pipeline initializer so `DiameterMessageDecoder` and `DiameterPeerHandler` can record their respective meters:

```java
DiameterNode() {
    this(new SimpleMeterRegistry());
}

DiameterNode(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    ...
}
```

## IncomingCommand — command code access

`IncomingCommand` currently exposes only `hopByHopId()` and `endToEndId()`. All concrete implementations extend `Command`, which has `getCommandCode()`. Add `getCommandCode()` to the `IncomingCommand` interface and delegate to the existing field in `Command` to avoid casting at every instrumentation point.

## Meter definitions

Session-level meters live in `DiameterSessionMeters`. Transport-level meters (decode errors, connections) live in `DiameterTransportMeters`. Both hold a `MeterRegistry` reference and expose named record methods. Meters register lazily on first use.

---

### `diameter.messages.sent` — Counter

Recorded in `sendAndTrack()` immediately before the write.

| Tag | Value |
|---|---|
| `command_code` | `String.valueOf(request.getCommandCode())` |
| `message_type` | `"request"` |

Outbound answers increment the same counter with `message_type = "answer"`, recorded in `dispatchInboundRequest()` when the handler future completes successfully.

---

### `diameter.messages.received` — Counter

Recorded at the start of `dispatchInboundRequest()` for inbound requests.

| Tag | Value |
|---|---|
| `command_code` | `String.valueOf(request.getCommandCode())` |
| `message_type` | `"request"` |

Inbound answers increment the same counter in `complete()`, before correlating the pending future.

| Tag | Value |
|---|---|
| `command_code` | `String.valueOf(answer.getCommandCode())` |
| `message_type` | `"answer"` |

---

### `diameter.requests.errors` — Counter

| Tag | Value |
|---|---|
| `command_code` | from the pending request (stored in `PendingRequest`) |
| `error_type` | `"timeout"` \| `"write_failure"` \| `"error_answer"` |

Recorded in:
- `timeout()` → `error_type = "timeout"`
- Write-listener in `sendAndTrack()` on `!writeResult.isSuccess()` → `error_type = "write_failure"`
- `complete()` when the inbound answer has the E-bit set → `error_type = "error_answer"`

---

### `diameter.decode.errors` — Counter

Recorded in `DiameterMessageDecoder` when a frame cannot be parsed (malformed header, unsupported version, AVP parse failure). No tags — the error is at the byte level, before any command code is known.

`DiameterMessageDecoder` receives a `DiameterTransportMeters` instance via its constructor, passed through the `ChannelInitializer` from `DiameterNode`.

---

### `diameter.request.duration` — Timer

Round-trip from write to answer receipt. Not recorded for timed-out or write-failed requests.

A `Timer.Sample` is started just before the write in `sendAndTrack()` and stored in `PendingRequest`. In `complete()`, the sample is stopped against the timer keyed to the command code.

| Tag | Value |
|---|---|
| `command_code` | from `PendingRequest` |

---

### `diameter.handler.duration` — Timer

Server-side handler latency: from `handler.handle(request)` in `dispatchInboundRequest()` until the returned `CompletableFuture` completes (successfully or exceptionally).

| Tag | Value |
|---|---|
| `command_code` | `String.valueOf(request.getCommandCode())` |

---

### `diameter.connections` — Counter

Incremented in `DiameterPeerHandler.channelActive()`.

| Tag | Value |
|---|---|
| `direction` | `"inbound"` (server-side accept) \| `"outbound"` (client-side connect) |

`DiameterPeerHandler` receives the direction and a `DiameterTransportMeters` instance at construction time. `DiameterNode` sets the direction when building the `ChannelInitializer` — `listen()` → `"inbound"`, `connect()` → `"outbound"`.

---

### `diameter.disconnections` — Counter

Incremented in `DiameterPeerHandler.channelInactive()`. Same `direction` tag as `diameter.connections`.

---

### `diameter.connections.active` — Gauge (global)

Currently open connections. Backed by an `AtomicInteger` held in `DiameterTransportMeters`, incremented on `channelActive` and decremented on `channelInactive`. Registered once when `DiameterNode` is constructed.

No tags — a single global count across all directions.

---

### `diameter.connections.active` — Gauge (per application ID)

Same metric name, but with an `application_id` tag. One `AtomicInteger` per application ID, held in a `ConcurrentHashMap<String, AtomicInteger>` in `DiameterTransportMeters`. Gauges are registered lazily on first encounter of a given application ID.

Updated after the CER/CEA handshake, once the negotiated application IDs are known. A connection that supports multiple application IDs increments each corresponding counter independently, so it appears in multiple series.

`DiameterPeerHandler` stores the negotiated application IDs in a field (populated on successful CEA). On `channelInactive()`, it passes that set to `DiameterTransportMeters` to decrement the corresponding counters. The peer handler holds the IDs; the meters class owns the gauge objects.

| Tag | Value |
|---|---|
| `application_id` | `String.valueOf(appId)` for each negotiated application ID |

---

## Base protocol messages

CER/CEA (257), DWR/DWA (280), and DPR/DPA (282) are handled by `DiameterPeerHandler`, not the session layer. `DiameterTransportMeters` exposes `recordSent(commandCode, messageType)` and `recordReceived(commandCode, messageType)` methods that write to the same `diameter.messages.sent` / `diameter.messages.received` counters with the same tag schema. Micrometer resolves them to the same series as the session-layer counters because the meter name and tag keys are identical.

## PendingRequest — changes

```java
private static final class PendingRequest<A> {
    final CompletableFuture<A> future;
    final Future<?> timeoutTask;
    final Timer.Sample timerSample;  // new — started just before write
    final String commandCode;        // new — stored to avoid re-computing on completion
    ...
}
```

## DiameterSessionMeters — sketch

```java
final class DiameterSessionMeters {

    private static final String PREFIX = "diameter.";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_MESSAGE_TYPE = "message_type";
    private static final String TAG_ERROR_TYPE = "error_type";

    private final MeterRegistry registry;

    DiameterSessionMeters(final MeterRegistry registry) {
        this.registry = registry;
    }

    void recordSent(final String commandCode, final String messageType) {
        registry.counter(PREFIX + "messages.sent",
                TAG_COMMAND_CODE, commandCode, TAG_MESSAGE_TYPE, messageType).increment();
    }

    void recordReceived(final String commandCode, final String messageType) {
        registry.counter(PREFIX + "messages.received",
                TAG_COMMAND_CODE, commandCode, TAG_MESSAGE_TYPE, messageType).increment();
    }

    void recordError(final String commandCode, final String errorType) {
        registry.counter(PREFIX + "requests.errors",
                TAG_COMMAND_CODE, commandCode, TAG_ERROR_TYPE, errorType).increment();
    }

    Timer.Sample startRequestTimer() {
        return Timer.start(registry);
    }

    void stopRequestTimer(final Timer.Sample sample, final String commandCode) {
        sample.stop(registry.timer(PREFIX + "request.duration", TAG_COMMAND_CODE, commandCode));
    }

    Timer.Sample startHandlerTimer() {
        return Timer.start(registry);
    }

    void stopHandlerTimer(final Timer.Sample sample, final String commandCode) {
        sample.stop(registry.timer(PREFIX + "handler.duration", TAG_COMMAND_CODE, commandCode));
    }
}
```

## DiameterTransportMeters — sketch

```java
final class DiameterTransportMeters {

    private static final String PREFIX = "diameter.";
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_APPLICATION_ID = "application_id";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_MESSAGE_TYPE = "message_type";

    private final MeterRegistry registry;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> activeConnectionsByAppId = new ConcurrentHashMap<>();

    DiameterTransportMeters(final MeterRegistry registry) {
        this.registry = registry;
        registry.gauge(PREFIX + "connections.active", activeConnections);
    }

    void recordConnected(final String direction) {
        activeConnections.incrementAndGet();
        registry.counter(PREFIX + "connections", TAG_DIRECTION, direction).increment();
    }

    void recordDisconnected(final String direction) {
        activeConnections.decrementAndGet();
        registry.counter(PREFIX + "disconnections", TAG_DIRECTION, direction).increment();
    }

    void recordActiveApplicationIds(final Collection<String> applicationIds) {
        for (final var appId : applicationIds) {
            activeConnectionsByAppId
                .computeIfAbsent(appId, id -> {
                    final var counter = new AtomicInteger(0);
                    registry.gauge(PREFIX + "connections.active",
                        List.of(Tag.of(TAG_APPLICATION_ID, id)), counter);
                    return counter;
                })
                .incrementAndGet();
        }
    }

    void recordInactiveApplicationIds(final Collection<String> applicationIds) {
        for (final var appId : applicationIds) {
            final var counter = activeConnectionsByAppId.get(appId);
            if (counter != null) {
                counter.decrementAndGet();
            }
        }
    }

    void recordSent(final String commandCode, final String messageType) {
        registry.counter(PREFIX + "messages.sent",
            TAG_COMMAND_CODE, commandCode, TAG_MESSAGE_TYPE, messageType).increment();
    }

    void recordReceived(final String commandCode, final String messageType) {
        registry.counter(PREFIX + "messages.received",
            TAG_COMMAND_CODE, commandCode, TAG_MESSAGE_TYPE, messageType).increment();
    }

    void recordDecodeError() {
        registry.counter(PREFIX + "decode.errors").increment();
    }
}
```

## Grafana dashboard

`specs/metrics/grafana-dashboard.json` is a manual deliverable. It cannot be meaningfully drafted before the meters are running against real traffic — panel queries and thresholds need empirical data to be useful.

Style reference: the [Node Exporter Full](https://grafana.com/grafana/dashboards/1860) dashboard (Grafana ID 1860) is a good model for layout and panel density. Adopt its conventions for stat panels (rate, error rate, active connections) and time-series panels (duration percentiles).

This step is explicitly out of scope for automated implementation. It must be authored manually after the instrumentation is deployed.
