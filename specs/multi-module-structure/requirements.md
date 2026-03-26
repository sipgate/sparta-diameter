# Requirements: Multi-Module Maven Structure

Implements ADR-0003.

## Functional requirements

1. The project builds as a Maven monorepo with a root aggregator POM and four sub-modules:
   `sparta-diameter-base`, `sparta-diameter-3gpp-common`, `sparta-diameter-3gpp-s6c`,
   `sparta-diameter-3gpp-sgdgdd`.

2. All modules share group ID `com.sipgate.sparta` and version `0.1.0-SNAPSHOT`.

3. Dependency arrows point inward only:
   - `sparta-diameter-base` has no dependencies on the other three modules.
   - `sparta-diameter-3gpp-common` depends on `sparta-diameter-base`.
   - `sparta-diameter-3gpp-s6c` and `sparta-diameter-3gpp-sgdgdd` each depend on
     `sparta-diameter-3gpp-common`.

4. All existing RFC 6733 code lives in `sparta-diameter-base`. The three 3GPP modules start
   empty (no production classes) but are present and compile cleanly.

5. `mvn verify` from the root passes with no test failures after the restructuring.

## Non-functional requirements

1. A consumer who needs only `sparta-diameter-sgdgdd` does not transitively acquire
   `sparta-diameter-s6c` — the two 3GPP interface modules have no dependency on each other.

2. Existing tests are not deleted or disabled; they move with their source classes.

## Out of scope

- Implementing any 3GPP AVPs or messages — the 3GPP modules are created as empty
  scaffolding only.
- Publishing to Maven Central or any remote repository.
