# Relay Support — Design

Implemented on branch `feature/relay-sctp` (commits `ccbf444`, `8375a4f`); the placement decision
(no dedicated `DiameterRelaySession`) is ADR-0015. This document describes how the implemented
code works.

## Components

| Type | Location | Role |
|---|---|---|
| `DiameterSession.relay(upstream, received, forward)` | `session/DiameterSession.java` | public relay entry point on every session |
| `DiameterSession.sendAnswerRestored(IncomingAnswer)` | `session/DiameterSession.java` | answer restoration (public for caller-triggered use; normally invoked by `complete()`) |
| `RelayTranslation` | `session/relay/RelayTranslation.java` | value: upstream peer + original hop-by-hop id + preserved end-to-end id + timestamp |
| `RelayTranslationStore` | `session/relay/RelayTranslationStore.java` | per-session map outbound-hop-by-hop → translation |
| `RelayedAnswer` | `session/relay/RelayedAnswer.java` | outgoing answer frame: downstream answer restored with the original hop-by-hop id |
| `RelayForwardRequest` | `session/relay/RelayForwardRequest.java` | outgoing request frame: full copy of the received request, `HasRouteRecordAVPs` |

## Request path (sequence)

```
Inbound request (proxiable) on upstream session S_in
  │  (application owns the sequence below — the DRA does it in RelayDispatcher)
  ▼
1. P-bit check: !isProxiable() → never relayed (answer DIAMETER_COMMAND_UNSUPPORTED)
2. LoopDetector.checkReceived(received, ownOriginHost) → 3005 on loop (BEFORE append)
3. Rule/peer selection; LoopDetector.filterCandidates → empty map ⇒ caller answers 3002
4. forward = new RelayForwardRequest(received)     // copies ALL AVPs + header flags
   forward.addRouteRecord(egressOriginHost)       // CALLER OBLIGATION (RFC 6733 §6.1.9),
                                                  // appended last
5. egressSession.relay(S_in, received, forward):
     a. guard: egress state must be I_OPEN/R_OPEN, else failed future
     b. fresh hopByHop = egressSession.identifiers.nextHopByHop()
     c. endToEnd = received.endToEndId()          // preserved unchanged (RFC 6733 §3)
     d. startTracking(future, cmdCode, appId, hbh, requestTimeout)   // same machinery as sendAndTrack
     e. relayTranslations.put(hbh, RelayTranslation(S_in.peer(), received.hopByHopId(), …))
     f. peer.send(forward, hbh, endToEnd)         // NO fill-if-absent Origin stamping
        write failure → fail(hbh, cause)          // cleans pending + translation
  ► forward object is never mutated (outgoing AVP list is a private copy)
```

Notes:

- `relay()` bypasses the `send()` helper that stamps absent Origin-Host/Origin-Realm with the
  local identity — a relayed request already carries the original origin (RFC 6733 §6.1.9);
  stamping ours would be a protocol violation. All other AVPs pass through unchanged.
- The returned future completes with the downstream `IncomingAnswer` (including E-bit error
  answers) after it was forwarded upstream, or exceptionally when no answer was forwarded
  (`IllegalStateException` not open / connection lost, `TimeoutException`, write failure).
- A request handler may return that future: `dispatchInboundRequest` sends nothing for a relayed
  answer (it recognises the `IncomingAnswer` with the request's end-to-end identifier).

## Answer path (sequence)

```
Downstream answer arrives on egress session S_out
  ▼
complete(answer)                                   // answer dispatch
  1. sendAnswerRestored(answer) — on a relay translation hit:
           translation = relayTranslations.remove(hbh)        // atomic; duplicate answers
                                                              // cannot double-forward
           restored = RelayedAnswer.of(answer, translation.originalHopByHop())
           // Origin-Host/Realm pass through (answering endpoint's identity, RFC 6733 §6.2.2)
           translation.upstreamPeer().send(restored)
             .addListener(WARN on write failure)              // review finding, fixed
  2. pendingRequests.remove(hbh): relay() future completes normally with the answer (E-bit
     included); a plain send() future fails with DiameterErrorAnswerException on E-bit (ADR-0009)
  3. neither map matched → logged as unsolicited
```

`RelayedAnswer` copies command code, flags (including the E-bit), application id, the end-to-end
identifier and every AVP verbatim; only the hop-by-hop identifier is swapped to the original value.

## Timeout pairing and cleanup

Every translation entry is paired 1:1 with a pending-request entry under the same (outbound)
hop-by-hop identifier:

- **Request timeout**: the per-request timeout task on the peer's event loop (duration of
  `config.getRequestTimeout()`) fails the pending request and removes the translation in the same
  breath — no separate cleanup timer for the translation table.
- **Write failure / connection loss**: the same `fail(hopByHop, cause)` path removes both halves.
- **Late answer after cleanup**: matches neither map → "unsolicited answer" log, no restoration,
  no leak.
- No identifier-range reservation: hop-by-hop identifiers are unique per connection by
  construction (RFC 6733 §3), so the pending map and the translation store cannot collide over keys.

Test coverage (`DiameterSessionRelayTest`): fresh hop-by-hop + preserved end-to-end, forward
non-mutation, future completion + restored answer upstream, no override of a relayed answer's
origin, connection-not-open failure, cleanup on request timeout, no restoration for unsolicited
answers. `RelayForwardRequestTest` covers the full-AVP copy, flag carry-over and the
`addRouteRecord` mixin; `RelayTranslationStoreTest` covers put/lookup/remove/contains semantics.

## Origin passthrough

On both paths the relay never touches Origin AVPs:

- Request: forwarded untouched (relay bypasses fill-if-absent stamping).
- Answer: `RelayedAnswer` copies the downstream origin verbatim; RFC 6733 §8.16 additionally
  forbids modifying Origin-Host without adjusting Origin-State-Id — the relay modifies neither.
  The DRA service appends only its own Route-Record (egress identity) per RFC 6733 §6.1.9.
