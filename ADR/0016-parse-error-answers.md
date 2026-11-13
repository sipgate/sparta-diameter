---
title: "ADR-0016: Parse-Error Answers"
description: "E-bit only on 3xxx parse-error answers; bad answers dropped; relays keep unknown AVPs of proxiable messages"
owner: "insomnick"
status: draft
tags:
  - error-handling
  - decode
  - relay
  - rfc6733
created: "2026-11-13"
---

## Context

ADR-0007 Cases 2 and 3 send every answer to an unparseable request as an error answer with the
E-bit set, including the permanent failures `DIAMETER_UNSUPPORTED_VERSION` (5011),
`DIAMETER_INVALID_MESSAGE_LENGTH` (5015), `DIAMETER_AVP_UNSUPPORTED` (5001),
`DIAMETER_INVALID_AVP_LENGTH` (5014) and `DIAMETER_INVALID_AVP_BIT_COMBO` (5016). RFC 6733 §7.1
reserves the E-bit for protocol errors (3xxx); permanent failures are carried in answers with the
E-bit not set (§7.1.5). Peers read an E-bit 5xxx answer as a protocol error.

ADR-0007 also does not say what happens to an *answer* that fails AVP-level parsing, and it applies
the 5001 rule to every node. A relay agent (ADR-0015) forwards messages it does not interpret; RFC
6733 §2.8.1 and §6.1 do not require relays to understand non-routing AVPs, so rejecting an unknown
M-bit AVP with 5001 breaks relaying of applications whose dictionary the relay does not load.

Finally, the Diameter Routing Agent must reproduce the wire form of the peer it replaces, which may
differ from the RFC form, without the library shipping non-RFC behaviour.

## Decision

This ADR supersedes the answer format of ADR-0007 Cases 2 and 3 and adds the answer and relay
rules below. ADR-0007 Cases 1 and 4 and the connection handling of Cases 2 and 3 (close after a
framing error, keep open after an AVP-level error) are unchanged.

### E-bit only for protocol errors

The answer to a request that failed to parse is built by
`DiameterMessageFactory.createParseErrorAnswer(DiameterResultCodeException)`:

- **3xxx** (e.g. `DIAMETER_INVALID_HDR_BITS`, 3008): an E-bit error answer (`ErrorAnswer.Out`).
- **5xxx** (5001, 5011, 5014, 5015, 5016): a regular answer **without** the E-bit. The request was
  not decoded into its typed form, so the typed command-specific answer cannot be built; the stack
  sends a `GenericAnswer.Out` with the request's header fields (command code, P-bit, application
  id, hop-by-hop and end-to-end identifiers), Session-Id, Auth-Session-State and Proxy-Info when
  they were parsed before the failure, and the Result-Code.
- An AVP-level failure (`AVPParseException`) adds a `Failed-AVP` wrapping the offending AVP
  (RFC 6733 §7.5), as before.

### Unparseable answers are never answered

An inbound *answer* (R-bit clear) that fails to parse is dropped with a WARN log; nothing is sent
(RFC 6733 §7: an answer is never answered). After an AVP-level failure the connection stays open;
after a framing-level failure it is closed, as in ADR-0007 Case 2. The pending request the answer
belonged to runs into its timeout.

### Relay nodes keep unknown AVPs of proxiable messages opaque

A node constructed as a relay agent (`new DiameterNode(meterRegistry, true)`) does not apply the
5001 rule to proxiable (P-bit) messages: an unrecognized M-bit AVP is decoded and kept opaque so it
can be forwarded (RFC 6733 §2.8.1, §6.1). Non-proxiable messages (CER/CEA, DWR/DWA, DPR/DPA) are
consumed locally and stay strict. Endpoints (`new DiameterNode()`, `new DiameterNode(meterRegistry)`)
keep strict parsing. Independently of the node type, a command no factory knows (ADR-0007 Case 1,
`GenericRequest` / `GenericAnswer.In`) is never interpreted locally, so its AVPs are kept opaque as
well.

### Consumer hook for non-RFC wire forms

`DiameterSession.onParseError` builds every parse-error answer through the protected
`parseErrorAnswer(DiameterResultCodeException)` hook, whose default returns
`createParseErrorAnswer`. A consumer that must mimic a specific peer overrides it in its
`DiameterResponderSession` / `DiameterInitiatorSession` subclass; the connection handling is not
affected by the override. The library ships only the RFC form — there is no configuration toggle.

Rejected alternative — keeping the E-bit form for 5xxx (allowed by RFC 6733 §7.1.5 when a
command-specific answer is "not possible or efficient"): peers treat E-bit answers as protocol
errors, and a generic answer with the request's header fields is cheap to build.

Rejected alternative — a per-node configuration flag for the E-bit behaviour: it puts non-RFC
behaviour into the library; the overridable hook keeps it in the consumer (ADR-0015).

## Consequences

- 5xxx parse-error answers no longer carry the E-bit; a peer sees a regular answer with
  Result-Code and, for AVP-level failures, `Failed-AVP`.
- A garbled answer is never answered; the caller of the original request sees a timeout instead.
- A relay forwards proxiable messages with AVPs it has no dictionary for; an endpoint still
  rejects them with 5001.
- Peer-specific deviations are code in the consumer, not configuration in the library.
- ADR-0007 Case 4 (`DIAMETER_UNABLE_TO_COMPLY`, 5012, with E-bit) is not implemented and not
  changed here; its E-bit is inconsistent with this rule and should be revisited when Case 4 is
  implemented.

## Related ADRs

- **Supersedes:** ADR-0007 (in part: answer format of Cases 2 and 3)
- **See also:** ADR-0009 (error-answer signalling to the application), ADR-0015 (relay
  architecture and the consumer-hook approach)
