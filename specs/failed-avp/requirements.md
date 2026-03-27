# Failed-AVP and Error Code Semantics — Requirements

## Context

RFC 6733 §7 defines how a Diameter node must respond to protocol violations detected during message processing. Many error conditions require the answer to include a `Failed-AVP` (code 279, type Grouped) containing the offending AVP, and specific `Result-Code` values that differ by error type. This is distinct from the E-bit exposure in `specs/command-flags/`: that spec covers *recognising* an error answer; this spec covers *constructing* one correctly.

## Result-Code categories (§7)

- **Protocol errors (3xxx):** MUST only appear in answers with the E-bit set (error answers). The answer does not conform to the normal CCF for that command.
- **Transient failures (4xxx):** MUST NOT use the E-bit.
- **Permanent failures (5xxx):** SHOULD NOT use the E-bit; MAY use it if more efficient processing results.

## When to include Failed-AVP (§7.5)

Failed-AVP (code 279, type Grouped) provides debugging information about the specific AVP that triggered an error.

| Result-Code | Failed-AVP requirement |
|---|---|
| `DIAMETER_AVP_UNSUPPORTED (5001)` — M-bit set on unrecognised AVP | MUST include the offending AVP |
| `DIAMETER_INVALID_AVP_VALUE (5004)` — AVP has invalid value | MUST include the offending AVP |
| `DIAMETER_MISSING_AVP (5005)` — required AVP absent | SHOULD include an example of the missing AVP with zero-filled payload |
| `DIAMETER_CONTRADICTING_AVPS (5007)` | MUST include the contradicting AVPs |
| `DIAMETER_AVP_NOT_ALLOWED (5008)` — AVP must not be present | MUST include the AVP |
| `DIAMETER_AVP_OCCURS_TOO_MANY_TIMES (5009)` | MUST include the first offending instance |
| `DIAMETER_INVALID_AVP_LENGTH (5014)` — invalid AVP length field | MUST include the offending AVP |
| `DIAMETER_INVALID_AVP_BIT_COMBO (5016)` — AVP flags combination not allowed | MUST include the offending AVP |

For nested grouped AVPs, the Failed-AVP MAY contain the full grouped AVP hierarchy down to the single offending AVP.

## M-bit enforcement (§1.3.4, §4.1)

- If a received AVP has the M-bit set and the receiving node does not recognise the AVP code, the node MUST return `DIAMETER_AVP_UNSUPPORTED (5001)` with a Failed-AVP containing the unrecognised AVP.
- If a received AVP has the M-bit set and the receiving node does not support the AVP's defined values, the node MUST return `DIAMETER_INVALID_AVP_VALUE (5004)` with a Failed-AVP containing the offending AVP.

## Error-Reporting-Host (§7.4)

- The `Error-Reporting-Host` AVP (code 294, type DiameterIdentity) MUST be set in any error answer where the host that is setting the Result-Code is not the same as the Origin-Host of the answer.
- This is relevant on the relay/proxy path: if an intermediate agent is constructing the error answer on behalf of the final destination, it MUST include Error-Reporting-Host identifying itself.

## Error-Message (§7.3)

- The `Error-Message` AVP (code 281, type UTF8String) MAY accompany a Result-Code for human-readable description.
- Its content MUST NOT be parsed automatically; it is for diagnostic purposes only.

## Single-error rule (§7)

- When multiple errors are present in a single message, the node MUST report only the first error encountered.

## Acceptance criteria

- `DiameterMessageFactory.createAnswer` (or an equivalent error-answer builder) accepts a `Result-Code` and an optional list of `Failed-AVP` payloads.
- When processing an inbound request that contains an unrecognised AVP with M-bit set, the session layer returns an answer with `Result-Code = DIAMETER_AVP_UNSUPPORTED` and a `Failed-AVP` containing the unrecognised AVP.
- When a required AVP is absent from an inbound request, the handler can produce an answer with `Result-Code = DIAMETER_MISSING_AVP` and a `Failed-AVP` containing a zero-length example.
- `Error-Reporting-Host` is included automatically when the node constructing the error answer differs from the Origin-Host of the answer message.
- Only the first detected error is reported per message; subsequent violations are ignored.
