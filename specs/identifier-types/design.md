# Identifier Types — Design

See ADR-0005 for the decision rationale and rejected alternatives.

## Records

```java
record HopByHopId(int value) {}
record EndToEndId(int value) {}
```

## Marker interfaces

```java
interface IncomingCommand  {}
interface IncomingRequest  extends IncomingCommand {}
interface IncomingAnswer   extends IncomingCommand {}
interface OutgoingRequest  {}
interface OutgoingAnswer   {}
```

## Command class structure

Each command type is split into `In` and `Out` static nested classes. The enclosing class is
abstract and never instantiated. The F-bounded type parameter `T` flows into every AVP mixin so
setters return the correct concrete type without overrides:

```java
// enclosing class — holds mixins and generic signature only
abstract class MoForwardShortMessageRequest<T extends MoForwardShortMessageRequest<T, A>, A extends Answer<A>> {

    // In — wire-parsed; setters throw UnsupportedOperationException
    static final class In extends MoForwardShortMessageRequest<In, MoForwardShortMessageAnswer.Out> implements IncomingRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) { ... }
    }

    // Out — application-created; identifiers assigned by Session.send()
    static final class Out extends MoForwardShortMessageRequest<Out, MoForwardShortMessageAnswer.In> implements OutgoingRequest {}
}
```

## Annotation placement

`@DiameterRequest` and `@DiameterResponse` are placed on the `In` nested class — not on the
enclosing abstract class. The reflection scan in `Command.initializeCommandTypes()` discovers
nested static classes (e.g. `XxxRequest$In`) and the existing
`Request.class.isAssignableFrom(cls)` / `Answer.class.isAssignableFrom(cls)` filters still
apply, since `In` inherits from `Request` / `Answer` through the enclosing class.

`createAnswer()` needs the `Out` class for the answer type. It derives it by convention from the
registered `In` class: given `XxxAnswer$In`, look up the enclosing class (`XxxAnswer`), then
find the nested `Out` class via reflection. No second annotation is needed.

## Identifier ownership

| Object | Identifiers | Set by |
|---|---|---|
| `In` (incoming request or answer) | `final HopByHopId`, `final EndToEndId` | Wire parser |
| `OutgoingAnswer` | `final HopByHopId`, `final EndToEndId` | `DiameterMessageFactory.createAnswer()`, copied from `IncomingRequest` |
| `OutgoingRequest` | none | `Session.send()` generates them, passes to encoder |

## DiameterMessageFactory

Two distinct factory entry points, one per usage context:

```java
// Package-private — called only by Command.parseMessage (wire parsing → always In)
// The compound bound rejects any Out class at compile time.
static <C extends Command<C> & IncomingCommand> C createForParsing(
        Class<C> type,
        HopByHopId hopByHop,
        EndToEndId endToEnd,
        boolean retransmitted);

// Public — called by application code to create an outgoing request (Out)
public static <R extends OutgoingRequest & Command<?>> R createRequest(Class<R> type);
```

`createRequest` invokes the no-argument private constructor on `Out`. Identifiers are not set
here; `Session.send()` generates them and passes them to the encoder at send time.

## Pending-requests map

```java
ConcurrentHashMap<HopByHopId, CompletableFuture<?>> pendingRequests;
```

The key changes from `int` to `HopByHopId`.

## Encoders

`Command.writeTo(DataOutputStream)` is removed. The Netty `DiameterMessageEncoder` is removed.
Serialization is required on the two outgoing types only, with signatures that match their
identifier ownership:

```java
// OutgoingAnswer already holds HopByHopId and EndToEndId as final fields
interface OutgoingAnswer {
    void writeTo(DataOutputStream out) throws IOException;
}

// OutgoingRequest carries no identifiers; they are injected at send time
interface OutgoingRequest {
    void writeTo(DataOutputStream out, HopByHopId hopByHop, EndToEndId endToEnd) throws IOException;
}
```

Two encoder classes replace the old single encoder, one per direction:

```java
class OutgoingAnswerEncoder {
    void encode(OutgoingAnswer answer, DataOutputStream out) throws IOException;
}

class OutgoingRequestEncoder {
    void encode(OutgoingRequest request, HopByHopId hopByHop, EndToEndId endToEnd, DataOutputStream out) throws IOException;
}
```

`IncomingRequest` and `IncomingAnswer` have no `writeTo` — wire-parsed objects are never
re-serialized by the session layer.

## GenericCommand

`GenericCommand` follows the same `In` / `Out` split. `GenericCommand.Out` may optionally hold
its own `HopByHopId` and `EndToEndId` (e.g. when relaying a received message with the original
identifiers preserved). If they are set, `writeTo` uses them and ignores the identifiers injected
by the encoder:

```java
@Override
void writeTo(DataOutputStream out, HopByHopId hopByHop, EndToEndId endToEnd) throws IOException {
    final HopByHopId effectiveHopByHop = this.hopByHop != null ? this.hopByHop : hopByHop;
    final EndToEndId effectiveEndToEnd  = this.endToEnd  != null ? this.endToEnd  : endToEnd;
    // ... serialize with effective ids
}
```

## ErrorAnswer

`ErrorAnswer` is application-created (sent by the session layer) and implements `OutgoingAnswer`.
It gains `writeTo(DataOutputStream)` like any other `OutgoingAnswer`.

## Runtime guard on `In` setters

`Command.setAVP` checks `this instanceof IncomingCommand` and throws
`UnsupportedOperationException` if true. Matches the Java unmodifiable-collections contract.

`IncomingCommand` is a marker interface implemented by both `IncomingRequest` and
`IncomingAnswer` — any wire-parsed object is immutable.
