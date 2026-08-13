# Feature Specifications

Each directory holds the specification for one feature, structured per
[ADR-0002](../ADR/0002-spec-driven-development.md): `01-requirements.md` (behaviour and acceptance
criteria), `02-design.md` (internals), `03-tasks.md` (implementation steps). Not every feature
needs all three.

Everything here describes work that is **planned but not implemented** — these are requirements,
not documentation of existing behaviour. For what the library does today, see the
[Development Status](../README.md#development-status) section of the main README. Architectural
decisions live in [`ADR/`](../ADR), not here.

| Feature | Scope |
|---|---|
| [`decode-encode-error-handling`](decode-encode-error-handling/01-requirements.md) | Remaining Case 4 of [ADR-0007](../ADR/0007-decode-encode-error-handling.md): unexpected Java exceptions (Cases 1–3 are done) |
| [`disconnect-reason`](disconnect-reason/01-requirements.md) | Callback delivering why a session closed |
| [`dtls-sctp`](dtls-sctp/01-requirements.md) | DTLS over SCTP transport (RFC 6733 §13, RFC 6083) |
| [`election`](election/01-requirements.md) | Connection election on simultaneous connect (RFC 6733 §5.6.4) |
| [`loop-detection`](loop-detection/01-requirements.md) | Route-Record loop detection and avoidance (RFC 6733 §6.1.3, §6.1.7) |
| [`origin-state-id`](origin-state-id/01-requirements.md) | Origin-State-Id AVP generation and interpretation (RFC 6733 §8.16, §8.6) |
| [`proxy-info-security`](proxy-info-security/01-requirements.md) | Proxy-Info AVP security and answer-path lifecycle (RFC 6733 §6.1.9, §6.7.2) |
| [`relay-support`](relay-support/01-requirements.md) | Relay-agent forwarding in the session layer (RFC 6733 §6.1.9) |
| [`retransmit`](retransmit/01-requirements.md) | T-flag and end-to-end identifier preservation on resend (RFC 6733 §1.7) |
| [`tls-tcp`](tls-tcp/01-requirements.md) | TLS over TCP transport (RFC 6733 §13, §2.2) |
