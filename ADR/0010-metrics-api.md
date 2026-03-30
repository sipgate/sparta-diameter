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

The `MeterRegistry` is passed via constructor injection into `DiameterSession`. Subclasses (`DiameterInitiatorSession`, `DiameterResponderSession`) forward it to `super()`. Applications that do not want metrics pass a no-op registry (e.g. `new SimpleMeterRegistry()`).

> **Guardrail:** `Metrics.globalRegistry` (the static Micrometer global) is banned in production code. A registry must always be supplied explicitly.

### Naming

Meter names follow the Micrometer [naming conventions](https://docs.micrometer.io/micrometer/reference/concepts/naming.html). All meters defined by this library use the `diameter.` prefix.

### MeterBinder

`io.micrometer.core.instrument.binder.MeterBinder` is not used. `MeterBinder` is designed for passive observation of external state (typically Gauges), and relies on lifecycle wiring that is unnecessary here. Constructor injection of `MeterRegistry` is sufficient and keeps the API surface minimal.

## Consequences

- The library compiles and runs without imposing a metrics backend on consumers.
- Consumers get Diameter protocol metrics for free by providing a `MeterRegistry` of their choice.
- Applications that do not want metrics incur no dependency overhead; they pass a no-op registry.
- Meters are defined and named in one place; the spec (`specs/metrics/`) governs which events are instrumented and what tags are applied.

## Related ADRs

- **See also:** ADR-0008 (SLF4J logging — same facade pattern applied to observability)
