---
title: "ADR-0004: Session Layer Architecture"
description: "New instance per connection, two concrete session classes, factory-based lifecycle wiring"
owner: "sipgate-uhlig"
status: accepted
tags:
  - session
  - architecture
created: "2026-03-25"
---

## Context

The transport layer (`DiameterNode`, `DiameterPeer`, `DiameterPeerHandler`) was complete. The session
layer needed to be designed from scratch. Three structural questions had no obvious answer from the
RFCs: how session objects relate to connections, how the class hierarchy is shaped, and how the
`DiameterNode` API wires reconnect logic without hard-coupling to session internals.

## Decision

### New instance per connection

A new session object is created for every accepted or initiated connection. Session objects are
never reused across reconnects.

Rejected alternative — reusing persistent session objects: handlers would need to survive or be
re-registered across reconnects, creating ambiguity about whether a handler's accumulated state
is valid or stale. A fresh instance makes reconnect semantics unambiguous: the session starts
clean, the full CER/CEA handshake runs again, and handlers are registered before the connection
is opened.

### Two concrete classes, one shared base

`DiameterResponderSession` (R-peer, RFC 6733) and `DiameterInitiatorSession` (I-peer, RFC 6733)
both extend `DiameterSession`, which implements `DiameterConnectionListener`. The base class owns
shared state and helpers; the subclasses own their respective state machines.

### `DiameterNode` API: `Supplier` vs. `Function`

- `DiameterNode.listen()` takes `Supplier<DiameterConnectionListener>` — called once per accepted
  connection; no additional context needed.
- `DiameterNode.connect()` takes `Function<Runnable, DiameterConnectionListener>` — `DiameterNode`
  constructs the reconnect `Runnable` and injects it into the session at construction time.

Rejected alternative — passing the same `Supplier` to `connect()`: the session would have no
access to the reconnect runnable at construction. The reconnect `Runnable` must capture the
factory and the node's connect logic, so it is injected rather than pulled.

## Additional design choices

These decisions are smaller in scope but are documented here rather than left as implicit code
knowledge.

- **Two state enums, never merged** — `PeerState` (RFC 6733 §5.6) and `WatchdogState`
  (RFC 3539 Appendix A) are independent state machines governed by separate RFCs. Merging them
  into one enum would obscure which RFC governs each transition.

- **`.send()` when not OPEN returns a failed `CompletableFuture` immediately** — the alternative,
  silent queuing, hides the error from the caller. Callers must be aware that the connection is
  not yet (or no longer) open and handle the failure explicitly.

- **Exactly one handler per command code, enforced at registration** — duplicate registration
  throws `IllegalStateException`. Silent last-wins semantics would mask misconfiguration that
  only surfaces at runtime under load.

- **DWR/DWA/DPR/DPA invisible to application handlers** — base protocol maintenance traffic is
  not application concern. Exposing it would force every handler author to filter it out
  defensively.

- **DWR routed through `sendAndTrack`** — hop-by-hop correlation for DWA is handled by the
  pending-request map for free, identical to any other request/answer pair. No separate
  `dwrHopByHop` field is needed. The DWA is invisible to the application because the future
  is held internally as `pendingDwr`.

- **`sendAndTrack` accepts `Duration.ZERO` to disable the generic request timeout** — DWR must
  not time out on the 10 s default before the Tw cycle (30 s) completes. The watchdog state
  machine, not the request timeout, controls what happens when a DWA is late.

- **`cancel(hopByHop)` on `DiameterSession`** — removes the entry from the pending map, cancels
  the timeout task, and completes the future with `CancellationException`. Used when any message
  arrives while a DWR is in flight (the message proves the link is alive, so the pending DWR is
  stale) and when the connection closes before a DWR is answered.

- **`pendingDwr` is `CompletableFuture<DeviceWatchdogAnswer>`, not a boolean flag** — the
  done/cancelled state of the future replaces a separate `dwrPending` boolean. A `whenComplete`
  callback drives the `SUSPECT → OKAY` failback transition without additional fields.

- **`cerHopByHop` stored in `DiameterInitiatorSession` on `onConnected`** — an incoming CEA
  whose hop-by-hop identifier does not match the stored value is silently discarded, consistent
  with RFC 6733 §6.2.

## Consequences

- Session instances are lightweight and short-lived; reconnect produces a clean slate.
- The `DiameterNode` API is symmetric: both `listen()` and `connect()` take a functional factory
  with just enough context for their respective use cases.
- The watchdog state machine owns DWR lifecycle entirely; the application layer is unaware of it.
- Developers adding a new command handler never encounter DWR/DWA/DPR/DPA in their handler map.
