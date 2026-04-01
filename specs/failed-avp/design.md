# Failed-AVP and Error Code Semantics — Design

## Scope

This document covers the three parse-time AVP violations the library can detect and handle generically, without application knowledge:

| Result-Code | Violation |
|---|---|
| `DIAMETER_AVP_UNSUPPORTED (5001)` | Unrecognized AVP with M-bit set |
| `DIAMETER_INVALID_AVP_LENGTH (5014)` | Malformed AVP length field |
| `DIAMETER_INVALID_AVP_BIT_COMBO (5016)` | Invalid AVP flag combination |

Semantic violations (`5004`, `5005`, `5007`, `5008`, `5009`) remain the application's responsibility — they require command- or business-level knowledge the library does not have.

## Generic AVPs

`AVP.readFrom()` already returns a raw `AVP` for unrecognized codes; the flags and data are preserved. This is the "generic AVP" path. No change is needed for that case.

The missing branch is:

```
definition == null && mandatory == true  →  throw (5001)
definition == null && mandatory == false →  return raw AVP (already works)
```

## Detection layer — `AVP.readFrom()`

All three violations are detected inside `AVP.readFrom()`, where the binary data is in scope:

- **5001** — after the registry lookup (current line 847): `definition == null && mandatory`
- **5014** — at the existing length sanity check (current line 830): replace the generic `EOFException` with the typed exception
- **5016** — after reading the flags byte (current line 826): check that reserved bits (bits 4, 3, 2, 1, 0) are zero

When a violation is detected, `readFrom()` throws `AVPParseException` — a new checked exception carrying:
- the `ResultCode` (which of the three codes applies)
- the offending `AVP` (fully constructed at the point of each throw)

For `5014`, when the length field is so broken that the AVP header itself cannot be fully decoded, RFC 6733 §7.1.5 allows a zero-padded stub as the `Failed-AVP` payload — just the header bytes that could be read, padded to the minimum AVP header length with zeroes.

## Exception hierarchy

```
DiameterException
└── DiameterResultCodeException(ResultCode)       ← version (5011), message length (5015)
    └── AVPParseException(ResultCode, AVP)         ← 5001, 5014, 5016
```

The three in-scope generic `DiameterException` throws in `Command` are handled as follows:

| Location | Result-Code | Exception type | Action |
|---|---|---|---|
| `getMessageLength()` line 237 | — | `DiameterException` | RESET transport, log |
| `parseMessage()` line 256 | — | `DiameterException` | RESET transport, log |
| `parseMessage()` line 267 | `5011` | `DiameterResultCodeException` | send reply, close transport |

`DiameterResultCodeException` is only thrown when there is enough context to send a valid reply. When a reply is not possible, a plain `DiameterException` is thrown.

**Lines 237 and 256 (5015):** The buffer does not contain a complete, parseable header. Per RFC 6733 §2.1, data that "cannot be parsed" means the stream is compromised and cannot be recovered — the transport connection MUST be closed with a TCP RESET or SCTP ABORT. No reply is sent.

**Line 267 (5011):** The peer sent a recognizable Diameter-shaped message with an unsupported version — this is a peer error, not stream corruption. RFC 6733 §7.1.5 states the error "is returned", implying a reply is expected. By the time the version check fires, `parseMessage(buffer, messageLength)` has already confirmed the buffer holds at least `messageLength` bytes, so the remaining header is readable. The throw site reads bytes 4–19 before throwing, so `DiameterResultCodeException` for `5011` carries commandCode, applicationId, hopByHop, and endToEnd. The session layer sends the reply (no `Failed-AVP` — no AVP was involved) then closes the transport gracefully: the peer is on an incompatible version and continued communication is pointless.

## Escalation layer — `Command.parseAVPs()`

`parseAVPs()` calls `readFrom()` in a loop. It catches `AVPParseException` on the first occurrence and re-throws it. This naturally implements the single-error rule from RFC 6733 §7 — no special logic required.

## Error answer construction — session layer

The exception propagates from `parseAVPs()` through `parseMessage()` up to the session layer, consistent with ADR-0009. The session layer uses multiple `catch()` blocks — most specific first — so no `instanceof` checks are needed:

```java
catch (AVPParseException e) {
    // result code + offending AVP → build error answer with Failed-AVP
}
catch (DiameterResultCodeException e) {
    // result code only → build error answer without Failed-AVP
}
catch (DiameterException e) {
    // no parseable context → drop/log, no answer possible
}
```

### Required AVPs in the error answer (RFC 6733 §7.2)

For E-bit answers the CCF mandates:

| AVP | Requirement | Source |
|---|---|---|
| `Origin-Host` | MUST | local node identity |
| `Origin-Realm` | MUST | local node identity |
| `Result-Code` | MUST | from exception |
| `Session-Id` | optional (0 or 1) | echo from request if present |
| `Failed-AVP` | MUST for 5001, 5014, 5016 | from `AVPParseException` |
| `Error-Reporting-Host` | MUST if origin differs | local node identity vs. Origin-Host |
| `Proxy-Info` | copy all from request | from parsed request AVPs |

`Origin-Host` and `Origin-Realm` come from the library's own node identity, which the session layer already holds. For `5015` (stream corrupt), no reply is sent and the connection is RESET. For `5011`, the reply is sent but there is no `Failed-AVP` and no `Session-Id` (neither was decoded).

## Connection and reconnect behaviour

After handling a parse error, the session layer acts as follows:

| Error | Close connection? | How | Suppress reconnect? | Rationale |
|---|---|---|---|---|
| `5015` — corrupt stream | Yes (mandatory) | TCP RESET / SCTP ABORT | No | Transient condition; Tc timer reconnect is appropriate |
| `5011` — unsupported version | Yes | Graceful close (no DPR) | Yes | Reconnecting would hit the same version mismatch |
| `5001`, `5014`, `5016` — AVP error | No | — | — | Connection remains valid; peer may send correct requests |

This requires a richer close API on `DiameterSession`. The current `stop()` (sends DPR, suppresses reconnect) is split into three public methods, all requiring Javadoc:

| Method | DPR | Reconnect | `Disconnect-Cause` | Use case |
|---|---|---|---|---|
| `stop()` | No | No | — | Protocol errors (5011); closes immediately without negotiation |
| `stopGracefully()` | Yes | No | `DO_NOT_WANT_TO_TALK_TO_YOU` | Operator-initiated shutdown; current `stop()` behaviour |
| `closeGracefully()` | Yes | Yes | `REBOOTING` | Node restart or upgrade; reconnects after DPR/DPA |

`stop()` and `stopGracefully()` both set `shuttingDown = true` and cancel the Tc timer (initiator side). `closeGracefully()` does not set `shuttingDown`, so `onDisconnected()` schedules the Tc reconnect as normal.

For `5015` (corrupt stream, TCP RESET): no new method is needed. The RESET is issued at the transport level and `shuttingDown` remains `false`, so the Tc reconnect fires automatically on the initiator side.

## What does NOT change

- Application-level error handling (`5004`, `5005`, `5007`, `5008`, `5009`) remains the caller's concern; the existing `ErrorAnswer.Out` + mixin API is sufficient for those
- `Error-Message` is optional and always left to the application
- The `ErrorAnswer.Out` class and the mixin interfaces (`HasFailedAVP`, `HasErrorReportingHostAVP`, `HasErrorMessageAVP`) require no structural changes
