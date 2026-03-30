# Command Flags — Requirements

## Context

RFC 6733 §3 defines the Command Flags byte in the Diameter message header. Two flags are relevant to message routing and error signalling but are not yet enforced at parse time.

## The P-bit (proxiable)

RFC 6733 §3 (line ~1921): if the P-bit is set, the message MAY be proxied, relayed, or redirected. If cleared, the message MUST be locally processed. A relay or proxy agent therefore MUST NOT forward a message with the P-bit cleared.

## The E-bit (error)

RFC 6733 §3 (line ~1926): if the E-bit is set, the answer contains a protocol error and will not conform to the normal CCF for that command. These are commonly called "error messages". The E-bit MUST NOT be set on request messages (see RFC 6733 §7.2).

## Current state

`Command.parseMessage` reads the flags byte but discards the P-bit and E-bit values — `isProxiable` and `isError` were removed without being surfaced on the parsed `IncomingCommand`. Neither flag is enforced or exposed at parse time.

## Acceptance criteria

- Receiving an answer with the E-bit set routes to `ErrorAnswer.In` rather than the command-specific answer type, and completes the sender's future exceptionally with `DiameterErrorAnswerException`.
- A request handler can signal a protocol error by completing its future exceptionally with `DiameterErrorAnswerException(ErrorAnswer.Out)`; the session sends that error answer to the peer.
- P-bit and relay enforcement are out of scope for this node (endpoint only); deferred indefinitely.
