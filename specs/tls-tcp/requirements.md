# TLS/TCP — Requirements

## Context

RFC 6733 §13 and §2.2 require all Diameter base protocol implementations to support TLS over TCP. The Diameter protocol MUST NOT be used without one of TLS, DTLS, or IPsec. The current transport layer (`DiameterNode`, `DiameterPeer`) uses plain TCP via Netty without TLS. This spec covers TLS/TCP only; DTLS/SCTP is a separate spec.

## Port assignment (§2.1)

- Plain TCP connections use port 3868.
- TLS/TCP connections MUST use port 5658.
- A node MUST always be prepared to receive connections on port 3868 (plain) and port 5658 (TLS).
- If a peer does not accept connections on port 5658, the initiating node MAY fall back to plain TCP on port 3868 for backwards compatibility, but this is inherently insecure (CER/CEA sent unprotected).

## Connection setup timing (§13)

- If a connection is to be protected via TLS/TCP, the TLS handshake SHOULD begin prior to any Diameter message exchange (i.e., before CER is sent).
- Alternatively, TLS may be negotiated in the open state: in this case the CER/CEA exchange MUST include an `Inband-Security-Id AVP` with value `TLS (1)`. The TLS handshake begins after both peers successfully reach the open state. If the handshake fails, both ends MUST move to the closed state.
- Note: bootstrapping TLS via `Inband-Security-Id` after CER/CEA is deprecated in RFC 6733. Prefer pre-CER TLS (port 5658) for new implementations.

## Mutual authentication (§13.1)

- Diameter nodes using TLS/TCP MUST mutually authenticate as part of TLS session establishment.
- The TLS server MUST request a certificate from the TLS client.
- The TLS client MUST be prepared to supply a certificate on request.

## Cipher suites (§13.1)

- Nodes MUST be able to negotiate:
  - `TLS_RSA_WITH_RC4_128_MD5`
  - `TLS_RSA_WITH_RC4_128_SHA`
  - `TLS_RSA_WITH_3DES_EDE_CBC_SHA`
- Nodes SHOULD be able to negotiate:
  - `TLS_RSA_WITH_AES_128_CBC_SHA`
- Nodes MAY negotiate additional cipher suites.

## Certificate expiry and peer table (§13.1, §2.6)

- If public key certificates are used, the expiration time stored in the peer table MUST NOT exceed the expiry time of the relevant certificate.

## Acceptance criteria

- `DiameterNode` accepts and initiates TLS/TCP connections on port 5658 in addition to plain TCP on port 3868.
- TLS is backed by Netty's `SslHandler` (or equivalent); the application supplies an `SslContext`.
- Mutual authentication is enforced: the server requests a client certificate; connections without a valid client certificate are rejected.
- The required cipher suites are negotiable.
- If the TLS handshake fails, the connection is closed and the session moves to the closed state.
- The library does not enforce post-TLS negotiation via `Inband-Security-Id`; that path is deprecated and out of scope.
