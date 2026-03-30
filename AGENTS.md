# Project Conventions

Java 17 codebase (`17.0.18-zulu` via `.sdkmanrc`).

## Immutability

`final` on every field, constructor parameter, and local variable. No exceptions.

## Loops vs. Streams

For-loops only in production code.

## Tests

**Method naming:** `it_<describes_behavior>`

```java
@Test
void it_parses_a_DWR_from_binary_input() { ... }
```

**Instance under test:** named by role (`command`, `avp`, `decoder`), not a generic name.

**Structure:** GIVEN / WHEN / THEN comment blocks.

**Assertions:** AssertJ only.

## Javadoc

If a method or type you change has a Javadoc comment, update it to reflect the new state.

## Architecture Decisions

`ADR/` holds all architecture decision records. Read relevant ADRs before making structural changes.

## Feature Specifications

`specs/<feature-name>/` holds per-feature specs (requirements, design, tasks). Load only the spec for the feature you are working on. See ADR-0002 for the structure.

## Package Structure

Organized by protocol domain within each module:

```
com.sipgate.sparta.diameter.base.core
com.sipgate.sparta.diameter.base.core.avp
com.sipgate.sparta.diameter.base.messages
com.sipgate.sparta.diameter.base.transport
com.sipgate.sparta.diameter._3gpp.common
com.sipgate.sparta.diameter._3gpp.s6c
com.sipgate.sparta.diameter._3gpp.sgdgdd
com.sipgate.sparta.diameter._3gpp.s6a
com.sipgate.sparta.diameter._3gpp.cxdx
```
