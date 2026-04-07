# Command Flags — Requirements

## Context

RFC 6733 §3 defines the Command Flags byte in the Diameter message header. The P-bit is relevant to message routing but is not yet enforced.

## The P-bit (proxiable)

RFC 6733 §3 (line ~1921): if the P-bit is set, the message MAY be proxied, relayed, or redirected. If cleared, the message MUST be locally processed. A relay or proxy agent therefore MUST NOT forward a message with the P-bit cleared.

## Current state

`Command.isProxiable()` is decoded and encoded correctly. It is not surfaced on `IncomingCommand` and is not enforced at routing time.

## Acceptance criteria

- P-bit and relay enforcement are deferred indefinitely; this node is a Diameter endpoint, never a relay or proxy.
