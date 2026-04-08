---
title: "ADR-0013: getCommandName() Default on Command"
description: "Command.getCommandName() returns getClass().getName(); GenericCommand overrides with the raw code"
owner: "sipgate-uhlig"
status: accepted
tags:
  - logging
  - commands
created: "2026-04-07"
---

## Context

Log output must not contain bare numeric command codes (per ADR-0008). Every `Command` subtype needs a way to provide a human-readable name that logging code can call without knowing the concrete type.

The mechanism must satisfy two constraints:

1. **Always available** — no I/O, no registry lookup, no null checks.
2. **Always correct** — must never silently return a wrong name because a subclass forgot to implement something.

## Decision

`Command` provides a non-abstract default:

```java
public String getCommandName() {
    return getClass().getName();
}
```

This returns the fully-qualified class name (e.g. `com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest$In`). It is always correct, always unique, and grep-able in source. Concrete types may override it when a prettier name is warranted, but are not required to.

`GenericCommand` overrides to expose the only information it has — the raw wire code — because no symbolic name exists for an unrecognised command:

```java
@Override
public String getCommandName() {
    return "Unknown[code=" + getCommandCode() + "]";
}
```

The raw code is acceptable here specifically because there is no symbolic name to use. This is the sole exception to the no-bare-numeric-codes rule from ADR-0008.

## Rejected Alternatives

**Abstract method** — forces every concrete subtype to implement `getCommandName()` at compile time. Rejected because the default (class name) is already a valid, unique identifier. Requiring every subclass to supply a custom string adds boilerplate with no safety gain: a subclass that blindly returns `""` compiles fine and is worse than the default.

**Annotation-based reflection** — place a `@CommandName("Device-Watchdog")` annotation on each class; `getCommandName()` reads it via reflection. Rejected because reflection adds runtime cost and complexity, and the annotation can go stale without a compile-time check.

**Class name stripping** — derive the pretty name by stripping package prefix and inner-class suffix from `getClass().getSimpleName()`. Rejected because the transformation is fragile: it relies on naming conventions enforced only by code review, and the result (e.g. `DeviceWatchdogRequestIn`) is less readable than the FQN is grep-able.

## Consequences

- Every `Command` subtype can report its name to logging code without additional implementation work.
- Protocol-specific modules may override `getCommandName()` to return RFC/3GPP names when the added readability is worth it.
- Unrecognised commands (via `GenericCommand`) expose the raw code, which is the only meaningful identifier available.

## Related ADRs

- **See also:** ADR-0008 (logging API; ban on bare numeric codes in log output)
