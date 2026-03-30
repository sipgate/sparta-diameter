---
title: "ADR-0009: Error Answer Signalling"
description: "E-bit error answers are signalled via completeExceptionally() with DiameterErrorAnswerException"
owner: "sipgate-uhlig"
status: accepted
tags:
  - error-handling
  - api
created: "2026-03-30"
---

## Context

RFC 6733 §7.2 defines error answers: answers with the E-bit set that do not conform to the normal CCF for their command. Every error answer is a response to a specific request — there are no unsolicited E-bit messages. The structure is uniform across all command codes (same fixed AVP set regardless of which request triggered the error).

The session API exposes two surfaces that must handle this:

- `DiameterRequestHandler<R, A>` — a handler registered for inbound requests must be able to return either a normal answer (`A`) or an error answer.
- `DiameterSession.send()` — a caller sending a request must be able to receive either the expected answer type (`A`) or an error answer.

Both currently return `CompletableFuture<A>`, typed to the command-specific answer. An error answer is not an `A` — it has a different structure — so the return type must somehow accommodate both cases.

Four options were evaluated:

| Option | Mechanism |
|---|---|
| `completeExceptionally()` | Use the failure channel of `CompletableFuture<A>` with a typed exception carrying the `ErrorAnswer` |
| Custom sealed type | Introduce `DiameterResponse<A>` — `Ok<A> \| Err` — as a wrapper around the future value |
| Vavr `Either` | Same shape as above, but provided by the vavr library |
| Wider return type | Widen `A` to a common supertype (e.g. `Answer<?>`) to accommodate both |

## Decision

Signal E-bit error answers via `completeExceptionally()` using `DiameterErrorAnswerException`, a checked-equivalent exception that carries the `ErrorAnswer`.

`CompletableFuture<A>` already has two channels: a success channel typed to `A` and a failure channel typed to `Throwable`. The failure channel with a named, structured exception is the existing Java idiom for exactly this shape — a result that is either a typed value or a typed failure. Introducing a separate wrapper type would be a parallel invention of something already present.

Callers handle the error case via `.handle()` or `.exceptionally()` with an `instanceof` pattern match:

```java
session.send(request).handle((answer, ex) -> {
    if (ex instanceof DiameterErrorAnswerException e) {
        // protocol error — inspect e.getErrorAnswer()
    } else if (ex != null) {
        // unexpected failure
    } else {
        // normal answer
    }
});
```

### Rejected alternatives

**Custom sealed type (`DiameterResponse<A>`)** — semantically the most honest representation (an error answer is a value, not a failure of computation), but re-implements a discriminated union that `CompletableFuture` already provides. The added type imposes unwrapping on every caller without a meaningful return. Rejected: no new invention when the existing mechanism suffices.

**Vavr `Either`** — same shape as the custom type, with better ergonomics (`.recover(Class, fn)`). Rejected: adds a dependency solely to avoid writing one exception class.

**Wider return type** — widening `A` to `Answer<?>` or a common supertype loses the compile-time guarantee that `send(UpdateLocationRequest)` returns an `UpdateLocationAnswer`. The `<R, A>` binding is a first-class design property. Rejected: type safety is not negotiable.

## Consequences

- The `<R, A>` type pairing on the success path is fully preserved.
- Callers that only care about the success case can ignore `.exceptionally()` and let the failure propagate naturally through the `CompletableFuture` chain — the same behaviour as any other unhandled future failure.
- Callers that must handle error answers use `.handle()` with an `instanceof` check; there is no typed filter method (`.onFailure(Class, fn)`) on `CompletableFuture`.
- `DiameterErrorAnswerException` is a named, documented exception class — the error case is discoverable via Javadoc and IDE tooling, not hidden in a type parameter.

## Related ADRs

- **See also:** ADR-0007 (E-bit error answers at the transport/decode layer — when the stack itself generates one before reaching the application)
