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

## Escalation layer — `Command.parseAVPs()`

`parseAVPs()` calls `readFrom()` in a loop. It catches `AVPParseException` on the first occurrence and re-throws it. This naturally implements the single-error rule from RFC 6733 §7 — no special logic required.

## Error answer construction — session layer

The exception propagates from `parseAVPs()` through `parseMessage()` up to the session layer, consistent with ADR-0009. By the time the session layer handles it, the Diameter header fields (commandCode, applicationId, hopByHop, endToEnd) are available to construct the response. The session layer:

1. Builds an `ErrorAnswer.Out` with the appropriate `Result-Code`
2. Wraps the offending `AVP` in a `GroupedAVP` and sets it as `Failed-AVP`
3. Sets `Error-Reporting-Host` if the node constructing the answer differs from its own Origin-Host

## What does NOT change

- Application-level error handling (`5004`, `5005`, `5007`, `5008`, `5009`) remains the caller's concern; the existing `ErrorAnswer.Out` + mixin API is sufficient for those
- `Error-Message` is optional and always left to the application
- The `ErrorAnswer.Out` class and the mixin interfaces (`HasFailedAVP`, `HasErrorReportingHostAVP`, `HasErrorMessageAVP`) require no structural changes
