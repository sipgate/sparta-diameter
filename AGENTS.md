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

## Package Structure

Organized by protocol domain:

```
com.sipgate.sparta.diameter.core
com.sipgate.sparta.diameter.core.avp
com.sipgate.sparta.diameter.messages.rfc6733
com.sipgate.sparta.diameter.transport
```
