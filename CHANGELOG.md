# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- CI: a snapshot push to `main` could cancel the tag-triggered Maven Central
  release publish before it ran; the publish workflows now use separate
  concurrency groups.

### Changed

- Dependency updates: Netty, Jackson, Micrometer and Apache POI.

## [0.1.13] - 2026-08-17

### Added

- SWx module (TS 29.273) with message factories guarded on the application id,
  so applications sharing command codes cannot claim each other's messages.
- Rx AVPs split out of the S6a module into `sparta-diameter-3gpp-rx`.
- Mobile IPv6 split scenario (RFC 5447) completed as a spec lib.
- All mandatory AVPs of the implemented interfaces are modeled and verified
  by tests.
- Factory method for error answers carrying an Experimental-Result.
- S6a AVPs: IMEI, QoS-Subscribed, Immediate-Response-Preferred and more.
- The spec extractor handles the Gi/Sgi and Rx spec tables.

### Fixed

- Vendor-specific application ids are preserved when creating answers.
- Grouped AVP values added after construction are all serialized.
- Origin AVPs are no longer duplicated when they are already set.
- Auth-Session-State is mirrored correctly from request to answer.
- Decoder and AVP parse failures keep the offending message and cause;
  unrecognized AVPs are logged instead of dropped silently.
- Experimental-Result answers no longer set the E-bit.
- Mandatory AVP flags corrected against the specs (S6a, Gx, Rx).

### Changed

- Dependabot keeps dependencies current; PRs are verified on every supported
  JDK; publishing no longer depends on internal infrastructure; contributor
  and security guides added.

## [0.1.12] - 2026-06-03

### Added

- S6a/S6d (TS 29.272): all commands and AVPs implemented.
- AVP-only modules extracted from the specs: Gx, S6t, SLh (3GPP), DOIC
  (RFC 7683), Load (RFC 8583), Mobile IPv6 integrated (RFC 5778) and split
  (RFC 5447), NAS (RFC 7155), and ETSI E2 (ES 283 035).
- Cx/Dx AVP definitions extracted from the specification.

### Fixed

- Spec table parsing and extraction: RFC 4005 tables, NOTE column headers,
  separation lines between AVPs, value types, and a forbidden V flag now
  yields vendor id 0.

## [0.1.11] - 2026-06-01

### Added

- Spec-driven verification: new modules `sparta-diameter-spec` (parses
  Command Code Format tables per RFC 6733 §3.2) and
  `sparta-diameter-spec-extractor`; tests now verify the AVPs and commands
  of the base protocol, Cx/Dx, S6c, S6m/S6n, SGd/Gdd and DRMP (RFC 7944)
  against the specs.

### Fixed

- Command codes, AVP codes and vendor ids are handled as unsigned values.
- Missing AVPs and wrong mandatory flags found by the spec tests
  (e.g. SGSN-Number is mandatory).

## [0.1.10] - 2026-04-29

### Changed

- AVPs shared across interfaces moved to common mixin interfaces.

## [0.1.9] - 2026-04-22

### Added

- Metrics for listen and connect errors.

## [0.1.8] - 2026-04-22

### Fixed

- Reconnect fires after a cancelled connection, and after an initial
  connection attempt that never succeeded.

## [0.1.7] - 2026-04-21

### Changed

- Grouped AVP values are easier to set.

### Fixed

- The short-hand AVP constructor sets the vendor id.

## [0.1.6] - 2026-04-20

### Fixed

- The Auth-Session-State AVP is always appended to SGd/Gdd messages.

### Changed

- Dropped the `Selfable<T>` mixin, which triggered a memory leak in javac.

## [0.1.5] - 2026-04-20

### Fixed

- Requests that require a Session-Id AVP now carry one.

## [0.1.4] - 2026-04-17

### Added

- Metrics distinguish request errors from handler errors.

### Fixed

- Possible null-pointer exceptions in error/cause handling.

## [0.1.3] - 2026-04-17

### Changed

- The peer state getter is now public.

### Fixed

- Origin-Host and Origin-Realm are always set on outgoing commands.

## [0.1.2] - 2026-04-13

### Fixed

- Micrometer gauges sharing a name now share the same tag set.

## [0.1.1] - 2026-04-08

### Added

- Logging across the library, including CER/CEA answers and application
  negotiation diagnostics.

### Fixed

- The Experimental-Result code is an unsigned 32-bit value.

## [0.1.0] - 2026-04-08

### Added

- Initial release: Diameter base protocol per RFC 6733 — binary parsing and
  serialization with a typed AVP API, message auto-discovery, peer lifecycle
  management (CER/CEA, DWR/DWA, DPR/DPA) including Tc reconnect handling,
  Netty-based transport for client and server roles, request handlers with
  timeouts, E-bit error answers and Micrometer metrics.
- Modules for the 3GPP interfaces Cx/Dx, S6a/S6d, S6c and SGd/Gdd and for
  IETF DRMP; SGd/Gdd commands and AVPs fully implemented.
- Architecture decision records (`ADR/`) and per-feature specs (`specs/`).

[unreleased]: https://github.com/sipgate/sparta-diameter/compare/0.1.13...HEAD
[0.1.13]: https://github.com/sipgate/sparta-diameter/compare/0.1.12...0.1.13
[0.1.12]: https://github.com/sipgate/sparta-diameter/compare/0.1.11...0.1.12
[0.1.11]: https://github.com/sipgate/sparta-diameter/compare/0.1.10...0.1.11
[0.1.10]: https://github.com/sipgate/sparta-diameter/compare/0.1.9...0.1.10
[0.1.9]: https://github.com/sipgate/sparta-diameter/compare/0.1.8...0.1.9
[0.1.8]: https://github.com/sipgate/sparta-diameter/compare/0.1.7...0.1.8
[0.1.7]: https://github.com/sipgate/sparta-diameter/compare/0.1.6...0.1.7
[0.1.6]: https://github.com/sipgate/sparta-diameter/compare/0.1.5...0.1.6
[0.1.5]: https://github.com/sipgate/sparta-diameter/compare/0.1.4...0.1.5
[0.1.4]: https://github.com/sipgate/sparta-diameter/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/sipgate/sparta-diameter/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/sipgate/sparta-diameter/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/sipgate/sparta-diameter/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/sipgate/sparta-diameter/compare/20395b6...0.1.0
