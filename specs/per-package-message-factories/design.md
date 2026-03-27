# Design: Per-Package Message Factories

## DiameterPackageFactory interface

New interface in `com.sipgate.sparta.diameter.base.core`:

```java
public interface DiameterPackageFactory {
    /**
     * Returns null if this factory does not handle the given command code / application ID.
     */
    IncomingCommand createForParsing(int commandCode, int applicationId, boolean isRequest,
                                     HopByHopId hopByHop, EndToEndId endToEnd,
                                     boolean retransmitted);

    /**
     * Returns null if this factory does not handle the given command code / application ID.
     */
    OutgoingAnswer<?> createAnswer(int commandCode, int applicationId,
                                   HopByHopId hopByHop, EndToEndId endToEnd);
}
```

## BaseMessageFactory

New package-private class in `com.sipgate.sparta.diameter.base.messages`:

```java
public final class BaseMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        return switch (commandCode) {
            case DiameterConstants.CMD_ABORT_SESSION -> isRequest
                    ? new AbortSessionRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AbortSessionAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_ACCOUNTING -> isRequest
                    ? new AccountingRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AccountingAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_CAPABILITIES_EXCHANGE -> isRequest
                    ? new CapabilitiesExchangeRequest.In(hopByHop, endToEnd, retransmitted)
                    : new CapabilitiesExchangeAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_DEVICE_WATCHDOG -> isRequest
                    ? new DeviceWatchdogRequest.In(hopByHop, endToEnd, retransmitted)
                    : new DeviceWatchdogAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_DISCONNECT_PEER -> isRequest
                    ? new DisconnectPeerRequest.In(hopByHop, endToEnd, retransmitted)
                    : new DisconnectPeerAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_RE_AUTH -> isRequest
                    ? new ReAuthRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ReAuthAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_SESSION_TERMINATION -> isRequest
                    ? new SessionTerminationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new SessionTerminationAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer<?> createAnswer(final int commandCode, final int applicationId,
                                          final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return switch (commandCode) {
            case DiameterConstants.CMD_ABORT_SESSION ->
                    new AbortSessionAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_ACCOUNTING ->
                    new AccountingAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_CAPABILITIES_EXCHANGE ->
                    new CapabilitiesExchangeAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_DEVICE_WATCHDOG ->
                    new DeviceWatchdogAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_DISCONNECT_PEER ->
                    new DisconnectPeerAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_RE_AUTH ->
                    new ReAuthAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_SESSION_TERMINATION ->
                    new SessionTerminationAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };
    }
}
```

## Constructor visibility

All 21 constructors below are widened from `private` to package-private (remove the
`private` keyword). No other access modifier changes are made.

| Class | Constructor signature |
|---|---|
| `AbortSessionRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `AbortSessionAnswer.In` | `(HopByHopId, EndToEndId)` |
| `AbortSessionAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `AccountingRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `AccountingAnswer.In` | `(HopByHopId, EndToEndId)` |
| `AccountingAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `CapabilitiesExchangeRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `CapabilitiesExchangeAnswer.In` | `(HopByHopId, EndToEndId)` |
| `CapabilitiesExchangeAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `DeviceWatchdogRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `DeviceWatchdogAnswer.In` | `(HopByHopId, EndToEndId)` |
| `DeviceWatchdogAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `DisconnectPeerRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `DisconnectPeerAnswer.In` | `(HopByHopId, EndToEndId)` |
| `DisconnectPeerAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `ReAuthRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `ReAuthAnswer.In` | `(HopByHopId, EndToEndId)` |
| `ReAuthAnswer.Out` | `(HopByHopId, EndToEndId)` |
| `SessionTerminationRequest.In` | `(HopByHopId, EndToEndId, boolean)` |
| `SessionTerminationAnswer.In` | `(HopByHopId, EndToEndId)` |
| `SessionTerminationAnswer.Out` | `(HopByHopId, EndToEndId)` |

## DiameterMessageFactory — factory discovery

`DiameterMessageFactory` owns the mutable list of factories. The list is populated at
startup via a reflections scan, and can be extended at runtime via a static `register`
method.

```java
static final List<DiameterPackageFactory> FACTORIES;

