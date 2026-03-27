# DTLS/SCTP — Requirements

## Context

RFC 6733 §13 and §2.2 require all Diameter base protocol implementations to support DTLS over SCTP (RFC 6083). The current transport layer uses TCP only. This spec covers DTLS/SCTP; TLS/TCP is a separate spec.

## Transport support (§2.1)

- Diameter clients MUST support either TCP or SCTP; agents and servers SHOULD support both.
- Plain SCTP connections use port 3868.
- DTLS/SCTP connections MUST use port 5658.
- A node MUST always be prepared to receive connections on port 3868 (plain) and port 5658 (DTLS).

## SCTP stream mapping to avoid head-of-line blocking (§2.1.1)

- Diameter messages SHOULD be mapped to SCTP streams in a way that avoids head-of-line blocking.
- The RECOMMENDED approach is to send every message on stream zero with the unordered delivery flag set.
- Implementations MAY use other approaches (e.g., multiple streams with ordered delivery) provided the receiving side is ready to accept messages on any stream.
- During connection establishment and teardown, the responder SHOULD NOT use out-of-order delivery until it has received the first message from the initiator (proving the initiator is in I-Open state). A practical way to achieve this is to wait for DWA before enabling unordered delivery.

## SCTP payload protocol identifiers (§2.1.1)

- Agents SHOULD use PPID 46 for cleartext Diameter over SCTP.
- Agents SHOULD use PPID 47 for DTLS/SCTP Diameter.

## DTLS handshake timing (§13)

- If a connection is to be protected via DTLS/SCTP, the DTLS handshake SHOULD begin prior to any Diameter message exchange (i.e., before CER is sent).
- In-open-state DTLS negotiation via `Inband-Security-Id` is deprecated in RFC 6733; prefer pre-CER DTLS on port 5658.

## Mutual authentication (§13.1)

- Diameter nodes using DTLS/SCTP MUST mutually authenticate during DTLS session establishment.
- The DTLS server MUST request a certificate from the DTLS client.
- The DTLS client MUST be prepared to supply a certificate on request.

## Cipher suites (§13.1)

Same requirements as TLS/TCP (see `specs/tls-tcp/`): MUST negotiate the three RSA/RC4 and RSA/3DES suites; SHOULD negotiate AES-128-CBC.

## SCTP and DPR race condition (§2.1.1)

- DPR/DPA messages may be delivered faster than queued application messages on SCTP. Implementations SHOULD mitigate this race condition, for example by waiting for pending messages to drain before closing the SCTP association.

## Acceptance criteria

- `DiameterNode` accepts and initiates SCTP connections on port 3868 and DTLS/SCTP connections on port 5658.
- Messages are sent over SCTP with the unordered flag set on stream zero by default.
- PPID 46 (cleartext) and 47 (DTLS) are set on outbound SCTP packets.
- Mutual DTLS authentication is enforced; connections without a valid client certificate are rejected.
- The DTLS handshake failure moves the connection to the closed state.
- The DPR/DPA drain window is respected before the SCTP association is aborted.
