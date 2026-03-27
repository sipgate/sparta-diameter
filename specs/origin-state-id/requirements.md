# Origin-State-Id — Requirements

## Context

RFC 6733 §8.16 defines the Origin-State-Id AVP (code 278, type Unsigned32) as a monotonically increasing value that a Diameter entity MUST advance whenever it restarts with loss of previous state (e.g., reboot). §8.6 describes how a receiving peer can use it to infer that sessions belonging to a lower Origin-State-Id are no longer active.

The `DiameterNodeConfig` already models the node identity and capabilities. Origin-State-Id belongs there as a startup-time value that is included in CER and CEA.

## Generation (§8.16)

- A Diameter entity issuing an Origin-State-Id AVP MUST create a strictly higher value each time its state is reset (restart, power cycle).
- The value MAY be set to the time of startup (e.g., Unix epoch seconds) or derived from a counter retained in non-volatile memory across restarts.
- If a node does not intend to allow other peers to infer session termination from it, it MUST either omit Origin-State-Id from all messages or set its value to 0.

## Inclusion in messages (§8.16)

- Origin-State-Id MAY be included in any Diameter message, including CER and CEA.
- If a proxy modifies the Origin-Host AVP, it MUST either remove Origin-State-Id from the message or adjust it to reflect the new origin.

## Crash detection on receipt (§8.6)

- A Diameter node receiving a CER or CEA from a peer SHOULD compare the received Origin-State-Id against the last known value for that peer.
- If the new value is strictly greater than the previously recorded value, the peer has restarted and any sessions associated with the lower Origin-State-Id should be treated as terminated.
- The session-level consequence of a detected restart (e.g., invoking `onDisconnected` for affected sessions) is defined by the session layer, which is out of scope for this spec and depends on the application.

## Acceptance criteria

- `DiameterNodeConfig` exposes an `originStateId` value that is stamped into CER and CEA at connection time.
- The library provides a mechanism (e.g., a static factory or constructor parameter) for the application to supply the Origin-State-Id value; the library does not generate it autonomously.
- The session layer records the peer's Origin-State-Id from the received CER or CEA.
- If a subsequent CER is received from the same peer with a strictly higher Origin-State-Id, the session layer emits a peer-restart event (callback or similar) so the application can react.
