# Diameter Session Layer — Implementation Plan

## Context

The transport layer (`DiameterNode`, `DiameterPeer`, `DiameterPeerHandler`) is done.
Step 1 (`DiameterNodeConfig`) is done.

All new session/state classes go in `com.sipgate.sparta.diameter.session`.

### Key design decisions

- **New instance per connection** — no persistent session objects across reconnects.
  Handlers live in the factory closure, not in the session instance.
- **Two concrete classes**, both extending `DiameterSession` which implements `DiameterConnectionListener`:
  - `DiameterResponderSession` — responder (R- states), no reconnect (RFC 6733 R-peer)
  - `DiameterInitiatorSession` — initiator (I- states), receives a `Runnable reconnect` in its constructor (RFC 6733 I-peer)
- **`DiameterNode.listen()`** takes `Supplier<DiameterConnectionListener>` — called once per accepted connection.
- **`DiameterNode.connect()`** takes `Function<Runnable, DiameterConnectionListener>` — `DiameterNode`
  constructs the reconnect runnable and passes it into each new session instance.
- **Two separate state enums** — do not merge RFC 6733 and RFC 3539 states:
  - `PeerState` — RFC 6733 §5.6: `CLOSED`, `WAIT_CONN_ACK`, `WAIT_I_CEA`, `WAIT_CONN_ACK_ELECT`,
    `WAIT_RETURNS`, `I_OPEN`, `R_OPEN`, `CLOSING`
  - `WatchdogState` — RFC 3539 Appendix A: `INITIAL`, `OKAY`, `SUSPECT`, `DOWN`, `REOPEN`
- **`.send()` when not OPEN** — returns a failed `CompletableFuture` immediately; never queues silently.
- **Exactly one handler per command code** — enforced at registration time.
- **CER/CEA capabilities** — declared in `DiameterNodeConfig.Capabilities`; session validates intersection
  and sends `DIAMETER_NO_COMMON_APPLICATION` if empty.
- **DWR/DWA, DPR/DPA** — handled transparently by the session; application handlers never see them.
- **Tw** (TWINIT default 30 s, min 6 s) and **Tc** (default 30 s) are configured in `DiameterNodeConfig`.

---

## Remaining steps

### ~~2 — Session scaffolding~~ ✓ Done

- `PeerState` enum — all RFC 6733 §5.6 states
- `WatchdogState` enum — all RFC 3539 Appendix A states
- `DiameterSession` — shared state (`config`, `peer`, `peerState`, `watchdogState`, `negotiator`) and helpers (`populateCapabilityAvps`, `extractUnsignedInts`, `onDisconnected`)
- `DiameterResponderSession extends DiameterSession` — R- state machine, no reconnect
- `DiameterInitiatorSession extends DiameterSession` — I- state machine, `Runnable reconnect` in constructor
- `DiameterNode.listen()` takes `Supplier<DiameterConnectionListener>` ✓
- `DiameterNode.connect()` takes `Function<Runnable, DiameterConnectionListener>` ✓; `doConnect()` wires the reconnect runnable

### ~~3 — CER/CEA~~ ✓ Done

- `DiameterInitiatorSession.onConnected`: builds CER from config, sends it, transitions to `WAIT_I_CEA`
- `DiameterInitiatorSession.onMessage(CEA)`: `2001` → `I_OPEN`; other → `CLOSED` + `peer.close()`
- `DiameterResponderSession.onMessage(CER)`: computes capability intersection via `CapabilityNegotiator`; `2001`/`R_OPEN` or `5010`/`CLOSED`/`peer.close()`
- `HasHostIpAddressAVP` added to both `CapabilitiesExchangeRequest` and `CapabilitiesExchangeAnswer`

### ~~4 — Send requests and wait for answers~~ ✓ Done

- `public <A extends Answer<A>> CompletableFuture<A> send(Request<?, A> request)` on `DiameterSession` (inherited by both)
- Not in OPEN state → return already-failed future immediately
- `ConcurrentHashMap<Integer, CompletableFuture<?>>` keyed by hop-by-hop identifier in `DiameterSession`
- Incoming answer: `tryCompleteFromPendingMap` looks up hop-by-hop id, removes entry, completes future
- Channel write failure: listener removes entry and fails future
- `onDisconnected`: `failAllPending` completes all outstanding futures exceptionally

### ~~4b — Fix CEA hop-by-hop correlation~~ ✓ Done

