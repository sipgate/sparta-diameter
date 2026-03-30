# Metrics — Requirements

## Context

The library has no metrics instrumentation today. ADR-0010 documents the decision: Micrometer is the sole metrics facade; no concrete registry is bundled; the `MeterRegistry` is constructor-injected. This spec defines what to measure and how.

## Micrometer as the metrics facade

- All metrics in production code MUST use the Micrometer API (`io.micrometer:micrometer-core`).
- No concrete Micrometer registry (`micrometer-registry-prometheus`, etc.) MAY be declared as a `compile` or `runtime` dependency in any library module.
- `Metrics.globalRegistry` is banned in production code.

## Registry injection

- `DiameterSession` and `DiameterNode` MUST each provide two constructors:
  - A full constructor accepting a `MeterRegistry`.
  - A convenience constructor without `MeterRegistry` that delegates to the full constructor with `new SimpleMeterRegistry()`.
- `DiameterInitiatorSession` and `DiameterResponderSession` MUST mirror the same two-constructor pattern and forward to `super()`.
- `SimpleMeterRegistry` is part of `micrometer-core` — no additional dependency is required.

## What to instrument — RED method

### Rate — messages sent and received

- Outbound requests and answers MUST each increment a sent counter on write.
- Inbound requests and answers MUST each increment a received counter on receipt.

### Errors — request path

- A request that times out MUST increment an error counter.
- A request whose write to the channel fails MUST increment an error counter.
- A request that completes with a Diameter error answer (E-bit set) MUST increment an error counter.

### Errors — decode path

- A Diameter message that fails to decode (malformed frame, unsupported version, parse error) MUST increment a decode error counter.

### Duration — outbound request round-trip

- The round-trip time from sending a request to receiving its answer (success or error answer) MUST be recorded as a Timer.
- Timed-out and write-failed requests are NOT recorded in this Timer (no answer was received).

### Duration — inbound handler latency

- The time from handing an inbound request to a registered handler until the handler's `CompletableFuture` completes MUST be recorded as a Timer.
- This measures server-side processing time, independently of the outbound round-trip timer.

### Connections

- Each new inbound or outbound TCP connection that reaches the application layer MUST increment a connections counter tagged by direction (`inbound` / `outbound`).
- Each connection close MUST increment a disconnections counter tagged by direction.
- The number of currently open connections MUST be exposed as a Gauge (global, no tags).
- The number of currently open connections MUST also be exposed as a per-application-id Gauge, tagged with `application_id`. A connection that supports multiple application IDs contributes to each corresponding gauge independently. This gauge is updated after the CER/CEA handshake, once the negotiated application IDs are known.

## Naming

All meter names use the `diameter.` prefix and follow the [Micrometer naming conventions](https://docs.micrometer.io/micrometer/reference/concepts/naming.html).

## Tags

| Tag | Values | Notes |
|---|---|---|
| `command_code` | numeric command code, e.g. `257`, `280` | Bounded across supported command codes — deliberate cardinality choice |
| `message_type` | `request`, `answer` | Direction at the protocol level |
| `error_type` | `timeout`, `write_failure`, `error_answer` | Applied to the request error counter only |
| `direction` | `inbound`, `outbound` | Applied to connection counters and gauge |
| `application_id` | numeric Diameter application ID, e.g. `16777251` | Applied to the per-application-id active connections gauge only |

## Grafana dashboard

An importable Grafana dashboard JSON MAY be provided as `specs/metrics/grafana-dashboard.json`. It covers the meters defined in this spec and is suitable for import into a Grafana instance backed by a Prometheus registry.

## Base protocol messages

- CER/CEA (command code 257), DWR/DWA (command code 280), and DPR/DPA (command code 282) MUST be counted in `diameter.messages.sent` and `diameter.messages.received` using the same tag schema as application-layer messages.
- These are handled by `DiameterPeerHandler`, not by the session layer, so they are recorded via `DiameterTransportMeters`.

## Acceptance criteria

- `io.micrometer:micrometer-core` is declared as a `compile`-scope dependency in `sparta-diameter-base`.
- No concrete Micrometer registry is present in any library module's `compile` or `runtime` scope.
- `DiameterSession`, `DiameterInitiatorSession`, `DiameterResponderSession`, and `DiameterNode` each expose a no-arg-registry convenience constructor backed by `SimpleMeterRegistry`.
- All RED signals are recorded: rate (sent/received), errors (request path + decode), duration (round-trip + handler latency), and connection gauges/counters.
- Base protocol messages (CER/CEA, DWR/DWA, DPR/DPA) appear in the sent/received counters.
- The per-application-id active connections gauge is maintained correctly across connect and disconnect.
- No `Metrics.globalRegistry` reference exists in any production source file.
