---
title: "ADR-0014: SCTP Transport"
description: "Plain SCTP as a second wire transport on the shared DiameterNode via Netty NioSctpChannel, no separate node class"
owner: "insomnick"
status: draft
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
  `DiameterMessageDecoder`. Only PPID 46 (DIAMETER, cleartext, per RFC 6733 §2.1.1) and PPID 0
  (unspecified — sent by many peers instead of 46) are processed; PPID 47 (DTLS) and anything else
  is dropped with a warning. The stream identifier and unordered flag are logged at
  debug level.
- Outbound: `SctpRequestEncoder`/`SctpAnswerEncoder` wrap each message into a single `SctpMessage`
  on **stream 0 with ordered delivery** and PPID 46 (see *Ordered delivery on stream 0* below).
  Hop-by-hop/end-to-end identifiers are stamped at encode time exactly like
  `OutgoingRequestEncoder` does for TCP; the `ioBuffer` is released when `writeTo` throws.

Session handling (`DiameterPeerHandler`), decode error policy, and meters are shared unchanged.

### Ordered delivery on stream 0

RFC 6733 §2.1.1 recommends stream 0 with the unordered flag to avoid head-of-line blocking, but
only with the caveat that the responder must not send unordered until the initiator is in I-Open,
and it warns that a DPR/DPA can overtake queued application messages. The base-protocol state
machines depend on order:

- A responder sends CEA and may immediately relay a request to the new peer. Sent unordered, that
  request can arrive before the CEA while the initiator is still in WAIT_I_CEA, where
  `DiameterInitiatorSession.onMessage` drops it silently.
- A DPA (or DPR) can overtake answers still in flight on the same association, so the peer tears
  the connection down before it has seen them.

Marking only the base-protocol messages ordered does not help: SCTP may deliver an unordered
message before an earlier-sent ordered one, so an unordered request can still overtake a CEA.
Therefore every message is sent **ordered on stream 0** — the same sequencing guarantee TCP gives.
Rejected alternative — a multi-stream scheme (base protocol on stream 0, applications spread over
further streams, all ordered): it reduces head-of-line blocking but needs a stream-selection
policy and cross-stream ordering rules for CEA/DPA versus application traffic; not worth it
without a measured need.

### Multi-homing via `bindAddress` (the `sctp_bindx` equivalent)

A server binds its primary address with `ServerBootstrap.bind(...)` and adds every further address
with `NioSctpServerChannel.bindAddress(InetAddress)` once the server channel is active. A client
binds its primary address via `Bootstrap.localAddress(...)` (honoured by
`NioSctpChannel.doConnect`) and adds further addresses with `NioSctpChannel.bindAddress(InetAddress)`
after the association is established. The first list entry is always the primary address — the same
ordering Yate's `addr` + `sctp_bindx` fields have.

### Plain SCTP only — DTLS explicitly out

No DTLS handshake, no port 5658, no PPID 47 sending. The §2.1.1 responder refinement (delaying
unordered delivery until the initiator has sent its first message) is moot because nothing is sent
unordered. The decoder-side PPID-47 drop keeps a later DTLS/SCTP implementation additive.
`SCTP_NODELAY` is not set (obsolete for SCTP).

### TCP path byte-identical (backwards compatibility)

The default `listen`/`connect` overloads behave exactly as before; all existing callers
(sparta-hss' 3-arg `connect`, plain `listen`) are untouched.

## Consequences

- A live node can serve TCP and SCTP **on the same port number** (3868) — TCP and SCTP bind in
  independent protocol namespaces, so there is no collision. This is Yate's production shape
  (`tcp_*:3868` + `sctp_*:3868`).
- macOS has no SCTP kernel support (and the development sandbox blocks JVM sockets entirely):
  encoder/decoder behavior is covered by offline unit tests (`SctpMessageDecoderTest`,
  `SctpAnswerEncoderTest` (both encoders), `DiameterNodeSctpValidationTest` — framing,
  PPID filter, stream/order/PPID fields, address validation). Real SCTP wire behavior cannot be
  exercised outside Linux.

> **Guardrail:** Before any production activation of the SCTP path, a two-node Linux wire test is
> mandatory (two nodes, multi-homing, CER/CEA, DWR, relay roundtrip) — the e2e docker-compose environment exists
> exactly for this; SCTP is not verified by the offline test suite.

- Sending every message ordered on stream 0 preserves send order inside one association, so the
  CER/CEA and DPR/DPA state handling sees messages in the order the peer sent them, as on TCP.
- Head-of-line blocking is accepted: one lost or delayed message holds back every later message on
  the association until it is retransmitted. Request/answer correlation stays identifier-based
  (hop-by-hop), so a multi-stream scheme can be added later without session-layer changes.
- DTLS/SCTP (specs/dtls-sctp remainder) stays a separate future feature on the same pipeline seams.

## Related ADRs

- **See also:** ADR-0002 (the full feature — including DTLS/SCTP — was specced in
  `specs/dtls-sctp/01-requirements.md`; DTLS/SCTP remains planned there, the plain-SCTP subset is
  the implemented design documented in this ADR).
