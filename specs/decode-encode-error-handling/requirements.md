# Decode/Encode Error Handling — Requirements

## Context

`DiameterMessageDecoder` currently calls `Command.parseMessage()` with no error handling. Three distinct failure modes can arise when the stack decodes an incoming message or encodes an outgoing one; each requires a specific response. ADR-0007 documents the decision. This spec translates it into acceptance criteria for implementation.

This spec depends on `specs/logging/` and `specs/command-flags/` being implemented first: the former provides the SLF4J setup required for exception logging; the latter introduces `ErrorAnswer.Out`, the outgoing message type used for E-bit error replies.

## Case 1 — Valid header, no factory match

When the message header is structurally valid but no registered `DiameterPackageFactory` handles the command code / application ID combination, the stack MUST decode the message as `GenericCommand.In` and deliver it to the application layer unchanged. No error is sent and the connection remains open.

The `GenericCommand.In` path MUST NOT mask decode failures: if the header is itself malformed, Case 2 applies regardless of whether a factory would have matched.

## Case 2 — Invalid message (RFC 6733 protocol violation)

When an incoming message violates RFC 6733 at the structural level:

- If the message is a **request** and both hop-by-hop and end-to-end identifiers could be recovered from the header, the stack MUST:
  1. Send an `ErrorAnswer.Out` with the appropriate result code:
     - `DIAMETER_INVALID_HDR_BITS (3008)` — reserved bits set or flags inconsistent with the command code
     - `DIAMETER_INVALID_MESSAGE_LENGTH (5015)` — reported length does not match actual payload
     - `DIAMETER_INVALID_AVP_LENGTH (5014)` — an AVP length field is out of range
  2. Close the transport connection after sending the answer.

- If the message is an **answer**, or if the header cannot be parsed far enough to recover both identifiers, the stack MUST close the transport connection silently (no reply). RFC 6733 §7.2 prohibits an error-bit reply to an answer.

In all invalid-message sub-cases the connection MUST be closed. A malformed message is a protocol-level failure; the connection state after receiving one is undefined.

## Case 3 — Unexpected Java exception

When decode or encode raises an exception that is not a modelled protocol error (i.e. not covered by Case 2):

- If the message was a **request** and both identifiers were recovered, the stack MUST reply with an `ErrorAnswer.Out` carrying `DIAMETER_UNABLE_TO_COMPLY (5012)`, then close the connection.
- Otherwise the stack MUST close the connection silently.

In both sub-cases the exception MUST be logged with its full stack trace via SLF4J.

## Acceptance criteria

- When `Command.parseMessage()` throws for a well-formed request (identifiers recoverable), an error answer with the correct result code is sent before the connection is closed.
- When `Command.parseMessage()` throws for an answer, or when the header is unreadable, the connection is closed silently with no reply.
- An inbound message that matches no factory is delivered to the application layer as `GenericCommand.In`; no error is sent and the connection stays open.
- An unexpected Java exception during decode or encode of a request (identifiers recoverable) results in a `DIAMETER_UNABLE_TO_COMPLY` reply followed by connection close; the exception is logged.
- An unexpected Java exception where identifiers are not recoverable results in silent connection close; the exception is logged.
- There is no retry or recovery at this layer; every error path closes the connection unconditionally.
