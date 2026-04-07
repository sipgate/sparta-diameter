# Retransmit After Link Failure — Requirements

## Context

RFC 6733 §1.7: when a request has not been acknowledged and is resent after a link failure, the
sender **MUST** set the T (potentially retransmitted) flag and **MUST** preserve the original
end-to-end identifier. The hop-by-hop identifier is freshly generated for the new connection.

The RFC does not mandate retransmission — failing pending requests on disconnect is also
conformant. If retransmission is implemented it must follow these rules.

## Acceptance criteria

- Pending requests survive a connection drop and are retransmitted on the new connection
- Each retransmitted request has the T flag set in its header
- Each retransmitted request carries the original `EndToEndId`
- Each retransmitted request carries a newly generated `HopByHopId`
- The original `CompletableFuture` returned to the caller is reused — the caller's future
  completes when the answer arrives on the new connection, with no additional API changes
- Requests that cannot be retransmitted (e.g. after `stop()`) fail with an appropriate exception

## Dependencies

Requires `specs/identifier-types/` to be complete (`HopByHopId`, `EndToEndId`,
`OutgoingRequest` types must exist). The pending-requests map structure changes introduced here
(`PendingRequest` record) are also a prerequisite for `specs/relay-support/` answer-path
identifier restoration.
