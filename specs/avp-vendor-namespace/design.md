# AVP Vendor Namespace — Design

## Registry key and public API type

Replace `Map<Integer, AVPDefinition>` with `Map<AVPKey, AVPDefinition>` where
`AVPKey` is a **public** record:

```java
public record AVPKey(int code, int vendorId) {}
```

`AVPKey` is the single currency for identifying an AVP throughout the public API:
`AVP.create`, `AVPContainer.findAVP`, `AVPContainer.findAVPs`, and all mixin
getters and setters accept it instead of a bare `int` code. This makes swapping
the two integers a compile error at any call site, and makes the requirement to
supply a vendor ID impossible to ignore.

`record` provides correct `equals`/`hashCode` for free. The registry lookup stays
flat — `registry.get(key)` — identical complexity to today. Nested maps
(`Map<Integer, Map<Integer, AVPDefinition>>`) are ruled out: they require a
two-level null-safe lookup at every call site, make iteration awkward, and offer
no natural duplicate-detection point.

## Duplicate detection in `registerProvider`

Replace `registry.put(…)` with `putIfAbsent`, treat a non-null return value as
a conflict, and throw `IllegalStateException` immediately with the duplicate key
and the names of both the incumbent and the challenger `AVPDefinition`.

## IETF base AVPs (vendor ID 0)

AVPs defined in RFC 6733 carry no vendor ID on the wire (`vendorSpecific=false`,
`vendorId=0`). They are registered and looked up under `AVPKey(code, 0)`, which
is consistent with the general scheme and requires no special casing.
