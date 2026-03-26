# Requirements: Multi-Module Maven Structure

Implements ADR-0003.

## Functional requirements

1. The project builds as a Maven monorepo with a root aggregator POM and six sub-modules:
   `sparta-diameter-base`, `sparta-diameter-3gpp-common`, `sparta-diameter-3gpp-s6c`,
   `sparta-diameter-3gpp-sgdgdd`, `sparta-diameter-3gpp-s6a`, `sparta-diameter-3gpp-cxdx`.

2. All modules share group ID `com.sipgate` and inherit their version from the root POM.

3. Dependency arrows point inward only:
   - `sparta-diameter-base` has no dependencies on the other modules.
   - `sparta-diameter-3gpp-common` depends on `sparta-diameter-base`.
   - `sparta-diameter-3gpp-s6c`, `sparta-diameter-3gpp-sgdgdd`, `sparta-diameter-3gpp-s6a`,
     and `sparta-diameter-3gpp-cxdx` each depend on `sparta-diameter-3gpp-common`.

4. All existing RFC 6733 code lives in `sparta-diameter-base`. The five 3GPP modules start
   empty (no production classes) but are present and compile cleanly.

5. `mvn verify` from the root passes with no test failures after the restructuring.

## Non-functional requirements

1. A consumer who needs only `sparta-diameter-sgdgdd` does not transitively acquire
   `sparta-diameter-s6c` — the four 3GPP interface modules have no dependency on each other.

2. Existing tests are not deleted or disabled; they move with their source classes.

## Out of scope

- Implementing any 3GPP AVPs or messages — the 3GPP modules are created as empty
  scaffolding only.
- Publishing to Maven Central or any remote repository.