- `DiameterInitiatorSession` stores the CER's hop-by-hop id in `cerHopByHop` on `onConnected`
- `onMessage(CEA)`: checks `cea.getHopByHopIdentifier() == cerHopByHop`; mismatches are silently discarded

### ~~5 — DWR/DWA~~ ✓ Done

- On entering OPEN state: start Tw timer (TWINIT + jitter ±2 s per RFC 3539 §3.4.1)
- Any message received: reset Tw timer; if a DWR is pending, cancel it — a received message
  proves the link is alive, so a stale DWA can no longer affect watchdog state
- Tw expires, no pending DWR: send DWR via `sendAndTrack`, store future in
  `pendingDwr: CompletableFuture<DeviceWatchdogAnswer>`; reschedule timer → stay `OKAY`
- Tw expires, `pendingDwr` non-null and not done: `OKAY` → `SUSPECT`, reschedule timer
- Tw expires in `SUSPECT`: → `DOWN`, close connection
- DWR received: send DWA immediately (base protocol, invisible to application)
- DWA received: completes `pendingDwr` naturally via `tryCompleteFromPendingMap`;
  clear `pendingDwr`; if `SUSPECT` → `OKAY` (failback)

#### Key design decisions

- **DWR routes through `sendAndTrack`** — hop-by-hop correlation is handled by the
  pending-request map for free. No separate `dwrHopByHop` field needed; DWA is
  matched the same way any answer is. The DWA is invisible to the application because
  the future is held internally as `pendingDwr`.
- **`sendAndTrack(request, timeout)` overload** — DWR must not use the default 10 s
  request timeout, which would fire before the Tw cycle completes (30 s). Instead DWR
  passes a Tw-derived timeout (or `Duration.ZERO` to disable) so the watchdog state
  machine, not the generic timeout, controls what happens when DWA is late.
- **`cancel(hopByHop)` on `DiameterSession`** — removes a pending entry from the map,
  cancels its timeout task, and completes the future with `CancellationException`.
  Used when any message arrives while a DWR is in flight (link proven alive) and when
  the connection closes before a DWR is answered.
- **`pendingDwr` is a `CompletableFuture<DeviceWatchdogAnswer>`**, not a boolean — the
  done/cancelled state of the future replaces a separate `dwrPending` flag, and
  `whenComplete` drives the SUSPECT → OKAY transition without extra fields.

### ~~6 — Handler binding~~ ✓ Done

- `setHandler(Class<R extends Request>, DiameterRequestHandler<R, A>)` on both session classes
- Stored as `Map<Integer, DiameterRequestHandler<?, ?>>` keyed by command code
- Second registration for the same command code throws `IllegalStateException`
- Incoming request in OPEN state: look up handler by command code; if none → send error answer
- Handler returns `CompletableFuture<A>`; session sends the answer when it completes

### ~~7 — Answer timeout~~ ✓ Done

- On `send()`: schedule a `ScheduledFuture` via Netty's `EventLoop` for a configurable timeout
- On timeout: remove from pending map, complete future with `TimeoutException`
- On answer received before timeout: cancel the scheduled task
- Timeout duration configurable in `DiameterNodeConfig` (no RFC-mandated value; pick a sensible default)
- Standards checked: RFC 6733, RFC 3539, 3GPP TS 29.338, TS 29.272, TS 29.002, GSMA IR.88, IR.67.
  None define a numeric request/answer timeout. RFC 3539 gives Tw = 30 s (watchdog) and IR.88 gives
  Tc = 30 s (transport reconnect) — both are infrastructure timers, not request timeouts. Recommend
  **10 s** as default: below both infrastructure timers, leaves headroom for agents in the path.

### ~~8 — DPR/DPA~~ ✓ Done

- `stop()` method on both session classes — sets a `shuttingDown` flag, fires `Stop` event
- `Stop` event: send DPR with appropriate `Disconnect-Cause`, transition to `CLOSING`
- DPR received in OPEN: send DPA, transition to `CLOSING`, close channel
- DPA received in `CLOSING`: transition to `CLOSED`, close channel
- In `CLOSING` / `CLOSED`: pending outbound requests fail immediately
- `shuttingDown = true` prevents reconnect logic from firing after graceful close

### ~~9 — Reconnect~~ ✓ Done

