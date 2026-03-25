# Identifier Types — Tasks

- [ ] Define `HopByHopId` and `EndToEndId` records
- [ ] Define `IncomingRequest`, `OutgoingRequest`, `IncomingAnswer`, `OutgoingAnswer` marker interfaces
- [ ] Refactor each command class to `In` / `Out` static nested structure
- [ ] Add runtime guard to `Command.setAVP`: throw `UnsupportedOperationException` when `this instanceof IncomingCommand`
- [ ] Update wire parser to construct `In` instances with `HopByHopId` and `EndToEndId` from the parsed header
- [ ] Update `DiameterMessageFactory.createAnswer()` to copy `HopByHopId` and `EndToEndId` from `IncomingRequest` into `OutgoingAnswer`
- [ ] Remove `Command.writeTo(DataOutputStream)`
- [ ] Add `writeTo(DataOutputStream)` to `OutgoingAnswer`
- [ ] Add `writeTo(DataOutputStream, HopByHopId, EndToEndId)` to `OutgoingRequest`
- [ ] Implement `OutgoingAnswerEncoder`
- [ ] Implement `OutgoingRequestEncoder`
- [ ] Update `Session.send()` to generate `HopByHopId` and `EndToEndId`, pass to encoder
- [ ] Update `DiameterInitiatorSession.cerHopByHop` from `int` to `HopByHopId`
- [ ] Update pending-requests map key from `int` to `HopByHopId`
- [ ] Update all call sites for `tryCompleteFromPendingMap`, `cancel()`, and `failAllPending()`
