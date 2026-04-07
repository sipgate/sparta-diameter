# Loop Detection — Requirements

## Context

RFC 6733 §6.1.3 and §6.1.7 define two complementary loop-prevention mechanisms for relay and proxy agents: reactive detection (checking Route-Record AVPs on receipt) and predictive avoidance (checking before choosing a route). Neither is currently implemented in the relay path.

## Reactive Loop Detection (§6.1.3)

A relay or proxy agent MUST check for forwarding loops when receiving requests. A loop is detected if the agent finds its own identity in a Route-Record AVP. When a loop is detected, the agent MUST answer with `Result-Code = DIAMETER_LOOP_DETECTED (3005)`.

## Predictive Loop Avoidance (§6.1.7)

Before forwarding or routing a request, agents SHOULD check whether any candidate route's peer identity already appears in a Route-Record AVP. If a candidate peer's identity is found in a Route-Record AVP, the agent MUST exclude that route and attempt alternate routes. If all candidate routes are eliminated, the agent SHOULD return `DIAMETER_UNABLE_TO_DELIVER (3002)`.

## Interaction with relay-support

The relay path (see `specs/relay-support/`) appends a Route-Record AVP when forwarding a request. Loop detection must be performed *before* that append, on the Route-Record AVPs already present in the inbound message.

## Acceptance criteria

- When a relay or proxy agent receives a proxiable request and its own Origin-Host identity is present in any Route-Record AVP, it MUST return an error answer with `Result-Code = DIAMETER_LOOP_DETECTED` instead of forwarding.
- Before selecting a candidate peer to forward a proxiable request to, the relay path MUST check whether that peer's identity appears in any Route-Record AVP already in the message; if so, that peer MUST be excluded as a candidate.
- If all candidate peers are excluded by the above check, the relay path MUST return an error answer with `Result-Code = DIAMETER_UNABLE_TO_DELIVER`.
- Loop detection is skipped for non-proxiable messages (P-bit clear); those are never relayed.