- Owned by `DiameterInitiatorSession` via the `Runnable reconnect` passed at construction
- Reconnect only fires when connection is lost unexpectedly (not after `stop()`)
- RFC 3539 reconnect path: `DOWN` state → Tc timer → call `reconnect.run()`
- `reconnect.run()` (constructed in `DiameterNode.doConnect()`): creates a new `DiameterInitiatorSession`
  instance via the user-supplied factory, then does a fresh `Bootstrap.connect()`
- New instance starts in `CLOSED` / `INITIAL` — full CER/CEA handshake runs again
- Tc timer is cancelled when `stop()` is called

---

## Later / deferred

These are known gaps that do not block the numbered steps but must be addressed before the library
is production-ready.

### ~~Hop-by-hop and end-to-end identifier generation~~ ✓ Done

Both identifiers are currently generated with `ThreadLocalRandom.current().nextInt()`. RFC 6733
§3 has stricter requirements:

- **Hop-by-hop**: "MUST be unique on a given connection". A per-session `AtomicInteger` counter
  starting at a random seed satisfies this with no collisions.
- **End-to-end**: "used to detect duplicates". RFC 6733 §3 says implementations SHOULD set the
  high-order 12 bits to the low-order 12 bits of current time and the low-order 20 bits to a
  random value. This survives reboots and allows duplicate detection across sessions.

Centralise generation in a package-private `DiameterIdentifiers` utility and replace the
`ThreadLocalRandom` calls in `buildCer`.

### Prevent misuse of hop-by-hop and end-to-end identifiers

**Goals**:
- Prevent accidental re-use of the same identifiers across sends
- Prevent cross-session identifier collisions (e.g. passing a hop-by-hop id from session A to session B)
- Allow diameter relays (forward with original identifiers)
- Prefer compile-time over runtime enforcement

#### Type hierarchy

Every command type is split into `In` (wire-parsed, incoming) and `Out` (application-created,
outgoing) as **static nested classes** of the enclosing command class. The enclosing class is
never instantiated directly — it exists only to hold the shared generic signature and AVP mixins.

```
OFR<T extends OFR<T, A>, A extends Answer<A>>   ← abstract, holds mixins HasSmRpUi<T> etc.
  OFR.In  extends OFR<OFR.In,  OFA.Out>  implements IncomingRequest
  OFR.Out extends OFR<OFR.Out, OFA.In>   implements OutgoingRequest
```

The F-bounded type parameter `T` flows into every AVP mixin (`HasSmRpUi<T>`, etc.), so
`setSmRpUi()` returns `OFR.In` when called on `In` and `OFR.Out` when called on `Out` —
no overrides needed. Each nested class also declares its answer type, so:

- `Session.send(OFR.Out)` returns `CompletableFuture<OFA.In>` — correct at compile time
- `Handler<OFR.In, OFA.Out>` — correct at compile time

**Identifiers**:
- Represented as two separate records `HopByHopId` and `EndToEndId`, each wrapping a
  single `int`. Rationale:
  - Swapping them is impossible anywhere — `encode(cmd, EndToEndId, HopByHopId)` is a
    compile error; raw `int`/`int` or a combined `Identifiers` record both still allow
    the swap at construction.
  - The pending-requests map key becomes `HopByHopId` — self-documenting without comments
    or relying on parameter names.
  - They are often used individually: hop-by-hop alone as the map key, end-to-end alone
    when echoing into an answer.
  - `new HopByHopId(msg.readInt())` is more verbose than a bare `int`, but that verbosity
    is clarity — it tells the reader what the value is without inspecting the call target.
- `IncomingCommand` carries `final Identifiers` set by the wire parser.
- `OutgoingAnswer` carries `final Identifiers` set at construction, copied from the
  `IncomingRequest` by `DiameterMessageFactory.createAnswer()`.
- `OutgoingRequest` carries no identifiers; `Session.send()` generates them and passes
  them to the encoder as explicit parameters — the `Command` object is never modified.

**AVP setters on `In`**: `In` inherits all mixin setters from the enclosing class. Compile-time
prevention would require splitting every mixin into read/write halves — too cumbersome.
A runtime guard in `Command.setAVP` throwing `UnsupportedOperationException` (matching the
Java unmodifiable-collections contract) is sufficient and cheap.

#### Rejected alternatives

- **Composition (`DeviceWatchdog* has a Request`)** — the wrapper still exposes setters or
  throws `UnsupportedOperationException` at runtime. Runtime check in disguise.
