# Tasks: Per-Package Message Factories

## 1. Create `DiameterPackageFactory` interface

Add `com.sipgate.sparta.diameter.base.core.DiameterPackageFactory` with:
- `IncomingCommand createForParsing(int commandCode, int applicationId, boolean isRequest, HopByHopId hopByHop, EndToEndId endToEnd, boolean retransmitted)`
- `OutgoingAnswer<?> createAnswer(int commandCode, int applicationId, HopByHopId hopByHop, EndToEndId endToEnd)`

## 2. Widen 21 constructors to package-private

Remove the `private` keyword from the constructors listed in `design.md`. Do not widen beyond package-private.

## 3. Create `public BaseMessageFactory`

Add `public final class BaseMessageFactory implements DiameterPackageFactory` in `com.sipgate.sparta.diameter.base.messages` with both switch expressions covering all 7 RFC 6733 command codes. No-arg constructor must be public for reflections instantiation.

## 4. Write `BaseMessageFactoryTest`

Add `BaseMessageFactoryTest` in `com.sipgate.sparta.diameter.base.messages`. Uses `Reflections` to discover all concrete `IncomingRequest` and `IncomingAnswer` subtypes; instantiates each via its package-private constructor (no `setAccessible`); asserts factory returns non-null for every discovered command code. Write this test first — it must be red before task 3 is complete.

## 5. Replace annotation scan in `Command` with factory discovery

- Delete `PACKAGES_TO_SCAN`, `REQUEST_TYPES`, `ANSWER_TYPES`, and `initializeCommandTypes()`.
- Add `static final List<DiameterPackageFactory> FACTORIES` populated by a Reflections scan for `DiameterPackageFactory` subtypes under `"com.sipgate.sparta.diameter"`.
- Add `public static void register(DiameterPackageFactory factory)` that appends to `FACTORIES`.
- Update `parseMessage` to iterate `FACTORIES`, take the first non-null result from `createForParsing`, and throw `DiameterException` if none match.

## 6. Update `DiameterMessageFactory` to delegate to factories

- `createForParsing`: iterate `Command.FACTORIES`, return first non-null result, throw `IllegalArgumentException` if none matches.
- `createAnswer`: iterate `Command.FACTORIES` for first non-null `createAnswer` result, then apply the existing identifier-copy and result-code logic.
- Delete `instantiateInRequest`, `instantiateInAnswer`, `instantiateOutAnswer`, and `findOutClass`.
- No `setAccessible(true)` calls must remain.

## 7. Remove annotations and delete annotation types

- Remove `@DiameterRequest` from all 7 request `In` classes.
- Remove `@DiameterResponse` from all 7 answer `In` classes.
- Delete `DiameterRequest.java`, `DiameterResponse.java`, and the `core.annotations` package.

## 8. Verify all tests pass

Run `mvn test` in `sparta-diameter-base`. All tests — including the new `BaseMessageFactoryTest` — must pass.
