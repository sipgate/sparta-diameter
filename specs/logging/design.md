# Logging — Design

## Logger declaration

Every class that emits log output declares a private static final field:

```java
private static final Logger LOGGER = LoggerFactory.getLogger(SomeClass.class);
```

The field is always named `LOGGER`. No other name is acceptable.

## Level mapping

| Level | Use |
|---|---|
| `ERROR` | Write/encode failures — `IOException` or unexpected `Exception` during `writeTo()` or serialization |
| `WARN` | Unrecognized command or AVP codes received from the peer |
| `INFO` | Protocol-level business errors — peer sent a structurally invalid message; non-2xxx result code received |
| `DEBUG` | Specific values in scope during processing — AVP names and values, peer addresses |
| `TRACE` | Branching decisions inside decode/encode logic |

## Message format

SLF4J parameterized placeholders (`{}`) only. No string concatenation.

```java
LOGGER.warn("Unknown command: {}", command.commandName());
LOGGER.error("Failed to encode message: {}", e.getMessage(), e);
```

**Never log and throw.** Either log the problem and handle it, or throw and let the caller decide whether to log. Doing both produces duplicate entries up the call stack.

The cause exception is passed as the trailing argument **at `ERROR` level only**. At `WARN` and below the library has handled the situation; the stack trace is noise in the consumer's log. At `ERROR` the library has not recovered and the trace is required for diagnosis.

## Relative frequency

This is a library — every `WARN` and `ERROR` statement appears in the consumer's log unconditionally unless they explicitly silence the package. Treat higher levels as a cost to the consumer.

| Level | Expected density |
|---|---|
| `TRACE` | Rare — temporary diagnostic scaffolding introduced when chasing a specific bug; removed or kept only if it proved its worth |
| `DEBUG` | Rare — only where the informational value is clear and lasting; avoid cluttering the source |
| `INFO` | Sparse — at most once per message when a business-level error is confirmed |
| `WARN` | Rare — only genuinely unexpected peer behaviour the library can work around |
| `ERROR` | Exceptional — only when the library cannot recover |

## Identifying types in log output

### Commands — `commandName()`

`Command` provides a non-abstract default:

```java
public String commandName() {
    return getClass().getName();
}
```

This returns the fully-qualified class name (e.g. `com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest$In`), which is always correct, always unique, and grep-able in source. Concrete types may override it if a prettier name is warranted, but are not required to.

`GenericCommand` overrides to expose the only information it has:

```java
@Override
public String commandName() {
    return "Unknown[code=" + getCommandCode() + "]";
}
```

This is the sole permitted use of a raw numeric code in a log message, because no symbolic name exists for an unrecognized command.

### AVPs

AVP log output uses `AVPDefinition.name()` directly — the field already exists and is always populated. No wrapper or interface needed.

```java
LOGGER.debug("Processing AVP: {}", avp.definition().name());
```

If typed AVPs are introduced in the future, they may override a `avpName()` method on the `AVP` base class following the same pattern as `commandName()`.

## What does NOT change

- The ban on `System.out`, `System.err`, and `printStackTrace()` in production code is absolute.
- No concrete SLF4J binding is added to any library module's `compile` or `runtime` scope.
- Application-level semantics (which events are worth logging, what context to include beyond the message) remain the caller's concern and are deferred per requirements.
