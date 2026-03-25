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

## Identifier ownership

| Object | Identifiers | Set by |
|---|---|---|
| `In` (incoming request or answer) | `final HopByHopId`, `final EndToEndId` | Wire parser |
| `OutgoingAnswer` | `final HopByHopId`, `final EndToEndId` | `DiameterMessageFactory.createAnswer()`, copied from `IncomingRequest` |
| `OutgoingRequest` | none | `Session.send()` generates them, passes to encoder |

## Pending-requests map

```java
ConcurrentHashMap<HopByHopId, CompletableFuture<?>> pendingRequests;
```

The key changes from `int` to `HopByHopId`.

## Encoders

`Command.writeTo(DataOutputStream)` is removed. Serialization is required on the two outgoing
types only, with signatures that match their identifier ownership:

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

Two encoder classes wrap these, one per direction:

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

## Runtime guard on `In` setters

`Command.setAVP` checks `this instanceof IncomingCommand` and throws
`UnsupportedOperationException` if true. Matches the Java unmodifiable-collections contract.

`IncomingCommand` is a marker interface implemented by both `IncomingRequest` and
`IncomingAnswer` — any wire-parsed object is immutable.
