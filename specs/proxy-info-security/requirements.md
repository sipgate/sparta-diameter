# Proxy-Info Security — Requirements

## Context

RFC 6733 §6.1.9 and §6.7.2 define the Proxy-Info AVP (code 284, type Grouped) and its security requirements. A relay or proxy agent MAY include Proxy-Info in a forwarded request to carry local state needed when the corresponding answer returns. Because this state is distributed to other entities in the network, the RFC RECOMMENDS cryptographic protection.

The relay path (see `specs/relay-support/`) handles forwarding; this spec covers only the security and answer-path lifecycle of Proxy-Info.

## Proxy-Info structure (§6.7.2)

```
Proxy-Info ::= < AVP Header: 284 >
               { Proxy-Host }
               { Proxy-State }
              *[ AVP ]
```

- `Proxy-Host` (code 280, DiameterIdentity): identity of the node that added this AVP.
- `Proxy-State` (code 33, OctetString): opaque local state. This is the field that must be protected.

## Cryptographic protection of Proxy-State (§6.1.9)

- The content of the Proxy-Info AVP SHOULD be protected with a cryptographic mechanism, for example a keyed message digest such as HMAC-SHA1 (RFC 2104).
- Key requirements:
  - Keys MUST be generated securely following the randomness recommendations in RFC 4086.
  - Keys and cryptographic algorithms SHOULD be at least 128 bits in strength.
  - Keys MUST NOT be used for any purpose other than generating and verifying Proxy-Info AVP instances.
  - Keys SHOULD be rotated regularly.
  - Keys SHOULD be changed if the AVP format or cryptographic algorithm changes.
- Key management is local to the Diameter node; no key exchange with peers is required or defined.

## Answer-path lifecycle (§6.2, §6.2.2)

- Any Proxy-Info AVPs present in a request MUST be copied into the corresponding answer message in the same order they appeared in the request (§6.2).
- On the answer path, if the last Proxy-Info AVP in the message targets the local node (i.e., its `Proxy-Host` matches the local node's identity), that AVP MUST be removed before the answer is forwarded upstream (§6.2.2).
- If the answer carries a `Result-Code` indicating failure, the relay or proxy MUST NOT modify the `Result-Code` AVP contents. Local errors SHOULD be logged but not reflected in `Result-Code`.

## Acceptance criteria

- The relay path copies all Proxy-Info AVPs from an inbound request into the outbound answer in their original order.
- When forwarding an answer, the relay path removes the last Proxy-Info AVP if its `Proxy-Host` value matches the local node's Origin-Host.
- The library provides a `ProxyStateProtector` interface (or equivalent) with sign/verify operations so applications can plug in their chosen HMAC implementation; the library does not bundle a default key.
- Verification failure of a received Proxy-State (tampered or key-mismatch) results in the message being rejected and logged, not silently passed through.
