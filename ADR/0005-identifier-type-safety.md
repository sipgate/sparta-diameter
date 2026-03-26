---
title: "ADR-0005: Identifier Type Safety"
description: "HopByHopId and EndToEndId as distinct records; per-command In/Out nested classes for direction enforcement"
owner: "sipgate-uhlig"
status: accepted
tags:
  - adr
  - session
  - identifiers
  - type-safety
created: "2026-03-25"
---

## Context

Hop-by-hop and end-to-end identifiers are currently raw `int` values throughout the codebase.
RFC 6733 §3 gives them distinct semantics: hop-by-hop is per-connection and used for
request/answer correlation; end-to-end survives reconnects and is used for duplicate detection.
Swapping them is a silent compile-time success and a runtime protocol violation.

A second, related problem: nothing prevents passing a wire-parsed incoming message to
`Session.send()`, or calling AVP setters on a message that was received over the wire and should
be treated as read-only.

## Decision

### Distinct identifier records

`HopByHopId` and `EndToEndId` are separate records, each wrapping a single `int`:

```java
record HopByHopId(int value) {}
record EndToEndId(int value) {}
```

Swapping them is a compile error anywhere they appear as distinct parameters. The pending-requests
map key becomes `HopByHopId` — self-documenting without relying on parameter names or comments.

### Per-command `In` / `Out` nested classes

Every command type is split into `In` (wire-parsed, incoming) and `Out` (application-created,
outgoing) as static nested classes of the enclosing command class. The enclosing class is never
instantiated directly — it exists only to hold the shared generic signature and AVP mixins via
the F-bounded type parameter `T`:

```
MoForwardShortMessageRequest<T extends MoForwardShortMessageRequest<T, A>, A extends Answer<A>>
  MoForwardShortMessageRequest.In  extends MoForwardShortMessageRequest<In,  MoForwardShortMessageAnswer.Out>  implements IncomingRequest
  MoForwardShortMessageRequest.Out extends MoForwardShortMessageRequest<Out, MoForwardShortMessageAnswer.In>   implements OutgoingRequest
```

`Session.send(OutgoingRequest)` and `Handler<IncomingRequest, OutgoingAnswer>` enforce direction
at compile time. A wire-parsed `In` cannot be passed to `send()`; a handler cannot accidentally
return an `In` as its answer.

### Identifier ownership

- `IncomingCommand` carries a `final HopByHopId` and a `final EndToEndId` set by the wire parser.
- `OutgoingAnswer` carries a `final HopByHopId` and a `final EndToEndId` set at construction,
  copied from the `IncomingRequest` by `DiameterMessageFactory.createAnswer()`.
- `OutgoingRequest` carries no identifiers. `Session.send()` generates them and passes them to
  the encoder as explicit parameters — the command object is never modified.

### Runtime guard on `In` setters

AVP setters on any `IncomingCommand` (`IncomingRequest` or `IncomingAnswer`) throw
`UnsupportedOperationException`, matching the Java unmodifiable-collections contract.
Compile-time prevention would require splitting every AVP mixin into read and write halves —
the added complexity is not justified by the marginal safety gain over a fast runtime check.

## Rejected alternatives

**Composition — `DeviceWatchdog*` has a `Request` field**: the wrapper class still exposes
setters (delegating to the inner object) or throws `UnsupportedOperationException` at runtime.
This is a runtime check in disguise with extra indirection.

**Combined identifier pair as a separate handler parameter** — users could construct or supply
arbitrary pairs, making it straightforward to pass a hop-by-hop id from session A into a call on
session B. The cross-session safety guarantee disappears.

**Single class with direction marker interfaces** — a class that implements both
`IncomingRequest` and `OutgoingRequest` satisfies both; `Session.send(OutgoingRequest)` would
silently accept a wire-parsed message. Direction enforcement evaporates at the first cast.

## Consequences

- Swapping `HopByHopId` and `EndToEndId` is a compile error.
- Passing an incoming message to `Session.send()` is a compile error.
- The pending-requests map is typed `ConcurrentHashMap<HopByHopId, PendingRequest<?>>` —
  no comment needed to explain what the key represents.
- Every existing command class must be refactored to the `In`/`Out` nested structure.
  This is a one-time migration cost with no ongoing overhead.
- AVP setter misuse on `In` objects surfaces at runtime, not compile time. Acceptable given
  the cost of the alternative.

## Related ADRs

- **See also:** ADR-0004 (session layer architecture; pending-requests map context)
