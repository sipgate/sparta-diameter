---
title: "ADR-0012: Exception as Trailing Arg at ERROR Only"
description: "Pass the cause exception as SLF4J trailing argument only at ERROR level"
owner: "sipgate-uhlig"
status: accepted
tags:
  - logging
  - exceptions
created: "2026-04-07"
---

## Context

SLF4J detects a `Throwable` as the last argument to a log call and appends its full stack trace to the log entry. A stack trace can be dozens of lines. In a library, those lines appear in every consumer's log, unconditionally.

At `ERROR` level the library cannot recover. The stack trace is required for diagnosis because the caller needs to understand exactly where and why the failure occurred.

At `WARN` and below the library has handled the situation. The protocol event is worth noting, but the stack trace is noise — the consumer did not ask for it and cannot act on it.

## Decision

The cause exception is passed as the trailing argument **at `ERROR` level only**.

```java
// ERROR — include the cause; stack trace required for diagnosis
LOGGER.error("Failed to encode message: {}", e.getMessage(), e);

// WARN and below — message only; stack trace is noise
LOGGER.warn("Unrecognized command code: {}", commandCode);
```

## Rejected Alternatives

**Pass cause at all levels** — maximizes information in the log. Rejected because at `WARN` and below the library has recovered and the trace adds no actionable information; it floods the consumer's log for every peer behavior anomaly.

**Never pass the cause** — avoids trace noise entirely. Rejected at `ERROR` level because without the stack trace a library-level failure that cannot be reproduced from the log message alone becomes undiagnosable.

## Consequences

- `ERROR` entries include the full stack trace; consumers can diagnose unrecoverable failures without additional instrumentation.
- `WARN` and below are single-line entries; consumers are not punished with trace noise for handled anomalies.

## Related ADRs

- **See also:** ADR-0008 (logging API, level mapping, density rules)
