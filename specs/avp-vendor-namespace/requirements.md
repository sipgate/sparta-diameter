# AVP Vendor Namespace — Requirements

## Goals

The AVP registry keys on code alone (`Map<Integer, AVPDefinition>`). RFC 6733 §4
defines the AVP code space as per-vendor: code 3300 under vendor 10415 and code
3300 under vendor 0 are distinct AVPs. The current design silently overwrites an
earlier registration when two AVPs share a code but differ in vendor ID, making
multi-vendor deployments unsafe.

The reflection-based provider discovery in `AVP`'s static initializer compounds
this: it calls `registry.put(definition.code(), definition)` without considering
vendor ID, so two providers that legitimately define the same code under different
vendors will silently collide at class-load time with no error surfaced.

## Acceptance criteria

- The registry key is the combination of AVP code and vendor ID; two
  registrations that share a code but differ in vendor ID coexist without either
  overwriting the other
- No public method may retrieve or set AVPs by integer code alone; every such
  method is removed or replaced
- Constructors and factory methods that identify an AVP by code must accept an
  `AVPKey`; vendor ID 0 is the correct value for IETF base AVPs (RFC 6733 §4),
  not null
- `AVPContainer.findAVP` and `findAVPs` are updated to accept `AVPKey`; the
  integer-only overloads are removed
- `AVP.readFrom(ByteBuffer)` uses the V-flag and the wire-level vendor ID field
  to construct the `AVPKey` for registry lookup; this is the only site where the
  vendor ID is sourced from the wire rather than supplied by the caller
- The reflection-based provider registration detects duplicate `(code, vendorId)`
  registrations across all discovered providers and throws `IllegalStateException`
  at class-load time naming the conflicting providers and the duplicate key
- Existing providers (`SgdGddAVPProvider`, `_3gppAVPProvider`, and any base
  provider) register without collision under this scheme
- All existing tests are updated to pass the vendor ID explicitly; no test
  relies on a code-only lookup
