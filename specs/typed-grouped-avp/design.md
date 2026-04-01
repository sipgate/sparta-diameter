# Typed Grouped AVP — Design

## GroupedAVP as AVPContainer

`GroupedAVP` already has `addAVP`, `findAVP`, and `findAVPs`. It gains `setAVP`
(replace-or-add by key, mirroring `Command`) and implements `AVPContainer<GroupedAVP>`.
This makes the full mixin interface hierarchy available to `GroupedAVP` and every subclass.

## Typed subclasses

A typed subclass extends `GroupedAVP` and implements the relevant mixin interfaces:

```java
public class UserIdentifierAVP extends GroupedAVP
        implements HasImsiAVP<UserIdentifierAVP>,
                   HasMsisdnAVP<UserIdentifierAVP>,
                   HasExternalIdentifierAVP<UserIdentifierAVP> {

    public UserIdentifierAVP(final int code, final boolean vendorSpecific,
                              final boolean mandatory, final boolean protectedAVP,
                              final int vendorId, final List<AVP> avps) {
        super(code, vendorSpecific, mandatory, protectedAVP, vendorId, avps);
    }
}
```

The constructor is a pure pass-through to `super`. All typed subclasses must expose
this exact six-argument signature — it is the contract the decoder relies on.

Update the class declaration once `HasLmsiAVP` exists:

```java
public class UserIdentifierAVP extends GroupedAVP
        implements HasUserNameAVP<UserIdentifierAVP>,
                   HasMsisdnAVP<UserIdentifierAVP>,
                   HasExternalIdentifierAVP<UserIdentifierAVP>,
                   HasLmsiAVP<UserIdentifierAVP> { ... }
```

## Decoder change — AVP.readFrom()

The grouped-AVP branch in `AVP.readFrom()` currently hardcodes `new GroupedAVP(...)`.
Two changes:

1. The condition broadens from `equals(GroupedAVP.class)` to
   `GroupedAVP.class.isAssignableFrom(definition.dataType())`, so registered subclasses
   are also caught.

2. Instantiation uses reflection on `definition.dataType()` with the six-argument
   constructor, producing whichever class is registered:

```java
if (definition != null && GroupedAVP.class.isAssignableFrom(definition.dataType())) {
    final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
    final List<AVP> nestedAVPs = new ArrayList<>();
    while (dataBuffer.remaining() >= 8) {
        nestedAVPs.add(readFrom(dataBuffer));
    }
    try {
        final var ctor = definition.dataType().getDeclaredConstructor(
            int.class, boolean.class, boolean.class, boolean.class, int.class, List.class);
        return (GroupedAVP) ctor.newInstance(
            code, vendorSpecific, mandatory, protectedAVP, vendorId, nestedAVPs);
    } catch (final ReflectiveOperationException e) {
        throw new IllegalStateException(
            "Typed GroupedAVP subclass " + definition.dataType().getSimpleName()
            + " does not expose the required six-argument constructor", e);
    }
}
```

For plain `GroupedAVP` entries the path is identical to today — reflection just
instantiates `GroupedAVP` itself.

## AVPDefinition registration

Typed grouped AVPs are registered with the concrete subclass as `dataType`:

```java
new AVPDefinition(AVP_USER_IDENTIFIER, "User-Identifier", UserIdentifierAVP.class,
                  true, true, VENDOR_ID_3GPP)
```

Plain grouped AVPs continue to use `GroupedAVP.class`.

## UserIdentifierAVP mixin list

`User-Identifier` (TS 29.336) is a sum type — exactly one of the following is present:

| Mixin | AVP | Source |
|---|---|---|
| `HasUserNameAVP` | User-Name (1, 0) | RFC 6733 — already exists in base |
| `HasMsisdnAVP` | MSISDN (701, 10415) | TS 29.329 — already exists in `_3gpp-common` |
| `HasExternalIdentifierAVP` | External-Identifier (3111, 10415) | TS 29.336 — already exists in `_3gpp-common` |
| `HasLmsiAVP` | LMSI (2400, 10415) | TS 29.173 — mixin and constant missing, must be added |

`AVP_LMSI = 2400` must be added to `_3gppConstants` and `HasLmsiAVP` created before
`UserIdentifierAVP` can be fully implemented.

## Mixin getter return types

`HasUserIdentifierAVP.getUserIdentifier()` returns `UserIdentifierAVP` instead of
`GroupedAVP`. Because `readFrom()` now produces the typed instance, the cast in the
getter is safe:

```java
default UserIdentifierAVP getUserIdentifier() {
    final var avp = findAVP(new AVPKey(AVP_USER_IDENTIFIER, VENDOR_ID_3GPP));
    return avp instanceof final UserIdentifierAVP typed ? typed : null;
}
```

## Write path

`HasUserIdentifierAVP.setUserIdentifier()` accepts `UserIdentifierAVP`, not the base
`GroupedAVP`. The typed parameter is the point — passing an arbitrary `GroupedAVP` to
a User-Identifier setter is a bug the compiler should catch:

```java
default T setUserIdentifier(final UserIdentifierAVP value) {
    setAVP(AVP.create(new AVPKey(AVP_USER_IDENTIFIER, VENDOR_ID_3GPP), value.getAVPs()));
    return self();
}
```

## Candidates for typed subclasses

All grouped AVPs already registered and already surfaced via a mixin, in order of
structural value:

| Typed class | AVP | Module | Inner structure |
|---|---|---|---|
| `UserIdentifierAVP` | User-Identifier (3102, 10415) | `_3gpp-common` | User-Name, MSISDN, External-Identifier, LMSI |
| `ServingNodeAVP` | Serving-Node (2401, 10415) | `_3gpp-common` | SGSN-Name/Realm/Number, MME-Name/Realm/Number-for-MT-SMS, MSC-Number, IP-SM-GW-Number/Name/Realm |
| `SmDeliveryOutcomeAVP` | SM-Delivery-Outcome (3316, 10415) | `_3gpp-common` | one of MME-, MSC-, SGSN-, IP-SM-GW-, SMSF-3GPP-, SMSF-Non-3GPP-SM-Delivery-Outcome |
| `SmDeliveryFailureCauseAVP` | SM-Delivery-Failure-Cause (3304, 10415) | `sgdgdd` | SM-Enumerated-Delivery-Failure-Cause + optional SM-Diagnostic-Info |
| `SmsMiCorrelationIdAVP` | SMSMI-Correlation-ID (3324, 10415) | `sgdgdd` | HSS-ID, Originating-SIP-URI, Destination-SIP-URI |
| `ProxyInfoAVP` | Proxy-Info (284, 0) | `base` | Proxy-Host + Proxy-State |
| `VendorSpecificApplicationIdAVP` | Vendor-Specific-Application-Id (260, 0) | `base` | Vendor-Id + Auth- or Acct-Application-Id |
| `ExperimentalResultAVP` | Experimental-Result (297, 0) | `base` | Vendor-Id + Experimental-Result-Code |

`Supported-Features` is excluded — it is not a sum type and its inner AVPs
(Feature-List-ID, Feature-List) are bitmask fields with application-specific semantics,
not generic accessors.

## What does not change

- The `AVPDefinition` record shape is unchanged; `dataType` already exists.
- All existing plain `GroupedAVP` registrations and their behaviour are unaffected.
- Mixin interfaces for Commands are unaffected.
- Grouped AVPs without a registered typed subclass continue to decode as plain `GroupedAVP`.
