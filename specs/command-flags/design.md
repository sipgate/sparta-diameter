# Command Flags — Design

## Scope

P-bit is deferred: this node is a Diameter endpoint, never a relay or proxy, so P-bit enforcement is irrelevant. `Command.isProxiable()` already exists and is encoded/decoded correctly; it is not added to `IncomingCommand`.

This design covers the E-bit only.

## Current state

`Command.parseMessage` reads the flags byte but discards the error bit — it is not passed to `DiameterMessageFactory.createForParsing`. All answers, including E-bit answers, are delivered to the caller's future via `complete()` as if they were normal answers.

`dispatchInboundRequest` routes any handler failure to `DIAMETER_UNABLE_TO_COMPLY` with no way for a handler to intentionally return a protocol error answer.

## Changes

### 1. `ErrorAnswer` becomes a sealed interface

`ErrorAnswer` is restructured to follow the same `In`/`Out` pattern used by every other command. The existing `ErrorAnswer` class becomes `ErrorAnswer.Out`. A new `ErrorAnswer.In` represents a received error answer.

The outer `ErrorAnswer` is a generic sealed interface that carries all mixins once. `In` and `Out` inherit them via the type parameter:

```java
public sealed interface ErrorAnswer<T extends ErrorAnswer<T>>
        extends HasResultCodeAVP<T>, HasSessionIdAVP<T>, HasOriginStateIdAVP<T>,
                HasErrorMessageAVP<T>, HasErrorReportingHostAVP<T>,
                HasFailedAVP<T>, HasExperimentalResultAVP<T>, HasProxyInfoAVP<T>
        permits ErrorAnswer.In, ErrorAnswer.Out {

    final class In extends IncomingAnswer<In> implements ErrorAnswer<In> {
        public In(final HopByHopId hopByHop, final EndToEndId endToEnd, ...) { ... }
    }

    final class Out extends OutgoingAnswer<Out> implements ErrorAnswer<Out> {
        public Out(final int commandCode, final boolean proxiable,
                   final int applicationId,
                   final HopByHopId hopByHop, final EndToEndId endToEnd) { ... }
    }
}
```

The static `ErrorAnswer.create()` factory is dropped — public constructors are sufficient.

### 2. Parse and propagate the E-bit

`Command.parseMessage` extracts the E-bit alongside the existing R-bit and T-bit:

```java
final boolean isError = (flags & 0x20) != 0;
```

`DiameterMessageFactory.createForParsing` receives `isError`. When `isRequest = false` and `isError = true`, the factory returns an `ErrorAnswer.In` directly, bypassing command-specific factory lookup. The command code and identifiers are preserved for correlation with a pending request.

`isError()` is not added to `IncomingCommand` — routing to `ErrorAnswer.In` at parse time makes the method redundant. The type itself carries the information.

### 3. E-bit answer routing in `DiameterSession.complete()`

```java
protected void complete(final IncomingAnswer<?> answer) {
    final PendingRequest<?> pending = pendingRequests.remove(answer.hopByHopId());
    if (pending == null) {
        return;
    }
    pending.timeoutTask.cancel(false);
    if (answer instanceof ErrorAnswer.In errorAnswer) {
        pending.future.completeExceptionally(
                new DiameterErrorAnswerException(errorAnswer));
    } else {
        pending.future.complete(answer);  // unchecked — kept as-is
    }
}
```

### 4. `DiameterErrorAnswerException`

Single constructor, single getter, typed to the wildcard of the sealed interface:

```java
public final class DiameterErrorAnswerException extends Exception {

    private final ErrorAnswer<?> answer;

    public DiameterErrorAnswerException(final ErrorAnswer<?> answer) {
        this.answer = answer;
    }

    public ErrorAnswer<?> getAnswer() {
        return answer;
    }
}
```

`Exception` (checked) is chosen deliberately: a protocol error is a defined protocol outcome, not an infrastructure failure. Callers of `send()` that must handle protocol errors are forced to acknowledge the possibility at compile time, unlike `TimeoutException` which travels through the unchecked `completeExceptionally` channel for infrastructure failures.

### 5. Handler error path in `DiameterSession.dispatchInboundRequest()`

```java
future.whenComplete((answer, err) -> {
    if (err instanceof DiameterErrorAnswerException e
            && e.getAnswer() instanceof ErrorAnswer.Out out) {
        peer.send(out);
    } else if (err != null) {
        peer.send(DiameterMessageFactory.createAnswer(request,
                DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY));
    } else {
        peer.send((OutgoingAnswer<?>) answer);
    }
});
```

## Sequence: handler returning an error answer

```
Application        DiameterSession         Peer
    |                    |                   |
    |   inbound request  |                   |
    |<-------------------|                   |
    |                    |                   |
    | completeExceptionally(                 |
    |   new DiameterErrorAnswerException(    |
    |     new ErrorAnswer.Out(...)))         |
    |------------------->|                   |
    |                    | peer.send(out)    |
    |                    |------------------>|
```

## Sequence: receiving an error answer

```
Application        DiameterSession         Peer
    |                    |                   |
    |  send(request)     |                   |
    |------------------->|------------------>|
    |  CompletableFuture |                   |
    |                    |  ErrorAnswer.In   |
    |                    |<------------------|
    |                    |                   |
    | completeExceptionally(                 |
    |   new DiameterErrorAnswerException(    |
    |     errorAnswer.In))                   |
    |<-------------------|                   |
```

## What does not change

- `DiameterRequestHandler` signature — `CompletableFuture<A>` is unchanged.
- `DiameterSession.send()` signature — `CompletableFuture<A>` is unchanged.
- `Command.isError()` — stays on `Command`, needed for encoding the flags byte.
