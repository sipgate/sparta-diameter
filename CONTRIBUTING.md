# Contributing

Thanks for your interest in Sparta Diameter. This document covers what you need to build the
project and what reviewers will look for.

## Build and test

Toolchain, build commands and the docker-based test peer are covered in the
[README](README.md#building). One addition for contributors: before opening a release-affecting
change, confirm the publishing profile still builds:

```shell
mvn -P deploy verify -Dgpg.skip=true
```

## Code conventions

[`AGENTS.md`](AGENTS.md) is the authoritative list and applies to human contributors too. The
short version:

- `final` on every field, constructor parameter, and local variable. No exceptions.
- For-loops in production code, not streams.
- Tests: method names `it_<describes_behavior>`, GIVEN/WHEN/THEN comment blocks, AssertJ
  assertions only, and the instance under test named by its role (`command`, `avp`, `decoder`)
  rather than something generic.
- Keep Javadoc in sync with any method or type you change.

### Message factories

A `DiameterPackageFactory` that dispatches application-specific command codes **must** check
`applicationId` first and return `null` when the app-id is not one it owns — command codes are
reused across 3GPP applications, and a factory that skips the check silently steals another
module's messages. [`AGENTS.md`](AGENTS.md) documents the required pattern and why the
base-protocol factory is the deliberate exception.

### Tests

Cover your change with tests; for bug fixes that means a regression test that reproduces the bug.
Not everything warrants a unit test — POJOs and configuration classes generally do not.

Formatting follows [`.editorconfig`](.editorconfig) (LF, UTF-8, 120 columns, 4-space Java) but is
not enforced by the build; please configure your editor to respect it.

## Architecture decisions and specs

- [`ADR/`](ADR) holds architecture decision records. Read the relevant ones before making
  structural changes. The format is defined in
  [ADR-0001](ADR/0001-adr-format.md); anything with a "Rejected alternatives" section belongs here.
- [`specs/`](specs) holds per-feature requirements for planned work, structured per
  [ADR-0002](ADR/0002-spec-driven-development.md). Load only the spec for the feature you are
  working on.

If your change is a trade-off at a crossroads, add an ADR alongside the code.

## Commits and pull requests

- Semantic commit subjects with a scope in brackets: `fix(transport): reset Tc timer on CEA`.
- Subject at most 50 characters, body wrapped at 72.
- Explain **why** in the body; the diff already shows what.
- Small, atomic commits. Each one should make sense on its own and leave the build working.

Open the pull request against `main`. CI runs `mvn clean verify` on Java 17, 21, 24 and 25, plus a
`-P deploy` build that produces the sources and javadoc jars — all must pass. Describe what changed and why, and reference the relevant ADR or spec if there is one.

## Reporting bugs

Use the issue templates. For decoding problems, a packet capture or hex dump of the offending
message is worth far more than a description of it. Please do not file security issues as public issues — see
[SECURITY.md](SECURITY.md).
