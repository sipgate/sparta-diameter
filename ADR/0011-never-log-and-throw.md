---
title: "ADR-0011: Never Log and Throw"
description: "Either log the problem or throw; never both in the same catch block"
owner: "sipgate-uhlig"
status: accepted
tags:
  - logging
  - exceptions
created: "2026-04-07"
---

## Context

When exception handling code both logs a message and re-throws (or wraps and throws), the same event appears multiple times in the consumer's log — once at the point of first catch, and again wherever the exception eventually surfaces. In a library, this is especially harmful: the library has no visibility into how many layers of catch/log/rethrow exist in the application stack.

## Decision

A catch block either logs the problem and handles it, or throws (re-throws or wraps) and lets the caller decide whether to log. Never both.

```java
// Correct — log and handle
try {
    decode(buffer);
} catch (final IOException e) {
    LOGGER.error("Failed to decode message: {}", e.getMessage(), e);
    // handle: return fallback, close connection, etc.
}

// Correct — throw and let caller log
try {
    decode(buffer);
} catch (final IOException e) {
    throw new DiameterException("Failed to decode message", e);
}

// Wrong — log and throw produces duplicate entries up the call stack
try {
    decode(buffer);
} catch (final IOException e) {
    LOGGER.error("Failed to decode message: {}", e.getMessage(), e);
    throw new DiameterException("Failed to decode message", e); // duplicate
}
```

## Rejected Alternatives

**Log-and-throw** — the intuitive approach when you want both a local record and caller visibility. Rejected because it unconditionally writes to the consumer's log at the library level and then surfaces the same event again through the caller's error handling, resulting in repeated entries with no additional information.

## Consequences

- Log output is clean: each failure appears exactly once.
- The library does not impose a logging decision on callers that choose to handle exceptions silently.

## Related ADRs

- **See also:** ADR-0008 (logging API and level mapping)
