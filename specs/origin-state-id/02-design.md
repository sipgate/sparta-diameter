# Origin-State-Id — Design

Implemented on branch `feature/relay-sctp` (commit `ccbf444`) in `DiameterNodeConfig`,
`DiameterSession` (stamping + recording) and `DiameterNodeConfig` constructor wiring. This design
describes the shipped code and one deliberate delta from `01-requirements.md`.

## Config field, application-supplied

`DiameterNodeConfig` gains `long originStateId` (RFC 6733 §8.16, code 278, Unsigned32):

- **0 = omit** the AVP (RFC 6733 §8.16's "MUST either omit or set 0" shape; implementation omits
  entirely on 0).
- The value is **supplied by the application in the constructor** — the library never generates it
  (e.g. epoch seconds of the boot that last reset the node's state; MUST be strictly higher after
  every restart with loss of previous state). Existing constructors are unchanged and delegate
  with `0`: all sparta-hss call sites (8-arg config ctor) compile untouched — adding the field
  only in the full 10-arg constructor is the backwards-compatible seam.

## CER/CEA stamping

`DiameterSession.populateCapabilityAvps` (call sites: CER build on `onConnected`, CEA build in the
responder handshake) calls `stampOriginStateId(message)` after the vendor/product/host-ip/app
AVPs: stamps the configured value onto any message implementing `HasOriginStateIdAVP` **only when
non-zero**. A node configured with 0 stays silent across CER and CEA.

## Remote recording and restart detection (§8.6)

- Recording point: `handleCer` (responder) and `handleCea` (initiator) call
  `recordRemoteOriginStateId(remoteOriginHost, remoteOriginStateId)`; an absent AVP is passed as
  `-1` and ignored.
- **Storage is a `static` map** (`REMOTE_ORIGIN_STATE_IDS`: remote Origin-Host → last seen value,
  `ConcurrentHashMap`) — *deliberately* process-wide: sessions are per-connection instances and
  are never reused across reconnects (ADR-0004), so per-session storage could never compare "this
  peer, this time last connect". A given remote host is reachable through one node identity in
  practice, which makes the host key valid process-wide. The trade-off (no reset hook, values
  survive a peer-list reconfiguration) is accepted; documented here knowingly.
- **Detection = WARN log**: on `compute`, a new value **strictly greater** than the recorded one
  logs a warning —
  `peer restart detected for <host>: Origin-State-Id increased from X to Y; sessions established
  before the restart are no longer valid` — and the new value is recorded. A lower (non-monotonic,
  misbehaving) value is recorded **without** a warning.

> **Spec delta (deliberate):** `01-requirements.md` asked for a "peer-restart event (callback or
> similar) so the application can react". The implementation exposes this only as a WARN log; a
> callback/event hook with session-level cleanup for affected sessions is **out of scope** here and
> left to a future feature (the DRA's own session cleanup on connection loss is handled by the
> pending-request/timeout machinery). Recorded as a follow-up, not needed by the DRA service MVP.

## Testabdeckung

`OriginStateIdTest` covers: stamping into CER and CEA when configured; omission when not
configured; recording from received CER and CEA; restart detection (strictly higher → warning);
lower value → record without warning and overwrite; CER without Origin-State-Id ignored.

The existing test suite also pins the BC promise: all previous `DiameterNodeConfig` constructor
signatures keep their behavior (`DiameterNodeConfigTest`).
