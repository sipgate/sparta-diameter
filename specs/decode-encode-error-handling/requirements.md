# Decode/Encode Error Handling — Requirements

## Context

Cases 1–3 from ADR-0007 are implemented. This spec tracks what remains:
ADR-0007 Case 4 — unexpected Java exception.

## Case 4 — Unexpected Java exception

When decode or encode raises an exception that is not a modelled protocol error
(i.e. not a `DiameterResultCodeException` or `AVPParseException`):

- If the message was a **request** and both hop-by-hop and end-to-end identifiers
  were recovered from the header, the stack MUST reply with an `ErrorAnswer.Out`
  carrying `DIAMETER_UNABLE_TO_COMPLY (5012)`, then close the connection.
- Otherwise the stack MUST close the connection silently.

In both sub-cases the exception MUST be logged with its full stack trace via SLF4J.

Currently `DiameterSession.onParseError()` and `DiameterPeerHandler.exceptionCaught()`
both close the connection without logging and without attempting a 5012 reply.

## Acceptance criteria

- An unexpected exception during decode of a request whose header was fully parsed
  (identifiers recoverable) results in a `DIAMETER_UNABLE_TO_COMPLY (5012)` reply
  followed by connection close.
- An unexpected exception where the header was not fully parsed results in silent
  connection close with no reply.
- The exception is logged with its full stack trace via SLF4J in both sub-cases.
