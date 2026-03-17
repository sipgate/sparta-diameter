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
- **Deferred — CEA hop-by-hop validation**: `onMessage` does not yet verify the hop-by-hop ID in the
  CEA matches the one sent in the CER. Will be addressed in step 6 when the pending-request map is
  introduced (CER/CEA will share the same correlation mechanism as regular requests).
- **Deferred — disconnect reason callback**: when a session closes (capability mismatch, transport drop,
  DPR received), the surrounding application currently has no way to learn why. A
  `DiameterSessionListener` interface with an `onClosed(reason)` callback will be added in step 8
  alongside DPR/DPA.

### 4 — DWR/DWA

- On entering OPEN state: start Tw timer (TWINIT + jitter ±2 s per RFC 3539 §3.4.1)
- Any message received: reset Tw timer
- Tw expires, no pending DWR: send DWR, set `pending = true`, reset timer → stay `OKAY`
- Tw expires, pending DWR outstanding: `OKAY` → `SUSPECT`, failover signal, reset timer
- Tw expires in `SUSPECT`: → `DOWN`, close connection
- DWR received: send DWA immediately (base protocol, invisible to application)
- DWA received: `pending = false`; if `SUSPECT` → `OKAY` (failback)

### 5 — Handler binding

- `setHandler(Class<R extends Request>, DiameterRequestHandler<R, A>)` on both session classes
- Stored as `Map<Integer, DiameterRequestHandler<?, ?>>` keyed by command code
- Second registration for the same command code throws `IllegalStateException`
- Incoming request in OPEN state: look up handler by command code; if none → send error answer
- Handler returns `CompletableFuture<A>`; session sends the answer when it completes

### 6 — Sending requests and waiting for answers

- `<A extends Answer<A>> CompletableFuture<A> send(Request<?, A> request)` on both session classes
- Not in OPEN state → return failed future immediately
- Store `CompletableFuture` in `ConcurrentHashMap<Integer, CompletableFuture<Answer<?>>>` keyed by
  hop-by-hop identifier
- Incoming answer: remove from map by hop-by-hop id, complete future
- Channel write failure: remove from map, fail future

### 7 — Answer timeout

- On `send()`: schedule a `ScheduledFuture` via Netty's `EventLoop` for a configurable timeout
- On timeout: remove from pending map, complete future with `TimeoutException`
- On answer received before timeout: cancel the scheduled task
- Timeout duration configurable in `DiameterNodeConfig` (no RFC-mandated value; pick a sensible default)

### 8 — DPR/DPA

- `stop()` method on both session classes — sets a `shuttingDown` flag, fires `Stop` event
- `Stop` event: send DPR with appropriate `Disconnect-Cause`, transition to `CLOSING`
- DPR received in OPEN: send DPA, transition to `CLOSING`, close channel
- DPA received in `CLOSING`: transition to `CLOSED`, close channel
- In `CLOSING` / `CLOSED`: pending outbound requests fail immediately
- `shuttingDown = true` prevents reconnect logic from firing after graceful close

### 9 — Reconnect

- Owned by `DiameterClientSession` via the `Runnable reconnect` passed at construction
- Reconnect only fires when connection is lost unexpectedly (not after `stop()`)
- RFC 3539 reconnect path: `DOWN` state → Tc timer → call `reconnect.run()`
- `reconnect.run()` (constructed in `DiameterNode.doConnect()`): creates a new `DiameterClientSession`
  instance via the user-supplied factory, then does a fresh `Bootstrap.connect()`
- New instance starts in `CLOSED` / `INITIAL` — full CER/CEA handshake runs again
- Tc timer is cancelled when `stop()` is called
