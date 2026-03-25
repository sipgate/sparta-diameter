# Identifier Types — Requirements

## Goals

- Prevent accidental re-use of hop-by-hop or end-to-end identifiers across sends
- Prevent cross-session identifier collisions (e.g. passing a hop-by-hop id from session A into
  a call on session B)
- Allow relay agents to forward requests with the original end-to-end identifier preserved
- Prefer compile-time over runtime enforcement

## Acceptance criteria

- Swapping `HopByHopId` and `EndToEndId` arguments must not compile
- Passing a wire-parsed incoming message to `Session.send()` must not compile
- A handler returning an incoming message as its answer must not compile
- Calling an AVP setter on a wire-parsed incoming message throws `UnsupportedOperationException`
- `Session.send()` generates both identifiers; the caller never sets them on the request object
- `DiameterMessageFactory.createAnswer()` copies identifiers from the incoming request into the
  outgoing answer automatically

## Out of scope

The relay API (`Session.relay()`) and retransmit-after-reconnect use these types but are specified
separately in `specs/relay-support/` and `specs/retransmit/`.
