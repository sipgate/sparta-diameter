# Election — Requirements

## Context

RFC 6733 §5.6.4 defines the election process for resolving simultaneous connection attempts between two Diameter peers. In a carrier network, both ends of a peering relationship often run 24/7 and reconnect autonomously after failures. If both attempt to connect to each other at the same time, each node ends up holding two transport connections to the same peer: one it initiated (I-connection) and one it accepted (R-connection). Running Diameter over both would split the peer state machine. The election deterministically selects which connection survives, without any coordination message.

## When an election occurs (§5.6 state machine)

An election is triggered on the responder side when an inbound CER (R-Conn-CER event) arrives while the local node already has an outbound connection in progress. The affected state transitions are:

| Current state | Event | Action | Next state |
|---|---|---|---|
| `Wait-Conn-Ack` | R-Conn-CER | R-Accept, Process-CER, **Elect** | `Wait-Conn-Ack/Elect` |
| `Wait-I-CEA` | R-Conn-CER | R-Accept, Process-CER, **Elect** | `Wait-Returns` |
| `Wait-Conn-Ack/Elect` | I-Rcv-Conn-Ack | I-Snd-CER, **Elect** | `Wait-Returns` |
| `Wait-Returns` | Win-Election | I-Disc, R-Snd-CEA | `R-Open` |
| `Wait-Returns` | I-Rcv-CEA | R-Disc | `I-Open` |
| `Wait-Returns` | I-Peer-Disc | I-Disc, R-Snd-CEA | `R-Open` |

## The election algorithm (§5.6.4)

The election is performed on the responder. The responder compares the Origin-Host it received in the inbound CER against its own Origin-Host as two octet streams.

- If the **local** Origin-Host lexicographically succeeds (is greater than) the **received** Origin-Host → local node wins → `Win-Election` event is issued locally.
- If the **received** Origin-Host lexicographically succeeds the **local** Origin-Host → local node loses.

Comparison is case-insensitive: octets in the ASCII range `'a'`–`'z'` MUST compare equally to `'A'`–`'Z'` (consistent with DNS case insensitivity).

## What the winner and loser do

- The **winner** MUST close the connection it *initiated* (I-connection) and keep the connection it *accepted* (R-connection). It then sends a CEA on the R-connection and transitions to `R-Open`.
- The **loser** keeps the connection it *initiated* (I-connection) and discards the R-connection. It waits for the CEA on the I-connection and transitions to `I-Open` upon receipt.

Both peers independently apply the same comparison and always reach opposite conclusions, so they always agree on which connection survives.

## Relationship to the current implementation

`PeerState` already declares `WAIT_CONN_ACK_ELECT` and `WAIT_RETURNS`, but neither `DiameterInitiatorSession` nor `DiameterResponderSession` implements the transitions that enter or resolve those states. The election is currently unimplemented dead state.

The current session-per-connection design (ADR-0004) assigns a fresh `DiameterSession` to each accepted connection. To support the election, the session layer needs to be able to look up an existing peer state machine by Origin-Host when a CER arrives on a new connection, and hand the new connection over to that machine as an R-Conn-CER event.

## Acceptance criteria

- When a CER arrives on an accepted connection and the local node already has an outbound connection to the same Origin-Host in progress, the session layer triggers an election rather than treating it as a new independent peer.
- The election comparison is performed on the responder: local Origin-Host vs. received Origin-Host, byte-by-byte, ASCII case-insensitive.
- If the local node wins: the initiator connection is closed, a CEA is sent on the responder connection, and the peer state transitions to `R-Open`.
- If the local node loses: the responder connection is closed, the session waits for the CEA on the initiator connection, and the peer state transitions to `I-Open` upon receipt.
- The surviving connection is the one used for all subsequent messages to that peer.
- If the peer disconnects the initiator connection before the election resolves (`I-Peer-Disc` in `Wait-Returns`), the responder connection is kept and a CEA is sent — same outcome as winning.
