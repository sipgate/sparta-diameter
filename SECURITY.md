# Security Policy

## Supported versions

Sparta Diameter is pre-1.0 and in active development. Only the latest release on Maven Central
receives fixes; there are no maintained backport branches.

| Version | Supported |
|---|---|
| latest `0.1.x` release | yes |
| anything older | no |

## Reporting a vulnerability

Please do **not** open a public issue for a security problem.

Report it through GitHub's private vulnerability reporting: go to the
[Security tab](https://github.com/sipgate/sparta-diameter/security/advisories/new) and choose
"Report a vulnerability". This creates a private advisory visible only to you and the maintainers.

Useful details, where you have them:

- affected module and version
- a packet capture (PCAP) of the triggering exchange, or failing that a hex dump of the message
- whether it is reachable from an unauthenticated peer
- what an attacker gains — crash, hang, memory exhaustion, spoofed identity, information disclosure

We will acknowledge the report and let you know whether we consider it in scope. Once a fix is
released we will credit you in the advisory unless you prefer otherwise.

## Scope notes

This is a protocol library, not a deployed service. A few things are the responsibility of the
application embedding it and are not vulnerabilities in the library:

- **Transport security.** TLS/TCP and DTLS/SCTP are not implemented yet (see
  [`specs/tls-tcp`](specs/tls-tcp/01-requirements.md) and
  [`specs/dtls-sctp`](specs/dtls-sctp/01-requirements.md)). RFC 6733 §2.2 requires Diameter to run
  over TLS, DTLS or IPsec — until those specs land, protect the transport at the network layer.
- **Metric cardinality.** A hostile peer can vary `application_id` and `command_code` to inflate
  label sets. Filter with Micrometer's `MeterFilter`; see
  [`docs/metrics.md`](docs/metrics.md) and [ADR-0010](ADR/0010-metrics-api.md).
- **Peer authorization.** The library negotiates capabilities but does not decide which peers may
  connect or what they may ask for.

Parser crashes, hangs, memory exhaustion on malformed input (for example a length field that
makes the decoder allocate attacker-controlled amounts of memory), and incorrect identifier or
identity handling **are** in scope.
