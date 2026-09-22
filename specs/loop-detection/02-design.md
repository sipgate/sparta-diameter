# Loop Detection — Design

Implemented on branch `feature/relay-sctp` (commit `ccbf444`), class
`session/relay/LoopDetector` (static utility). Frequency note: this spec is implemented together
with relay support; the design below describes the shipped code and one deliberate delta from
`01-requirements.md`.

## Shared contract

- Both mechanisms read the **Route-Record AVPs already present in the inbound message** — they run
  *before* the relay appends its own Route-Record (append is caller-side, see
  `specs/relay-support/02-design.md`).
- Route-Records are read via the `HasRouteRecordAVPs` mixin, which the proxiable base request
  types (ASR, RAR, STR, ACR) implement. Requests without the mixin are treated as having no route
  records.
- **P-bit skip**: both checks return immediately (no detection / all candidates unchanged) when
  `isProxiable()` is false — non-proxiable messages are never relayed (RFC 6733 §3), so route
  records on them are not loop evidence.
- Both operate on *identities* (Origin-Host strings), compared with exact `String.equals` against
  each Route-Record value.

## `checkReceived(request, ownOriginHost)` — reactive detection (§6.1.3)

Returns `OptionalLong`: `of(RES_DIAMETER_LOOP_DETECTED)` = 3005 when `ownOriginHost` equals any
Route-Record value of the request, `empty()` otherwise. Skipped when the P-bit is clear or no
Route-Records are present.

## `filterCandidates(request, candidatesByIdentity)` — predictive avoidance (§6.1.7)

Input is a map from candidate identity (the Origin-Host the candidate peer advertises) to peer
connection — `DiameterPeer` does not expose the negotiated remote Origin-Host, so the caller
supplies the keying. Output preserves the input iteration order (`LinkedHashMap`) and drops every
candidate whose identity appears in a Route-Record.

> **Spec delta (deliberate):** the requirements said a found candidate "MUST [be] exclude[d]" and
> the agent "MUST ... attempt alternate routes" — the implemented library does *not* attempt
> alternates. `filterCandidates` returns the survivors once; **an empty map is the signal that
> all candidates were eliminated. Deciding what "attempt alternate routes" means (same group
> re-selection, alternate groups, DRA-specific policy) is application logic; the
> library's job is the RFC-mandated exclusion + signaling. The DRA service implements the caller
> side: after group selection against the filtered map, an empty survivor set → 3002 answer
> (RelayDispatcher step; covered by its tests).

## Testabdeckung

`LoopDetectorTest` (the contract):

| Test | Behavior |
|---|---|
| `it_reports_loop_detected_when_own_origin_host_is_in_a_route_record` | 3005 on own identity hit |
| `it_reports_no_loop_when_own_origin_host_is_not_in_a_route_record` | empty → forward |
| `it_skips_loop_detection_when_proxiable_bit_is_clear` | P-bit clear → no 3005 |
| `it_excludes_candidates_whose_identity_appears_in_a_route_record` | selective exclusion, order kept |
| `it_excludes_all_candidates_when_every_identity_appears_in_a_route_record` | **empty map = 3002 signal** |
| `it_returns_candidates_unchanged_when_proxiable_bit_is_clear` | P-bit clear → no exclusion |

Service-side coverage: `RelayDispatcherTest` verifies the caller contract (`it_answers_3005_…` and
the 3002-on-empty-survivors path in the peer-group flow).

## Caveat

Requests that parse as `GenericCommand.In` do not implement `HasRouteRecordAVPs`, so reactive
detection silently sees "no route records" for them (documented in the DRA review; production
message types arrive as concrete typed requests).
