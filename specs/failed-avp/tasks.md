# Failed-AVP and Error Code Semantics — Tasks

## 1. Split `DiameterSession.stop()` into three methods

Refactor the existing `stop()` method into `stop()`, `stopGracefully()`, and `closeGracefully()` as specified in `design.md`. All three methods must have Javadoc. Update `DiameterInitiatorSession` to override all three where reconnect suppression or Tc-timer cancellation differs.

Acceptance criteria:
- `stop()` closes the channel immediately, sets `shuttingDown = true`, no DPR
- `stopGracefully()` sends DPR with `DCC_DO_NOT_WANT_TO_TALK_TO_YOU`, sets `shuttingDown = true`
- `closeGracefully()` sends DPR with `DCC_REBOOTING`, does not set `shuttingDown`
- All callers of the old `stop()` are updated to the correct replacement
- Existing tests pass; new tests cover reconnect suppression behaviour

## 2. Introduce `DiameterResultCodeException` and `AVPParseException`

Add the exception hierarchy from `design.md`:

```
DiameterException
└── DiameterResultCodeException(ResultCode)
    └── AVPParseException(ResultCode, AVP)
```

`DiameterResultCodeException` carries the integer result code. `AVPParseException` additionally carries the offending `AVP`. Both are checked exceptions.

Acceptance criteria:
- Both classes exist, extend the correct parent, and are checked
- Unit tests verify the hierarchy (e.g. `AVPParseException instanceof DiameterResultCodeException`)

## 3. Update `Command` to throw typed exceptions

Replace the three in-scope `DiameterException` throws in `Command`:

- `getMessageLength()` line 237 → remains `DiameterException` (no result code context)
- `parseMessage()` line 256 → remains `DiameterException` (no result code context)
- `parseMessage()` line 267 → `DiameterResultCodeException(RES_DIAMETER_UNSUPPORTED_VERSION)`, reading bytes 4–19 from the buffer before throwing so commandCode, applicationId, hopByHop, and endToEnd are available to callers

Acceptance criteria:
- Line 267 throws `DiameterResultCodeException` carrying the four header fields
- Existing unit tests for `Command.parseMessage()` pass
- New unit test: receiving a version-2 message produces `DiameterResultCodeException` with result code `5011`

## 4. Detect AVP violations in `AVP.readFrom()`

Add three detection branches in `AVP.readFrom()`, each throwing `AVPParseException`:

- After the flags byte (line 826): reserved bits (4–0) non-zero → `RES_DIAMETER_INVALID_AVP_BIT_COMBO (5016)`
- At the length sanity check (line 830): replace `EOFException` → `AVPParseException` with `RES_DIAMETER_INVALID_AVP_LENGTH (5014)`. When the header is too broken to construct a full AVP, include a zero-padded stub (see `design.md`)
- After the registry lookup (line 847): `definition == null && mandatory` → `AVPParseException` with `RES_DIAMETER_AVP_UNSUPPORTED (5001)`

Acceptance criteria:
- Unrecognized AVP with M-bit clear is still returned as a raw `AVP` (no change)
- Unit tests cover all three throw paths with the correct result codes and offending AVPs

## 5. Apply single-error rule in `Command.parseAVPs()`

`parseAVPs()` catches `AVPParseException` on the first occurrence and re-throws it, stopping AVP parsing. This implements the single-error rule from RFC 6733 §7 without additional logic.

Acceptance criteria:
- A message with two bad AVPs produces exactly one `AVPParseException` for the first one
- Unit test confirms single-error behaviour

## 6. Handle parse exceptions in the session layer

In the `onMessage` path (both `DiameterInitiatorSession` and `DiameterResponderSession`), wrap the `Command.parseMessage()` call to catch and dispatch parse exceptions using multiple `catch` blocks:

```java
catch (AVPParseException e) {
    // send ErrorAnswer.Out with result code + Failed-AVP wrapping e.getAvp()
}
catch (DiameterResultCodeException e) {
    // send ErrorAnswer.Out with result code, no Failed-AVP
    // then call stop()
}
catch (DiameterException e) {
    // issue TCP RESET, log, do not suppress reconnect
}
```

The `ErrorAnswer.Out` must include `Origin-Host`, `Origin-Realm` (from `config`), `Result-Code` (from exception), `Session-Id` (echoed from request if present), and `Proxy-Info` AVPs (copied from request if present). `Error-Reporting-Host` is set if the session's Origin-Host differs from the outgoing Origin-Host.

For `DiameterResultCodeException` (5011): call `stop()` after sending the reply.

Acceptance criteria:
- An inbound message with an unrecognized mandatory AVP produces a well-formed error answer with `Failed-AVP`
- An inbound message with unsupported version produces a well-formed error answer, connection closes without reconnect
- An unparseable message (too short) causes a RESET and reconnect is not suppressed on the initiator side
- Integration test or unit test with a mocked peer covers each catch branch
