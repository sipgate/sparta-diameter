---
title: "ADR-0017: Relay Integration Seams"
description: "Public relay APIs for advertised applications, predictive filtering, dialled sessions and error-answer AVPs"
owner: "sipgate-uhlig"
status: draft
tags:
  - relay
  - api
  - session
created: "2026-11-27"
---

## Context

Relay consumers such as the DRA need four pieces of state the library already computes but did not
expose: the applications a peer advertises in its CER/CEA, predictive loop filtering over the
consumer's own peer type, the initiator session created during an outgoing connect, and locally
generated error answers that omit Vendor-Specific-Application-Id while still mirroring
Auth-Session-State and Proxy-Info.

Without public seams, every relay reimplemented those rules. Duplicated protocol logic drifts:
the DRA extracted CER application ids itself, copied Route-Record filtering because
`LoopDetector.filterCandidates` only accepted `DiameterPeer` values, smuggled the dialled session
out of the connect factory through an array, and rebuilt the error-answer AVP set.

## Decision

### Advertised applications on CER/CEA

CER and CEA implement `HasAdvertisedApplicationIds`, which unions top-level Auth-Application-Id,
Acct-Application-Id and the application ids inside every Vendor-Specific-Application-Id.
`HasVendorSpecificApplicationIdAVPs` exposes `getVendorSpecificApplicationIdValues()` so
`DiameterResponderSession` and consumers use one extraction.

### Generic predictive loop filtering

`LoopDetector.filterCandidates` is generic over the candidate value type. Candidates stay keyed by
their Origin-Host identity; the library compares those keys to the request's Route-Records
case-insensitively and preserves input order. `peersOf` remains the `DiameterPeer` convenience.

### Dialled session and connect future

`DiameterNode.dial` returns a `DialedConnection` holding the initiator session and the initial
connect future. The existing `connect` overloads keep returning `ChannelFuture` and delegate to
`dial`, so callers that only need the future are unaffected.

### Error-answer AVP mirroring

`DiameterMessageFactory.createErrorAnswer` accepts `ErrorAnswerAvps`:
`ALL` mirrors Auth-Session-State and Vendor-Specific-Application-Id, while
`AUTH_SESSION_STATE_ONLY` mirrors only Auth-Session-State. Proxy-Info is always copied per RFC 6733
§6.2. Both the error-message and the `Consumer<ErrorAnswer.Out>` overloads take the choice; the
existing overloads default to `ALL`.

### Rejected alternatives

- **Expose only route-record extraction**: consumers would still duplicate the filtering loop and
  its case-insensitive identity comparison.
- **Change `connect` to return `DialedConnection`**: breaks the existing return type for every
  caller that only wants the future. `dial` is additive.
- **Boolean flag on `createErrorAnswer`**: a trailing `false` is opaque at the call site and the
  first iteration exposed the flag on only one overload. The enum names both choices and covers
  every overload.
- **Separate application-id utility class**: a default method on the message type is discoverable
  where the AVPs already live and reuses the existing mixin getters.

## Consequences

- The new APIs are additive; existing `connect`, `createErrorAnswer` and `peersOf` callers keep
  their behaviour.
- `DiameterNode` now has two outgoing entry points. `connect` remains the short form; `dial` is
  for callers that need the session as well.
- Callers of the generic filter must key candidates by the Origin-Host identity the library
  compares against Route-Records.
- The library owns the protocol rules, so relay behaviour can be tested once instead of per
  consumer.

## Related ADRs

- **See also:** ADR-0004 (session layer and the connect factory), ADR-0009 (error-answer
  signalling), ADR-0015 (relay architecture and loop detection).
