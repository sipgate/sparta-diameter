---
title: "ADR-0008: Logging API"
description: "Use SLF4J as the sole logging API; bind no implementation in library modules"
owner: "sipgate-uhlig"
status: accepted
tags:
  - logging
  - slf4j
created: "2026-03-27"
---

## Context

This project is a library. Application code that embeds it will already have a logging framework in place — typically Logback, Log4j 2, or java.util.logging. Bundling a concrete logging implementation inside the library would force that choice onto every consumer and risk duplicate or conflicting logging on the classpath.

SLF4J (Simple Logging Facade for Java) is the de-facto standard facade in the Java ecosystem. Logback was written by the SLF4J author and implements it natively. Log4j 2 ships a `log4j-slf4j2-impl` bridge. `java.util.logging` can be wired via `jul-to-slf4j`. Any consumer with any of these on their classpath gets working logs out of the box; consumers that want no logs ship `slf4j-nop`.

No logging calls exist in the codebase today. ADR-0007 requires logging on unexpected Java exceptions during decode/encode, making this decision necessary now.

## Decision

All logging in this project uses the SLF4J API (`org.slf4j:slf4j-api`).

Every class that emits log output declares a private static final field:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger LOGGER = LoggerFactory.getLogger(ContainingClass.class);
```

The field is always named `LOGGER`. The argument to `getLogger` must be the class that declares the field — never a supertype, subtype, or unrelated class.

No concrete logging implementation (`logback-classic`, `log4j-core`, `slf4j-simple`, etc.) is declared as a `compile` or `runtime` dependency in any library module. Test scope is exempt — `slf4j-simple` or a similar no-op binding may be declared `<scope>test</scope>` to silence "no SLF4J binding" warnings during unit test runs.

SLF4J parameterized placeholders (`{}`) only — no string concatenation in log calls.

> **Guardrail:** `System.out`, `System.err`, and `printStackTrace()` are banned in production code. All diagnostic output goes through SLF4J.


Empty catch blocks are banned. If an exception is caught and not re-thrown, at minimum log with the exception.

## Level Mapping

| Level | Use |
|---|---|
| `ERROR` | Write/encode failures — `IOException` or unexpected `Exception` during `writeTo()` or serialization |
| `WARN` | Unrecognized command or AVP codes received from the peer |
| `INFO` | Business-level results worth knowing — session lifecycle events, capability negotiation outcomes, non-2xxx result codes |
| `DEBUG` | Specific values in scope during processing — AVP names and values, peer addresses |
| `TRACE` | Branching decisions inside decode/encode logic |

## Density Rules

This is a library — every `WARN` and `ERROR` statement appears in the consumer's log unconditionally unless they explicitly silence the package. Treat higher levels as a cost to the consumer.

| Level | Expected density |
|---|---|
| `TRACE` | Rare — temporary diagnostic scaffolding introduced when chasing a specific bug; removed or kept only if it proved its worth |
| `DEBUG` | Rare — only where the informational value is clear and lasting; avoid cluttering the source |
| `INFO` | Sparse — at most once per message when a business-level error is confirmed |
| `WARN` | Rare — only genuinely unexpected peer behaviour the library can work around |
| `ERROR` | Exceptional — only when the library cannot recover |

## Consequences

- The library compiles and runs without imposing a logging backend on consumers.
- Consumers get structured, levelled logs for free by having any SLF4J-compatible backend on their classpath.
- All three cases in ADR-0007 (unknown command, invalid message, unexpected exception) can now emit diagnostic output.

## Related ADRs

- **Required by:** ADR-0007 (decode/encode error handling requires a logging mechanism)
