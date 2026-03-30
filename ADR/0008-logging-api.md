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

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(SomeClass.class);
```

No concrete logging implementation (`logback-classic`, `log4j-core`, `slf4j-simple`, etc.) is declared as a `compile` or `runtime` dependency in any library module. Test scope is exempt — `slf4j-simple` or a similar no-op binding may be declared `<scope>test</scope>` to silence "no SLF4J binding" warnings during unit test runs.

> **Guardrail:** `System.out`, `System.err`, and `printStackTrace()` are banned in production code. All diagnostic output goes through SLF4J.

## Open Questions

The following decisions are deferred until there is enough operational experience to make them non-arbitrarily:

- **When to log** — which events at the library level are worth recording vs. delegating entirely to the application layer?
- **Which level to use** — mapping of protocol events (decode failures, unknown commands, connection close) to TRACE / DEBUG / INFO / WARN / ERROR.
- **What to include** — which fields (hop-by-hop ID, peer address, command code, …) belong in every log statement for effective tracing?

A follow-up ADR (or an amendment to this one) will codify these once patterns emerge from real usage.

## Consequences

- The library compiles and runs without imposing a logging backend on consumers.
- Consumers get structured, levelled logs for free by having any SLF4J-compatible backend on their classpath.
- All three cases in ADR-0007 (unknown command, invalid message, unexpected exception) can now emit diagnostic output.

## Related ADRs

- **Required by:** ADR-0007 (decode/encode error handling requires a logging mechanism)