static {
    final Reflections reflections = new Reflections("com.sipgate.sparta.diameter");
    final List<DiameterPackageFactory> discovered = new ArrayList<>();
    for (final Class<? extends DiameterPackageFactory> cls :
             reflections.getSubTypesOf(DiameterPackageFactory.class)) {
        /* instantiate via no-arg constructor and add to discovered */
    }
    FACTORIES = discovered; // kept mutable to allow register()
}

/**
 * Registers an additional factory at runtime. Intended for embedders that
 * add a Diameter application module after startup. Thread-safety is the
 * caller's responsibility; call before any message is parsed.
 */
public static void register(final DiameterPackageFactory factory) {
    FACTORIES.add(factory);
}
```

`createForParsing` (public, used by tests and `Command`) iterates `FACTORIES` and
delegates to the first factory returning non-null, throwing `IllegalArgumentException`
if none handles the combination.

`createAnswer` iterates `FACTORIES` for a non-null `createAnswer` result, then applies
the existing identifier-copy and result-code logic.

## Command — parseMessage

`PACKAGES_TO_SCAN`, `REQUEST_TYPES`, `ANSWER_TYPES`, and `initializeCommandTypes()` are
deleted.

`parseMessage` calls `DiameterMessageFactory.createForParsing` and uses the returned
value directly — no null check needed, because `DiameterMessageFactory` already throws
`IllegalArgumentException` when no factory matches.

The three private `instantiateIn*` / `instantiateOutAnswer` methods and the
`findOutClass` helper are deleted — construction is now handled entirely by the factories
via direct constructor calls.

## Annotations

`@DiameterRequest` and `@DiameterResponse` are removed from every `In` class. The two
annotation types (`DiameterRequest.java`, `DiameterResponse.java`) are deleted. The
`core.annotations` package is left empty and can be deleted along with them.

## Test impact

`DiameterMessageFactory.createForParsing` remains public; `Command.parseMessage`
behaviour is unchanged. The only observable difference is that `setAccessible` is no
longer called.

## BaseMessageFactoryTest

New test class `BaseMessageFactoryTest` in package
`com.sipgate.sparta.diameter.base.messages` (same package as the factory, so
package-private constructors are accessible).

The test uses `Reflections` to discover every concrete subtype of `IncomingRequest` and
`IncomingAnswer` under `com.sipgate.sparta.diameter.base.messages`. For each discovered
class it reads the command code from the superclass constructor argument (or from
`DiameterConstants` via the existing `commandCode()` accessor on a dummy instance), then
asserts that `BaseMessageFactory.createForParsing` returns a non-null result for that
command code.

```java
class BaseMessageFactoryTest {

    private static final HopByHopId HOP = new HopByHopId(0L);
    private static final EndToEndId END = new EndToEndId(0L);

    private final BaseMessageFactory factory = new BaseMessageFactory();

    @Test
    void it_handles_every_discovered_request_type() {
        // GIVEN
        final Reflections reflections =
            new Reflections("com.sipgate.sparta.diameter.base.messages");
        final Set<Class<? extends IncomingRequest>> requestTypes =
            reflections.getSubTypesOf(IncomingRequest.class);

        for (final Class<? extends IncomingRequest> cls : requestTypes) {
            // WHEN
            final IncomingRequest<?> instance =
                (IncomingRequest<?>) cls.getDeclaredConstructors()[0]
                    .newInstance(HOP, END, false);
            final IncomingCommand result = factory.createForParsing(
                instance.commandCode(), 0, true, HOP, END, false);

            // THEN
            assertThat(result)
                .as("factory must handle command code for %s", cls.getSimpleName())
                .isNotNull();
        }
    }

    @Test
    void it_handles_every_discovered_answer_type() {
        // GIVEN
        final Reflections reflections =
            new Reflections("com.sipgate.sparta.diameter.base.messages");
        final Set<Class<? extends IncomingAnswer>> answerTypes =
            reflections.getSubTypesOf(IncomingAnswer.class);

        for (final Class<? extends IncomingAnswer> cls : answerTypes) {
            // WHEN
            final IncomingAnswer<?> instance =
                (IncomingAnswer<?>) cls.getDeclaredConstructors()[0]
                    .newInstance(HOP, END);
            final IncomingCommand result = factory.createForParsing(
                instance.commandCode(), 0, false, HOP, END, false);

            // THEN
            assertThat(result)
                .as("factory must handle command code for %s", cls.getSimpleName())
                .isNotNull();
        }
    }
}
```

The constructors are called directly (no `setAccessible`) because the test is in the
same package.
