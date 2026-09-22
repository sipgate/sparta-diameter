---
title: "ADR-0014: SCTP Transport"
description: "Plain SCTP as a second wire transport on the shared DiameterNode via Netty NioSctpChannel, no separate node class"
owner: "insomnick"
status: accepted
tags:
  - transport
  - sctp
  - rfc6733
created: "2026-09-22"
---

## Context

RFC 6733 §2.1: Diameter clients MUST support TCP or SCTP; relay agents and servers SHOULD support
both, with plain SCTP on port 3868. The Diameter Routing Agent that replaces the Yate DRA serves
the Telefonica DEA links as plain SCTP on 3868 (Yate `[listener sctp_*:3868]`) while the internal
UCN/HSS/SMSC side stays on TCP — so the library's transport layer, previously TCP-only
(`LengthFieldBasedFrameDecoder` on a byte stream), needs a second wire transport.

`specs/dtls-sctp/01-requirements.md` defines the full SCTP scope including DTLS (port 5658, PPID 47,
mutual authentication). This ADR covers the plain-SCTP subset that is actually implemented
(commit `ccbf444`, branch `feature/relay-sctp`); DTLS/SCTP remains an open spec.

## Decision

### `Transport` enum on `DiameterNode`, not a separate node class

`DiameterNode` gains a `Transport { TCP, SCTP }` selector with `listen(...)`/`connect(...)`
overloads (including explicit multi-homing address lists). Rejected alternative — a separate
`SctpDiameterNode` class: it would duplicate the node's construction, meter registry, connect
listener and `close()` wiring for a difference that is entirely inside the channel pipeline.
Rejected alternative — an abstract transport hierarchy: both transports are NIO channel types on
the same `Selector`; a class split buys nothing.

### Same event loop group for both transports

SCTP channels (`NioSctpChannel`/`NioSctpServerChannel`) are NIO handles registered on the same
`java.nio.channels.Selector` as TCP channels (`NioIoHandler.isCompatible` accepts any NIO handle),
so both transports share one `MultiThreadIoEventLoopGroup` with `NioIoHandler`. No separate
event loop group is required.

### Pipeline identical to TCP except framing

SCTP preserves message boundaries, so there is no `LengthFieldBasedFrameDecoder`. The SCTP
pipeline is otherwise a mirror of the TCP one:

- Inbound: `SctpMessageCompletionHandler` reassembles fragmented SCTP messages,
  then `SctpMessageDecoder` unwraps the payload into a `ByteBuf` for the shared
  `DiameterMessageDecoder`. Only PPID 46 (DIAMETER, cleartext, per RFC 6733 §2.1.1) is processed;
  PPID 47 (DTLS) is dropped with a warning. The stream identifier and unordered flag are logged at
  debug level.
- Outbound: `SctpRequestEncoder`/`SctpAnswerEncoder` wrap each message into a single `SctpMessage`
  on **stream 0 with the unordered delivery flag set** and PPID 46 — the mapping RFC 6733 §2.1.1
  recommends to avoid head-of-line blocking. Hop-by-hop/end-to-end identifiers are stamped at
  encode time exactly like `OutgoingRequestEncoder` does for TCP; the `ioBuffer` is released when
  `writeTo` throws.

Session handling (`DiameterPeerHandler`), decode error policy, and meters are shared unchanged.

### Multi-homing via `bindAddress` (the `sctp_bindx` equivalent)

A server binds its primary address with `ServerBootstrap.bind(...)` and adds every further address
with `NioSctpServerChannel.bindAddress(InetAddress)` once the server channel is active. A client
binds its primary address via `Bootstrap.localAddress(...)` (honoured by
`NioSctpChannel.doConnect`) and adds further addresses with `NioSctpChannel.bindAddress(InetAddress)`
after the association is established. The first list entry is always the primary address — the same
ordering Yate's `addr` + `sctp_bindx` fields have.

### Plain SCTP only — DTLS explicitly out

No DTLS handshake, no port 5658, no PPID 47 sending, and no implementation of the §2.1.1 responder
refinement (delaying unordered delivery until the initiator has sent its first message). The
decoder-side PPID-47 drop keeps a later DTLS/SCTP implementation additive. `SCTP_NODELAY` is not
set (obsolete for SCTP).

### TCP path byte-identical (backwards compatibility)

The default `listen`/`connect` overloads behave exactly as before; all existing callers
(sparta-hss' 3-arg `connect`, plain `listen`) are untouched.

## Consequences

- A live node can serve TCP and SCTP **on the same port number** (3868) — TCP and SCTP bind in
  independent protocol namespaces, so there is no collision. This is Yate's production shape
  (`tcp_*:3868` + `sctp_*:3868`).
- macOS has no SCTP kernel support (and the development sandbox blocks JVM sockets entirely):
  encoder/decoder behavior is covered by offline unit tests (`SctpMessageDecoderTest`,
  `SctpRequestEncoderTest`, `SctpAnswerEncoderTest`, `DiameterNodeSctpValidationTest` — framing,
  PPID filter, stream/order/PPID fields, address validation). Real SCTP wire behavior cannot be
  exercised outside Linux.

> **Guardrail:** Before any production activation of the SCTP path, a two-node Linux wire test is
> mandatory (two nodes, multi-homing, CER/CEA, DWR, relay roundtrip) — the e2e docker-compose environment exists
> exactly for this; SCTP is not verified by the offline test suite.

- Sending every message unordered on stream 0 means message reordering inside one association is
  possible; all request/answer correlation in the session layer is identifier-based (hop-by-hop),
  never order-based, so this is safe.
- DTLS/SCTP (specs/dtls-sctp remainder) stays a separate future feature on the same pipeline seams.

## Related ADRs

- **See also:** ADR-0002 (the full feature — including DTLS/SCTP — was specced in
  `specs/dtls-sctp/01-requirements.md`; DTLS/SCTP remains planned there, the plain-SCTP subset is
  the implemented design documented in this ADR).
