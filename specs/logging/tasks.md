# Logging — Tasks

## Infrastructure (safe to automate)

- [ ] Add `org.slf4j:slf4j-api` as a `compile`-scope dependency to `sparta-diameter-base/pom.xml`
- [ ] Add a test-scoped SLF4J binding (e.g. `slf4j-simple`) to `sparta-diameter-base/pom.xml` to suppress "no SLF4J binding" warnings during unit test runs
- [ ] Add `commandName()` default implementation to `Command` returning `getClass().getName()`
- [ ] Override `commandName()` in `GenericCommand` returning `"Unknown[code=" + getCommandCode() + "]"`

## Log call placement (human review required)

The density rules in `design.md` are intentionally tight. Automated placement will over-log. Place calls below, then review each one against the frequency table before committing.

- [ ] Add `LOGGER` declaration and log call in the unknown-command branch (ADR-0007 case 1) at `WARN`
- [ ] Add `LOGGER` declaration and log call on invalid/malformed message (ADR-0007 case 2) at `INFO`
- [ ] Add `LOGGER` declaration and log call on unexpected `Exception` during decode/encode (ADR-0007 case 3) at `ERROR` with cause

## Verification (safe to automate)

- [ ] Confirm no `System.out`, `System.err`, or `printStackTrace()` calls exist in any production source file
- [ ] Confirm no concrete SLF4J binding appears in `compile` or `runtime` scope in any library module

## Before cleanup

- [ ] Amend ADR-0008 with the level mapping table and density rules from `design.md`
- [ ] Write a new ADR for "never log and throw" (rejected alternative: log-and-throw causes duplicate entries up the call stack)
- [ ] Write a new ADR for "exception as trailing arg at ERROR only" (rejected alternative: passing cause at all levels)
- [ ] Write a new ADR for `commandName()` default on `Command` (rejected alternatives: abstract method forcing all subclasses to implement; annotation-based reflection; class name stripping)
- [ ] Delete `specs/logging/` per ADR-0002
