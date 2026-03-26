---
title: "ADR-0003: Multi-Module Maven Structure"
description: "Split the project into focused Maven modules aligned with protocol standards and consumer boundaries"
owner: "sipgate-uhlig"
status: accepted
tags:
  - adr
  - maven
  - architecture
created: "2026-03-25"
---

## Context

The project started as a single Maven module. As support for 3GPP-defined Diameter interfaces is added, different consumers emerge with non-overlapping needs: an SMSC needs the SGd interface, an HSS needs Cx/Dx and S6a — neither needs the other. Keeping everything in one module forces unrelated dependencies on every consumer.

Additionally, encoding the protocol standard in the module boundary makes the dependency rule explicit: 3GPP interface modules depend on the base, never the other way around.

The project is open source, so artifact naming must be clean and self-explanatory to external consumers.

## Decision

The project is restructured as a Maven monorepo with the following modules:

| Artifact ID                    | Java package root              | Scope                                         |
|-------------------------------|-------------------------------|-----------------------------------------------|
| `sparta-diameter-base`        | `sparta.diameter.base`        | RFC 6733 Diameter Base Protocol               |
| `sparta-diameter-3gpp-common` | `sparta.diameter._3gpp.common`| Shared 3GPP AVPs and result codes             |
| `sparta-diameter-3gpp-s6c`   | `sparta.diameter._3gpp.s6c`  | TS 29.338 §5 — S6c (HSS ↔ SMS-SC/Router)     |
| `sparta-diameter-3gpp-sgdgdd`| `sparta.diameter._3gpp.sgdgdd`| TS 29.338 §6 — SGd/Gdd (MME/SGSN ↔ SMS-SC)  |

All modules share the Maven group ID `com.sipgate.sparta` and live in the same repository.

Java packages use the `_3gpp` prefix because Java identifiers cannot start with a digit. Maven artifact IDs use `3gpp` without the underscore, as artifact IDs have no such restriction.

> **Guardrail:** `sparta-diameter-base` must not depend on any 3GPP module. The dependency arrow points inward only: `3gpp-*` → `base`.

## Consequences

- Consumers take only what they need — an SMSC depends on `sgdgdd` and `3gpp-common`, not on S6c or S6a.
- Protocol standard boundaries are enforced by the build system, not just by convention.
- A change to `base` that breaks the message API surfaces immediately across all dependent modules in the same build.
- The `_3gpp` prefix in Java package names is a minor visual inconsistency with the artifact ID naming, accepted as a consequence of Java identifier rules.
