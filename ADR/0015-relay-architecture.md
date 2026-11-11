---
title: "ADR-0015: Relay Architecture"
description: "Relay lives on DiameterSession itself with per-session translation state; no dedicated relay session subtype"
owner: "insomnick"
status: accepted
tags:
  - relay
  - session
  - rfc6733
created: "2026-09-22"
---

## Context

The Diameter Routing Agent (Yate-DRA successor) needs relay forwarding per RFC 6733 §6.1.9
(request: minimal mutation, fresh hop-by-hop identifier, preserved end-to-end identifier, appended
Route-Record) and §6.2.2 (answer: restore the original hop-by-hop identifier upstream). The
original feature sketch (`specs/relay-support/01-requirements.md`) placed a dedicated
`DiameterRelaySession` class in the session layer.

Implementation showed that relay state is inherently **per-connection state**, which the session
classes already own.

## Decision

### `relay()` sits on `DiameterSession`, not on a `DiameterRelaySession`

Every session (initiator and responder) can act as the egress side of a relay hop; the application
calls `egressSession.relay(upstreamSession, received, forward)`. The three reasons (from
`session/relay/package-info.java`):

1. The outbound hop-by-hop identifier must be unique **on the egress connection** (RFC 6733 §3) —
   it is minted by the egress session's per-connection `DiameterIdentifiers`.
2. The downstream answer arrives on the egress connection, so the answer dispatch (`complete`)
   that performs the translation lookup runs on the egress session anyway.
3. Request tracking (timeout task scheduled on the peer's event loop, duration meters, pending map)
   reuses the exact machinery of `sendAndTrack` — a `startTracking` shared by `send`, `sendAndTrack`
   and `relay`.

A dedicated session subtype would either duplicate the initiator/responder capability state
machines (CER/CEA handshake, watchdog, reconnect — ADR-0004) or be an empty marker class. Both are
strictly worse than inheriting `relay()` on the shared base.

Rejected alternative — free-standing `RelaySession` object holding translations and orchestrating
two sessions by reflection or package-private access: duplicate bookkeeping for timeout/pending
state the session already tracks, plus a second answer-dispatch path. The relay's translation half
lives in a small value-type package (`session/relay/`) instead.

### Answer restoration happens automatically in the `complete()` dispatch

`RelayTranslationStore` (per egress session, `ConcurrentHashMap<HopByHopId, RelayTranslation>`)
maps the outbound hop-by-hop identifier to `(upstream peer, original hop-by-hop id)`. The session's
answer dispatch (`complete`) checks the translation store first, then completes the pending
request (unchanged behavior for normal `send` traffic); on a hit `sendAnswerRestored` sends the answer to the
upstream peer as a `RelayedAnswer` with the original hop-by-hop identifier restored — Origin-Host/
Origin-Realm pass through untouched (they belong to the answering endpoint; RFC 6733 §6.2.2), the
fill-if-absent origin stamping of plain `send()` is deliberately skipped.

Cleanup needs **no separate timer**: every translation entry is paired with a pending-request
entry under the same key, so the per-request timeout task already scheduled on the peer's event
loop (plus write failures and connection loss) removes both halves together. A late answer after
cleanup finds neither and is logged as unsolicited. No reserved hop-by-hop identifier range is
needed — identifiers are unique per connection by construction.

### RelayForwardRequest as a frame type, not `GenericCommand.Out`

The outbound forwarded request is `RelayForwardRequest` (in `session/relay/`): an
`OutgoingRequest` frame that copies every AVP of the received request and the header flags
(command code, P-bit, retransmit bit, application id), implements `HasRouteRecordAVPs` so the
application appends its Route-Record with `addRouteRecord(...)` before relaying. Identifiers are
deliberately not carried over: `relay()` mints a fresh hop-by-hop on the egress session and
preserves the end-to-end identifier from the `received` argument.

Rejected alternative — reusing `GenericCommand.Out`: it is neither an `OutgoingRequest` (cannot
ride the wire encoder for a forwarded request) nor a `HasRouteRecordAVPs` (cannot carry the
mandatory Route-Record append).

### Loop detection

`LoopDetector` implements RFC 6733 §6.1.3/§6.1.7 (see `specs/loop-detection/02-design.md`). The
library contract for predictive avoidance: `filterCandidates` returns the surviving candidates as
a map; **an empty map is the 3002 signal** — deciding and answering `DIAMETER_UNABLE_TO_DELIVER`
is the caller's obligation, not the library's. Equally, `relay()` never appends the Route-Record:
**the caller hangs the Route-Record on** the forward frame (RFC 6733 §6.1.9: the relay identifies
itself); the DRA service appends the egress identity's Route-Record before each hop.

## Consequences

- Backwards compatibility is untouched: `relay()` is additive on `DiameterSession`; all sparta-hss
  call sites (config constructor, `connect`, session constructors) compile unchanged against this
  branch (verified per commit `ccbf444`).
- The relay future completes normally with every forwarded downstream answer, E-bit included, and
  exceptionally only when nothing was forwarded (timeout, write failure, connection lost, not
  open). A request handler may return it; the handler machinery does not answer a second time.
- Answer-path work is zero-code for the caller: as long as the request went through `relay()`, the
  `complete()` dispatch restores and forwards the answer automatically; `sendAnswerRestored` is
  public only for applications that intercept answers themselves.
- The empty-candidates signal couples the loop-avoidance result format to the caller: a caller
  that ignores an empty map would forward to nothing; the DRA answers 3002 in that branch.

## Related ADRs

- **See also:** ADR-0004 (per-connection session instances — the foundation for "relay state is
  connection state"), ADR-0014 (the SCTP transport the relay traffic rides).
- Specs: `specs/relay-support/02-design.md` (request/answer sequence), `specs/loop-detection/02-design.md`.
