---
title: "ADR-0006: Per-Package Message Factories"
description: "Replace classpath-wide annotation scanning and reflection access bypasses with per-package factory classes"
owner: "sipgate-uhlig"
status: draft
tags:
  - adr
  - factory
  - reflection
  - messages
created: "2026-03-25"
---

## Context

`Command.initializeCommandTypes()` scans the classpath for `@DiameterRequest` and
`@DiameterResponse` annotations on every `In` nested class to build command-code→class maps at
startup. `DiameterMessageFactory` then instantiates those classes by calling
`setAccessible(true)` on their private constructors to bypass Java's access control.

Two problems:

1. **Access bypass.** `setAccessible(true)` deliberately circumvents the visibility rules
   declared by the message classes themselves. JPMS module boundaries or a future security
   manager would break it without warning.

2. **Annotation proliferation.** Every `In` class carries `@DiameterRequest` or
   `@DiameterResponse` with a command-code constant. That constant already appears in the
   superclass constructor call — the annotation duplicates it, creating a second place for them
   to diverge silently.

A third problem surfaces when the codebase grows to multiple Maven modules (one per Diameter
application — s6a, Cx/Dx, Gd/Sgd, …): the classpath-wide scan has no clean boundary between
the base library and application modules, and adding a new application module requires no
explicit registration — which is the failure mode, not the feature.

## Decision

### Per-package factory class

Each messages package provides exactly one class implementing `DiameterPackageFactory`, defined
in `core`:

```java
public interface DiameterPackageFactory {
    /**
     * Returns null if this factory does not handle the given command code and application ID.
     * The caller is responsible for throwing if no factory produces a result.
     */
    IncomingCommand createForParsing(int commandCode, int applicationId, boolean isRequest,
                                     HopByHopId hopByHop, EndToEndId endToEnd,
                                     boolean retransmitted);

    /**
     * Returns null if this factory does not handle the given command code and application ID.
     */
    OutgoingAnswer<?> createAnswer(int commandCode, int applicationId,
                                   HopByHopId hopByHop, EndToEndId endToEnd);
}
```

The implementation uses `switch` expressions mapping command codes to direct constructor calls:

```java
// com.sipgate.sparta.diameter.messages.rfc6733
final class Rfc6733MessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        return switch (commandCode) {
            case CMD_ABORT_SESSION -> isRequest
                    ? new AbortSessionRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AbortSessionAnswer.In(hopByHop, endToEnd);
            // …
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer<?> createAnswer(final int commandCode, final int applicationId,
                                          final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return switch (commandCode) {
            case CMD_ABORT_SESSION -> new AbortSessionAnswer.Out(hopByHop, endToEnd);
            // …
            default -> null;
        };
    }
}
```

### Package-private constructors

`In` and `Out` constructors on incoming messages and outgoing answers are widened from `private`
to package-private. The factory lives in the same package and calls them directly — no
`setAccessible(true)`.

> **Guardrail:** `In` constructors and `OutgoingAnswer.Out` constructors must never be widened
> beyond package-private. Wire-parsed and factory-constructed objects must not be directly
> instantiable by application code.

`OutgoingRequest.Out` constructors remain `public` — application code creates outgoing requests
directly without going through a factory.

### Factory discovery and error handling

`Command.parseMessage` discovers factory instances at startup using the reflections library,
scanning for `DiameterPackageFactory` implementations. This replaces the current per-class
annotation scan.

Each factory returns `null` for command codes and application IDs it does not own. The parser
iterates the discovered factories, takes the first non-null result, and throws `DiameterException`
if no factory handles the combination. Responsibility is cleanly split: factories express
capability through their return value; the parser owns the error.

The scan is narrow: one factory class per messages package, versus one annotated `In` class per
command under the current scheme. Startup cost is lower, and the approach works transparently
across Maven modules — any jar on the classpath containing a `DiameterPackageFactory`
implementation is automatically included without any per-module registration file.

The `applicationId` is included in both factory methods. Vendor-specific Diameter applications
may reuse command codes across different application namespaces; the `applicationId` parsed from
the wire is the disambiguator. Factories that cover a single application ID may ignore it and
match on command code alone; factories covering vendor-specific extensions should check both.

### `@DiameterRequest` and `@DiameterResponse` removed

Routing logic moves entirely into each factory's `switch`. The annotations are no longer needed
on individual `In` classes.

### `DiameterMessageFactory.createAnswer` retained

`DiameterMessageFactory.createAnswer` in `core` retains its current responsibility: find the
right factory by command code, delegate construction to it, then copy identifiers and set the
result code. One `@SuppressWarnings("unchecked")` cast from `OutgoingAnswer<?>` to the
caller's `A` is accepted and isolated to this single method.

## Rejected alternatives

**`ServiceLoader` for factory discovery** — each Maven module would need a
`META-INF/services/…DiameterPackageFactory` file. Per-jar maintenance overhead with no
benefit over the reflections scan, which discovers implementations automatically.

**Abstract `createOutAnswer()` on `IncomingRequest`** — each `In` class implements it to
construct its corresponding `Out` answer, eliminating the factory's `createAnswer` switch and
the unchecked cast in core. Rejected: the per-class boilerplate (one override per command) is
more redundant code than the single unchecked cast it avoids.

**Generic type extraction for answer lookup** — derive the `Out` class from the `In` class's
generic superclass type arguments (`ParameterizedType.getActualTypeArguments()[1]`). This is
valid Java, but it replaces an explicit `switch` with implicit type algebra that requires
knowledge of the class hierarchy to interpret. The switch is clearer.

## Consequences

- `setAccessible(true)` is gone from all message construction paths.
- `@DiameterRequest` and `@DiameterResponse` are removed from every `In` class.
- Adding a new command requires updating the package's factory `switch` — an explicit, auditable
  change with a compile-time signal (non-exhaustive switch warning) if a case is missing.
- Adding a new Diameter application (new Maven module + messages package) requires providing
  one `DiameterPackageFactory` implementation. No other registration.
- One unchecked cast remains in `DiameterMessageFactory.createAnswer`; it is isolated and
  documented.
- The reflections library is retained, but its scan surface shrinks from every annotated `In`
  class to one factory class per package.

## Related ADRs

- **Extends:** ADR-0005 (defines the `In`/`Out` nested class structure this ADR builds on)
