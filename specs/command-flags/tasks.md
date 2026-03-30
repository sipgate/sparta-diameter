# Command Flags — Tasks

## [ ] 1. Restructure `ErrorAnswer` into a sealed interface with `In` and `Out`

- Create `ErrorAnswer<T extends ErrorAnswer<T>>` as a sealed interface carrying all error CCF mixins (`HasResultCodeAVP`, `HasSessionIdAVP`, `HasOriginStateIdAVP`, `HasErrorMessageAVP`, `HasErrorReportingHostAVP`, `HasFailedAVP`, `HasExperimentalResultAVP`, `HasProxyInfoAVP`)
- Move the existing `ErrorAnswer` class body into `ErrorAnswer.Out` implementing `ErrorAnswer<Out>`
- Create `ErrorAnswer.In extends IncomingAnswer<In>` implementing `ErrorAnswer<In>` with a public constructor
- Make the `ErrorAnswer.Out` constructor public; remove the `create()` factory method
- Update the two md files that reference the old class name

## [ ] 2. Introduce `DiameterErrorAnswerException`

- Create `DiameterErrorAnswerException extends Exception` in the session package
- Single constructor: `DiameterErrorAnswerException(ErrorAnswer answer)`
- Single getter: `ErrorAnswer getAnswer()`

## [ ] 3. Propagate the E-bit through `Command.parseMessage`

- Extract `isError = (flags & 0x20) != 0` alongside the existing R-bit and T-bit extraction
- Pass `isError` to `DiameterMessageFactory.createForParsing`

## [ ] 4. Route error answers to `ErrorAnswer.In` in `DiameterMessageFactory`

- When `createForParsing` is called with `isRequest = false` and `isError = true`, return a new `ErrorAnswer.In` directly, bypassing command-specific factory lookup
- Ensure command code and identifiers are set for correlation

## [ ] 5. Route E-bit answers to `completeExceptionally` in `DiameterSession.complete()`

- Check `answer instanceof ErrorAnswer.In` after removing the pending request
- On match: `completeExceptionally(new DiameterErrorAnswerException(errorAnswer))`
- On no match: existing `complete(answer)` path unchanged

## [ ] 6. Handle `DiameterErrorAnswerException` in `DiameterSession.dispatchInboundRequest()`

- In the `whenComplete` callback, check `err instanceof DiameterErrorAnswerException e && e.getAnswer() instanceof ErrorAnswer.Out out`
- On match: `peer.send(out)` directly
- On other failure: existing `DIAMETER_UNABLE_TO_COMPLY` path unchanged

## [ ] 7. Tests

- `ErrorAnswer.In` parses correctly from a wire buffer with E-bit set
- `DiameterSession.send()` future completes exceptionally with `DiameterErrorAnswerException` when the peer responds with an E-bit answer
- A handler completing exceptionally with `DiameterErrorAnswerException(ErrorAnswer.Out)` causes the session to send that `ErrorAnswer.Out` to the peer
- A handler completing exceptionally with any other exception still triggers `DIAMETER_UNABLE_TO_COMPLY`