- **`Identifiers` as a separate handler parameter** — users could construct or pass arbitrary
  `Identifiers` values, defeating cross-session safety.
- **Single class with direction marker interface** — a class implementing both
  `IncomingRequest` and `OutgoingRequest` satisfies both; `Session.send(OutgoingRequest)`
  would accept a wire-parsed message. Direction enforcement evaporates.

See "Retransmit after link failure" for the full design, including end-to-end identifier
preservation and the required changes to the pending-requests map.

### Disconnect reason callback

When a session closes — whether due to a capability mismatch, unexpected transport drop, or a
received DPR — the surrounding application has no way to learn why. A `DiameterSessionListener`
interface with an `onClosed(CloseReason reason)` method should be added in step 8 alongside
DPR/DPA. `CloseReason` would enumerate at minimum: `CAPABILITY_MISMATCH`, `TRANSPORT_ERROR`,
`PEER_DISCONNECTED` (DPR), `LOCAL_STOP` (our DPR).

### Vendor-Specific-Application-Id in CER/CEA

The current `DiameterNodeConfig.Capabilities` model holds `authApplicationIds` and
`acctApplicationIds` as flat `List<Integer>` values. RFC 6733 §5.3 requires that vendor-specific
applications (e.g. 3GPP SGd, application-id `16777313`, vendor `10415`) be advertised via the
`Vendor-Specific-Application-Id` grouped AVP (`Auth-Application-Id` + `Vendor-Id` together), not
as bare top-level `Auth-Application-Id` AVPs. Strict DRAs and 3GPP nodes will reject CERs that
omit the grouping.

Required changes:
- Add `List<VendorSpecificApp> vendorSpecificApps` to `Capabilities`, where `VendorSpecificApp`
  is a value type holding `(long vendorId, int authApplicationId)` (acct variant if needed)
- `AbstractDiameterSession.populateCapabilityAvps` emits one `Vendor-Specific-Application-Id`
  grouped AVP per entry
- `CapabilityNegotiator` must also inspect `Vendor-Specific-Application-Id` AVPs from the remote
  CER when computing the intersection

### Relay support

RFC 6733 §6.1.9: a relay agent forwards a request to the next hop with minimal mutation:

- **Route-Record AVP**: the relay appends its own identity. Required. The application is
  responsible for adding this AVP to the outgoing request before calling `relay()`.
- **Hop-by-hop identifier**: replaced with a fresh `HopByHopId` generated by the session
  (identical to normal `send()`).
- **End-to-end identifier**: must not be modified (RFC 6733 §3). The caller provides the
  original `EndToEndId` from the incoming request explicitly.
- **All other AVPs**: must not be modified by a relay agent.

On the answer path the session must restore the original hop-by-hop identifier before
forwarding the answer back to the requester (RFC 6733 §6.2.2).

#### API

```java
CompletableFuture<A> relay(OutgoingRequest<A> outgoing, EndToEndId endToEnd);
```

The caller builds `outgoing` (copies AVPs, adds Route-Record), then passes
`incoming.getEndToEndId()` as the second argument. The session generates a new
`HopByHopId` and encodes both into the wire buffer; the `OutgoingRequest` object is
never modified.

### Retransmit after link failure

RFC 6733 §1.7: when a request has not been acknowledged and is resent after a link
failure, the sender **MUST** set the T (potentially retransmitted) flag and **MUST**
preserve the original end-to-end identifier. The hop-by-hop identifier is freshly
generated (new connection). The RFC does not mandate that an implementation retransmit
— failing pending requests on disconnect is also conformant — but if it does retransmit
it must follow these rules.

#### Requirement: store pending requests

The current pending-requests map stores `HopByHopId → CompletableFuture<?>`. To
retransmit after reconnect, the session must also retain the original request object
and its `EndToEndId`. The map entry becomes a record:

```java
record PendingRequest<A>(OutgoingRequest<A> request, EndToEndId endToEnd, CompletableFuture<A> future) {}
ConcurrentHashMap<HopByHopId, PendingRequest<?>> pendingRequests;
```

#### Reconnect path

After the Tc timer fires and a new `DiameterInitiatorSession` is created (step 9),
the old session's pending requests are passed to the new session. The new session
retransmits each one: sets the T flag, preserves the original `EndToEndId`, generates
a new `HopByHopId`. The original `CompletableFuture` is reused — the caller's future
eventually completes when the answer arrives on the new connection.
