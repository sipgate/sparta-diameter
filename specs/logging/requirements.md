# Logging API — Requirements

## Context

The library currently has no logging calls. ADR-0007 requires diagnostic output on decode/encode failures, making a logging API decision necessary before that spec can be implemented. ADR-0008 documents the decision: SLF4J is the sole logging facade; no concrete implementation is bundled in library modules.

## SLF4J as the logging facade

- All diagnostic output in production code MUST go through the SLF4J API (`org.slf4j:slf4j-api`).
- `System.out`, `System.err`, and `printStackTrace()` are banned in production code.
- No concrete logging implementation (`logback-classic`, `log4j-core`, `slf4j-simple`, etc.) may be declared as a `compile` or `runtime` dependency in any library module.
- Test scope is exempt: a no-op or simple binding MAY be declared `<scope>test</scope>` to suppress "no SLF4J binding" warnings during unit test runs.

## Logger declaration

Each class that emits log output declares a static final logger:

```java
private static final Logger log = LoggerFactory.getLogger(SomeClass.class);
```

## Deferred decisions

The following are intentionally out of scope for this spec and will be addressed in a follow-up once usage patterns emerge:

- Which events at the library level are worth logging versus delegating to the application layer.
- Log level mapping (TRACE / DEBUG / INFO / WARN / ERROR) for protocol events.
- Which fields (hop-by-hop ID, peer address, command code, …) belong in every log statement.

## Acceptance criteria

- `org.slf4j:slf4j-api` is declared as a `compile`-scope dependency in `sparta-diameter-base`.
- No concrete SLF4J binding is present in any library module's `compile` or `runtime` scope.
- A test-scoped binding (e.g. `slf4j-simple`) is added to suppress warnings during unit test runs.
- No `System.out`, `System.err`, or `printStackTrace()` calls exist in any production source file.
