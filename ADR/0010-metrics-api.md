---
title: "ADR-0010: Metrics API"
description: "Use Micrometer as the sole metrics facade; inject MeterRegistry via constructor"
owner: "sipgate-uhlig"
status: accepted
tags:
  - metrics
  - micrometer
created: "2026-03-30"
---

## Context

This project is a library. Application code that embeds it will already have a metrics backend in place — Prometheus, Datadog, Atlas, or any of the other registries Micrometer supports. Coupling the library to a specific backend would impose that choice on every consumer.

Micrometer is the de-facto standard metrics facade in the Java ecosystem. It provides a single API that maps to any supported backend via a pluggable `MeterRegistry`. The same model that SLF4J provides for logging (see ADR-0008) — facade in the library, binding in the application — applies directly here.

## Decision

All metrics in this project use the Micrometer API (`io.micrometer:micrometer-core`).

No concrete Micrometer registry implementation (`micrometer-registry-prometheus`, `micrometer-registry-datadog`, etc.) is declared as a `compile` or `runtime` dependency in any library module.

### Injection

The `MeterRegistry` is passed via constructor injection into `DiameterSession` and `DiameterNode`. Subclasses forward it to `super()`. Applications that do not want metrics pass a no-op registry via the convenience constructors, which default to `SimpleMeterRegistry`.

> **Guardrail:** `Metrics.globalRegistry` (the static Micrometer global) is banned in production code. A registry must always be supplied explicitly.

### Naming

Meter names follow the Micrometer [naming conventions](https://docs.micrometer.io/micrometer/reference/concepts/naming.html). All meters use the `diameter.` prefix and the domain term **command** rather than "message" — consistent with the library's own type hierarchy. The tag key for request/answer direction is `command_type`.

### Tags

Every meter tagged with `command_code` is also tagged with `application_id`. Diameter command codes are not globally unique — the same code can appear in different application contexts — so `application_id` is required for correct disambiguation. The cardinality is bounded by what the node actually handles; a focused deployment such as an HSS sees a small, stable label space.

**Cardinality attacks are out of scope.** A malicious peer could inflate the Diameter `Application-Id` or command code fields to generate unbounded label combinations, exhausting the metrics backend. Mitigating this (e.g. label allow-lists, cardinality limits) is the responsibility of the application layer, not this library.

The sent counter (`diameter.commands.sent`) increments only on confirmed write success. Write failures increment the error counter instead, preserving the invariant `sent + write_failure = total attempted`.

### MeterBinder

`io.micrometer.core.instrument.binder.MeterBinder` is not used. `MeterBinder` is designed for passive observation of external state (typically Gauges), and relies on lifecycle wiring that is unnecessary here. Constructor injection of `MeterRegistry` is sufficient and keeps the API surface minimal.

## Consequences

- The library compiles and runs without imposing a metrics backend on consumers.
- Consumers get Diameter protocol metrics for free by providing a `MeterRegistry` of their choice.
- Applications that do not want metrics incur no dependency overhead; they pass a no-op registry.

## Related ADRs

- **See also:** ADR-0008 (SLF4J logging — same facade pattern applied to observability)
