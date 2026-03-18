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

### 5 — DWR/DWA

- On entering OPEN state: start Tw timer (TWINIT + jitter ±2 s per RFC 3539 §3.4.1)
- Any message received: reset Tw timer
- Tw expires, no pending DWR: send DWR, set `pending = true`, reset timer → stay `OKAY`
- Tw expires, pending DWR outstanding: `OKAY` → `SUSPECT`, failover signal, reset timer
- Tw expires in `SUSPECT`: → `DOWN`, close connection
- DWR received: send DWA immediately (base protocol, invisible to application)
- DWA received: `pending = false`; if `SUSPECT` → `OKAY` (failback)
- DWR/DWA use a separate boolean flag, not the pending-request map

### 6 — Handler binding

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

### 8 — DPR/DPA

- `stop()` method on both session classes — sets a `shuttingDown` flag, fires `Stop` event
- `Stop` event: send DPR with appropriate `Disconnect-Cause`, transition to `CLOSING`
- DPR received in OPEN: send DPA, transition to `CLOSING`, close channel
- DPA received in `CLOSING`: transition to `CLOSED`, close channel
- In `CLOSING` / `CLOSED`: pending outbound requests fail immediately
- `shuttingDown = true` prevents reconnect logic from firing after graceful close

### 9 — Reconnect

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

### Hop-by-hop and end-to-end identifier generation

Both identifiers are currently generated with `ThreadLocalRandom.current().nextInt()`. RFC 6733
§3 has stricter requirements:

- **Hop-by-hop**: "MUST be unique on a given connection". A per-session `AtomicInteger` counter
  starting at a random seed satisfies this with no collisions.
- **End-to-end**: "used to detect duplicates". RFC 6733 §3 says implementations SHOULD set the
  high-order 12 bits to the low-order 12 bits of current time and the low-order 20 bits to a
  random value. This survives reboots and allows duplicate detection across sessions.

Centralise generation in a package-private `DiameterIdentifiers` utility and replace the
`ThreadLocalRandom` calls in `buildCer`.

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
