# Relay Support — Requirements

## Context

RFC 6733 §6.1.9 defines how a relay agent forwards a request to the next hop with minimal
mutation. The session layer must support this use case without exposing identifier internals to
the application.

## Acceptance criteria

- The application can forward an incoming request to another session via a dedicated `relay()`
  method
- The session generates a fresh `HopByHopId` for the outgoing request
- The original `EndToEndId` is preserved on the wire unchanged (RFC 6733 §3)
- The `OutgoingRequest` object passed to `relay()` is never modified
- The application is responsible for appending a `Route-Record` AVP before calling `relay()`
- `relay()` returns a `CompletableFuture<A>` that completes when the answer arrives
- All other AVPs in the outgoing request are forwarded unchanged

## Open design question

On the answer path RFC 6733 §6.2.2 requires the relay to restore the original hop-by-hop
identifier before forwarding the answer upstream. How the session exposes the restored identifier
to the relay handler (the original `HopByHopId` came in with the `IncomingRequest`) needs to be
worked out during design — the answer type returned by `relay()` must carry or allow access to
the original identifier so the handler can write the correct value into the upstream answer.

## Dependencies

Requires `specs/identifier-types/` to be complete (`HopByHopId`, `EndToEndId`, `OutgoingRequest`
types must exist).
