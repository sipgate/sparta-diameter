# Design: Multi-Module Maven Structure

## Module layout

```
sparta-diameter/                    ← root aggregator POM (packaging = pom)
├── sparta-diameter-base/           ← RFC 6733 base protocol
├── sparta-diameter-3gpp-common/    ← shared 3GPP AVPs (empty initially)
├── sparta-diameter-3gpp-s6c/       ← TS 29.338 §5, S6c (empty initially)
├── sparta-diameter-3gpp-sgdgdd/    ← TS 29.338 §6, SGd/Gdd (empty initially)
├── sparta-diameter-3gpp-s6a/       ← TS 29.272, S6a/S6d (empty initially)
└── sparta-diameter-3gpp-cxdx/      ← TS 29.228/29.229, Cx/Dx (empty initially)
```

## Root POM

The root POM becomes an aggregator (no source, `<packaging>pom</packaging>`). It:

- Declares all six modules in `<modules>`.
- Keeps the group ID `com.sipgate` and artifact ID `sparta-diameter` unchanged.
- Sub-modules declare no `<version>` — they inherit it from the parent block.
- Centralises shared dependency versions in `<dependencyManagement>` and shared plugin
  config in `<pluginManagement>` so sub-module POMs stay thin.

## Package rename

ADR-0003 gives each module its own Java package root (e.g. `sparta.diameter.base`,
`sparta.diameter._3gpp.common`). Using the project's existing `com.sipgate.` prefix,
the full roots are:

| Module | Current packages | New package root |
|---|---|---|
| `sparta-diameter-base` | `com.sipgate.sparta.diameter.*` | `com.sipgate.sparta.diameter.base` |
| `sparta-diameter-3gpp-common` | — | `com.sipgate.sparta.diameter._3gpp.common` |
| `sparta-diameter-3gpp-s6c` | — | `com.sipgate.sparta.diameter._3gpp.s6c` |
| `sparta-diameter-3gpp-sgdgdd` | — | `com.sipgate.sparta.diameter._3gpp.sgdgdd` |
| `sparta-diameter-3gpp-s6a` | — | `com.sipgate.sparta.diameter._3gpp.s6a` |
| `sparta-diameter-3gpp-cxdx` | — | `com.sipgate.sparta.diameter._3gpp.cxdx` |

This means existing classes move from e.g. `com.sipgate.sparta.diameter.core` to
`com.sipgate.sparta.diameter.base.core`. This is a **breaking API change**; it is
acceptable at version `0.1.0-SNAPSHOT` with no external consumers yet.

All `import` statements and `package` declarations in both production and test sources
must be updated as part of this task.

## sparta-diameter-base source allocation

All 106 current production classes and 13 test classes move to `sparta-diameter-base`
unchanged (only their package declaration and any cross-package imports are updated):

| Current package | New package |
|---|---|
| `com.sipgate.sparta.diameter` | `com.sipgate.sparta.diameter.base` |
| `com.sipgate.sparta.diameter.core` | `com.sipgate.sparta.diameter.base.core` |
| `com.sipgate.sparta.diameter.core.annotations` | `com.sipgate.sparta.diameter.base.core.annotations` |
| `com.sipgate.sparta.diameter.core.avp` | `com.sipgate.sparta.diameter.base.core.avp` |
| `com.sipgate.sparta.diameter.core.avp.mixins` | `com.sipgate.sparta.diameter.base.core.avp.mixins` |
| `com.sipgate.sparta.diameter.messages.rfc6733` | `com.sipgate.sparta.diameter.base.messages` |
| `com.sipgate.sparta.diameter.session` | `com.sipgate.sparta.diameter.base.session` |
| `com.sipgate.sparta.diameter.transport` | `com.sipgate.sparta.diameter.base.transport` |

## Empty 3GPP module scaffolding

Each of the five 3GPP modules needs only a `pom.xml` with the correct coordinates and a
single `<dependency>` on its upstream module. No source directories are created — Maven
does not require them to exist when there are no sources to compile.

## Dependency versions

The root POM's `<dependencyManagement>` block pins all versions currently declared in the
flat POM. Sub-module POMs reference dependencies without version attributes.

Current dependencies to centralise:

| Dependency | Version | Scope |
|---|---|---|
| `org.reflections:reflections` | `0.10.2` | compile |
| `io.netty:netty-all` | `4.2.10.Final` | compile |
| `org.junit.jupiter:junit-jupiter` | `6.0.3` | test |
| `org.assertj:assertj-core` | `3.27.7` | test |
| `org.mockito:mockito-junit-jupiter` | `5.23.0` | test |

Only `sparta-diameter-base` needs the `reflections` and `netty-all` compile dependencies.
The 3GPP modules inherit them transitively via the base dependency and do not re-declare
them.
