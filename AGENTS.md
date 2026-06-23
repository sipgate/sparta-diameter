# Project Conventions

Java 17 codebase (`17.0.18-zulu` via `.sdkmanrc`).

## Build

Use `mvn` directly — no Maven wrapper (`mvnw`) in this project.

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

## Diameter Message Factories

A `DiameterPackageFactory` dispatches an incoming message by `(commandCode, applicationId)`. `DiameterMessageFactory` iterates every registered factory and the **first non-null result wins** — factory order is not guaranteed (discovered via `reflections`), so two factories must never both claim the same `(code, app-id)`.

**Rule:** a factory that dispatches by command-code MUST check `applicationId` first and return `null` if the app-id is not one it owns. Command codes are NOT globally unique across applications: 3GPP apps share codes across app-ids (Cx/Dx app-id `16777216` and SWx app-id `16777265` both use MAR 303 / SAR 301 / RTR 304 / PPR 305). A factory that ignores `applicationId` will steal another application's messages and return the wrong type, surfacing as a `ClassCastException` only when both modules are on the classpath — invisible in per-module tests (the SWx module has no `cxdx` dependency, so the bug never fired in its own tests).

**Required pattern** (see `SwxMessageFactory`, `CxDxMessageFactory`):

```java
public IncomingCommand createForParsing(final int commandCode, final int applicationId, ...) {
    if (applicationId != XxConstants.APP_ID_XX) {
        return null;
    }
    return switch (commandCode) { ... };
}
```

Apply the same guard in `createAnswer`.

**Scope of the rule:** every factory whose command codes are application-specific (Cx/Dx, SWx, S6a, SgdGdd, …). The base-protocol factory is the documented exception, not an example to copy: CER/CEA, DWR/DWA, DPR/DPA are app-id 0, while ASR/ASA, RAR/RAA, STR/STA, ACR/ACA are application-agnostic per RFC 6733 (valid with any app-id) — do not blanket-check it without verifying those semantics.

**Review check:** for any new or changed factory, ask "does this check app-id before matching command-code?". If it does not and the codes are app-specific, it is a latent bug — even if no other application currently shares the codes.
