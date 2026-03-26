# Requirements: Per-Package Message Factories

Implements ADR-0006.

## Functional requirements

1. A `DiameterPackageFactory` interface exists in `core`. It declares two methods:
   `createForParsing` and `createAnswer`, both returning `null` for command codes the
   factory does not own.

2. A single `public` `BaseMessageFactory` class in `base.messages` implements
   `DiameterPackageFactory`, covering all seven RFC 6733 base command codes via `switch`
   expressions with direct constructor calls.

3. `Command.parseMessage` discovers `DiameterPackageFactory` implementations on the
   classpath at startup (via the reflections library) and uses them to parse incoming
   messages. No per-class `@DiameterRequest` / `@DiameterResponse` annotation scan.

4. `Command` exposes a static `register(DiameterPackageFactory)` method that allows
   callers to add factories programmatically after startup, for embedders that add a
   Diameter application module at runtime.

5. `DiameterMessageFactory.createAnswer` iterates the discovered factories to construct
   the outgoing answer, then copies identifiers and sets the result code as today.

6. `DiameterMessageFactory.createForParsing` remains as a public method (it is part of
   the test API) but delegates to the discovered factories instead of calling
   `setAccessible(true)`.

7. `setAccessible(true)` is absent from all message construction paths.

8. `@DiameterRequest` and `@DiameterResponse` are removed from every `In` class and the
   annotation types themselves are deleted.

9. `In` constructors and `OutgoingAnswer.Out` constructors are widened from `private` to
   package-private. They must not be widened further.

10. All existing tests pass without modification.

11. A `BaseMessageFactoryTest` in package `com.sipgate.sparta.diameter.base.messages`
    uses `Reflections` to discover every concrete `IncomingRequest` and `IncomingAnswer`
    subtype in the package, instantiates each via its package-private constructor (no
    `setAccessible`), reads the command code, and asserts that `BaseMessageFactory`
    returns non-null for every discovered code. This test must fail if a new message
    class is added without updating the factory switch.

## Out of scope

- Adding a factory for any 3GPP module — those modules have no message classes yet.
- Changing the `createAnswer` return type or the one accepted unchecked cast.
