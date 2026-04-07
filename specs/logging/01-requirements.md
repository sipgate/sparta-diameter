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

## Human-readable names for protocol identifiers

Numeric identifiers — application IDs, command codes, and AVP codes — MUST NOT appear as bare magic numbers in log output. Every type that carries such an identifier MUST offer a way for logging code to retrieve a human-readable canonical name without reimplementing a lookup table.

The mechanism for exposing this name (interface method, annotation, reflection on the class name, or a combination) is intentionally left open and will be decided in the implementation spec.

### Scope

- **Protocols** — each type representing a Diameter application must be able to provide its canonical application name (e.g. `3GPP Sg/Gd/Dd`, `Diameter Base`).
- **Commands** — each Request, Answer, and Error type must be able to provide its canonical command name, without directional suffix (e.g. `Device-Watchdog`, `Capabilities-Exchange`).
- **AVPs** — each AVP type must be able to provide its canonical AVP name as defined by the relevant RFC or 3GPP specification (e.g. `Origin-Host`, `Auth-Application-Id`).

### Constraints

- The name MUST be available without any I/O — it is derived solely from the type definition.
- No production logging call MAY format a raw numeric code directly into a log message; it MUST use the name the type exposes.

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
