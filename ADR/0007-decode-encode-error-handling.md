---
title: "ADR-0007: Decode/Encode Error Handling"
description: "How the stack responds to unknown commands, invalid messages, and unexpected exceptions during decode/encode"
owner: "sipgate-uhlig"
status: accepted
tags:
  - error-handling
  - decode
  - encode
created: "2026-03-27"
---

## Context

Four distinct failure modes can arise when the stack decodes an incoming Diameter message or encodes an outgoing one:

1. The message header is structurally valid, but no registered factory handles the command code / application ID combination.
2. The message stream is corrupt at the framing level — header bits are invalid, the length field is wrong, or the version is unsupported.
3. The message is structurally valid but contains an AVP-level violation detectable without application knowledge.
4. Decode or encode triggers an unexpected Java exception that is not a modelled protocol error.

Each case requires a different response. Without an explicit decision, behaviour is undefined and implementations will diverge.

## Decision

### Case 1 — Valid command, no factory match

Decode as `GenericCommand.In`.

The message header is syntactically valid and the stack can represent it faithfully. Failing to match a factory is a capability gap, not a protocol violation. Delivering a `GenericCommand.In` to the application layer preserves the raw content and lets the application decide what to do (log, forward as-is, reply, or discard).

No error is sent. The connection remains open.

> **Guardrail:** The `GenericCommand.In` path MUST NOT be used to mask decode failures. If the message header itself is malformed, Case 2 applies regardless of whether a factory would have matched.

### Case 2 — Framing-level corruption

RFC 6733 distinguishes protocol errors (§7.1.3, 3xxx codes) from permanent failures (§7.1.5, 5xxx codes). Framing-level errors — where the byte stream itself cannot be trusted — fall into the protocol error category. The connection cannot continue after a framing failure because the decoder's position in the stream is undefined.

If the incoming message is a **request** and the header was parsed far enough to recover the hop-by-hop and end-to-end identifiers:

1. Send an error answer with the E-bit set and the appropriate Result-Code:
   - `DIAMETER_INVALID_HDR_BITS` (3008) — reserved bits set or flags inconsistent with the Command Code
   - `DIAMETER_INVALID_MESSAGE_LENGTH` (5015) — reported length does not match actual payload
   - `DIAMETER_UNSUPPORTED_VERSION` (5011) — version field is not 1; bytes 4–19 are still readable and used to construct the reply
2. Close the transport connection after sending the answer.

If the incoming message is an **answer**, or if the header cannot be parsed far enough to recover both identifiers: close the transport connection silently (no reply). RFC 6733 §7.2 prohibits sending a message with the E-bit in response to an answer message.

### Case 3 — AVP-level permanent failure

5001, 5014, and 5016 are permanent failures (RFC 6733 §7.1.5), not protocol errors. The byte stream is intact; only one AVP within an otherwise valid message is rejected. The connection remains open — a subsequent request may be perfectly valid.

If the incoming message is a **request** and the header was fully decoded:

1. Send an error answer with the E-bit set, the appropriate Result-Code, and a `Failed-AVP` wrapping the offending AVP:
   - `DIAMETER_AVP_UNSUPPORTED` (5001) — unrecognized AVP with M-bit set
   - `DIAMETER_INVALID_AVP_LENGTH` (5014) — AVP length field out of range; use a zero-padded stub if the header itself was incomplete
   - `DIAMETER_INVALID_AVP_BIT_COMBO` (5016) — reserved flag bits set
2. Keep the transport connection open.

RFC 6733 §7.1.5 states that permanent failures SHOULD be returned in answers with the E-bit not set (using the command-specific CCF). The E-bit form MAY be used when composing a command-specific answer is not possible or efficient. For these three codes the violation is detected before the command is fully decoded, so the E-bit form is the practical choice.

### Case 4 — Unexpected Java exception

If decode or encode raises an exception that is not a modelled protocol error (i.e. not covered by Cases 2 or 3):

- If the message was a **request** and the header was parsed far enough to recover both identifiers: reply with `DIAMETER_UNABLE_TO_COMPLY` (5012, E-bit set), then close the connection.
- Otherwise: close the connection silently.

Log the exception with full stack trace in both sub-cases.

Rationale: `DIAMETER_UNABLE_TO_COMPLY` is defined in RFC 6733 §7.1.5 as "rejected for unspecified reasons" — the correct code for an internal failure with no more specific cause. Closing the connection is mandatory because a Java exception leaves the decode state machine in an unknown position; continuing to read from the same stream would risk misframing subsequent messages.

## Consequences

- Decode of an unknown command never terminates the connection; the application layer owns the decision.
- Framing-level violations (Case 2) are surfaced to the peer where possible and always terminate the connection.
- AVP-level permanent failures (Case 3) are surfaced to the peer with a `Failed-AVP`; the connection remains open.
- Java exceptions (Case 4) produce a best-effort error reply and always terminate the connection.
- The transport close in Cases 2 and 4 is unconditional — there is no retry or recovery at this layer.

## Related ADRs

- **See also:** ADR-0006 (per-package factory dispatch — the mechanism that triggers Case 1)
