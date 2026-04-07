# Disconnect Reason — Requirements

## Goals

When a session closes, the surrounding application currently has no way to learn why. This spec
adds a callback that delivers the close reason.

## Acceptance criteria

- The application can register a listener that is called exactly once when the session closes
- The listener receives a `CloseReason` that covers at minimum:
  - `CAPABILITY_MISMATCH` — CER/CEA negotiation failed
  - `TRANSPORT_ERROR` — unexpected connection loss
  - `PEER_DISCONNECTED` — peer sent DPR
  - `LOCAL_STOP` — local `stop()` call sent DPR
- The listener is called regardless of which path caused the close
- The listener is never called more than once per session instance
