# Cx/Dx Interface — Design

Mirrors the SGd/Gdd module pattern (`Constants` → `AVPProvider` → `messages/` →
`MessageFactory` → `mixins/`). Providers and factories are discovered automatically via the
Reflections scan over `com.sipgate.sparta.diameter` (`AVP` static init, `DiameterMessageFactory`
static init), so no manual registration is required — a module just needs to be on the classpath.

## Modules & dependencies

```
sparta-diameter-3gpp-cxdx        (new content; was an empty Constants stub)
  ├─ sparta-diameter-3gpp-common (Supported-Features, Feature-List(-ID))
  │    └─ sparta-diameter-base   (RFC 6733 AVPs, command base classes)
  ├─ sparta-diameter-ietf-drmp   (DRMP)
  ├─ sparta-diameter-ietf-radius-digest-authentication (new — RFC 5090 Digest-* AVPs)
  ├─ sparta-diameter-ietf-diameter-nas (new — RFC 7155 Framed-* AVPs)
  └─ sparta-diameter-etsi-e2     (new — ETSI ES 283 035 Line-Identifier AVP)
```

`sparta-diameter-3gpp-cxdx/pom.xml` currently depends only on `3gpp-common`. Add: `ietf-drmp`,
`ietf-radius-digest-authentication`, `ietf-diameter-nas`, `etsi-e2`, plus the `junit-jupiter` +
`assertj-core` test deps (mirroring `sgdgdd`/`drmp` poms). Register all new modules in the parent
`pom.xml` `<modules>` list.

## CxDxConstants

```
APP_ID_CX_DX = 16777216

// Command codes (TS 29.229 Table 6.1.1)
CMD_SERVER_ASSIGNMENT          = 301
CMD_MULTIMEDIA_AUTH            = 303
CMD_REGISTRATION_TERMINATION   = 304

// Cx/Dx-specific AVP codes (TS 29.229 Table 6.3.0.1) — see inventory below
AVP_PUBLIC_IDENTITY = 601 ... AVP_PCSCF_IP_ADDRESS = 666

// Experimental-Result values (TS 29.229 §6.2). 5001 is reused from _3gppConstants
//   (EXP_RES_DIAMETER_ERROR_USER_UNKNOWN). Defined here: successes 2001-2004,
//   permanent failures 5002-5012.
EXP_RES_DIAMETER_FIRST_REGISTRATION = 2001 ...
EXP_RES_DIAMETER_ERROR_SERVING_NODE_FEATURE_UNSUPPORTED = 5012

// Enumerated value sets: Server-Assignment-Type (0-14), Reason-Code (0-3),
//   User-Data-Already-Available (0-1), Multiple-Registration-Indication (0-1),
//   Loose-Route-Indication (0-1), Priviledged-Sender-Indication (0-1),
//   Session-Priority (0-4)
// Flag bit constants: SAR-Flags (bit0 P-CSCF-Restoration), RTR-Flags (bit0 Ref-Location-Change)
```

## AVP inventory — `cxdx` module (3GPP vendor 10415)

Type mapping: OctetString→`byte[]`, UTF8String/DiameterURI/DiameterIdentity→`String`,
Unsigned32→`Long`, Enumerated→`Integer`, Time→`Date`, Grouped→`GroupedAVP`, Address→`InetAddress`.

Flag column `M,V` ⇒ `AVPDefinition(..., mandatory=true,  vendorSpecific=true,  10415)`;
`V`   ⇒ `AVPDefinition(..., mandatory=false, vendorSpecific=true,  10415)`.

| Code | Name | Type | Flags | In groups / commands |
|---|---|---|---|---|
| 601 | Public-Identity | UTF8String | M,V | SAR,MAR,MAA,RTR; Identity-w-Emerg-Reg |
| 602 | Server-Name | UTF8String | M,V | SAR,SAA,MAR |
| 606 | User-Data | OctetString | M,V | SAA |
| 607 | SIP-Number-Auth-Items | Unsigned32 | M,V | MAR,MAA |
| 608 | SIP-Authentication-Scheme | UTF8String | M,V | SIP-Auth-Data-Item, SCSCF-Restoration-Info |
| 609 | SIP-Authenticate | OctetString | M,V | SIP-Auth-Data-Item |
| 610 | SIP-Authorization | OctetString | M,V | SIP-Auth-Data-Item |
| 611 | SIP-Authentication-Context | OctetString | M,V | SIP-Auth-Data-Item |
| 612 | SIP-Auth-Data-Item | Grouped | M,V | MAR,MAA |
| 613 | SIP-Item-Number | Unsigned32 | M,V | SIP-Auth-Data-Item |
| 614 | Server-Assignment-Type | Enumerated | M,V | SAR |
| 615 | Deregistration-Reason | Grouped | M,V | RTR |
| 616 | Reason-Code | Enumerated | M,V | Deregistration-Reason |
| 617 | Reason-Info | UTF8String | M,V | Deregistration-Reason |
| 618 | Charging-Information | Grouped | M,V | SAA |
| 619 | Primary-Event-Charging-Function-Name | DiameterURI | M,V | Charging-Information |
| 620 | Secondary-Event-Charging-Function-Name | DiameterURI | M,V | Charging-Information |
| 621 | Primary-Charging-Collection-Function-Name | DiameterURI | M,V | Charging-Information |
| 622 | Secondary-Charging-Collection-Function-Name | DiameterURI | M,V | Charging-Information |
| 624 | User-Data-Already-Available | Enumerated | M,V | SAR |
| 625 | Confidentiality-Key | OctetString | M,V | SIP-Auth-Data-Item |
| 626 | Integrity-Key | OctetString | M,V | SIP-Auth-Data-Item |
| 632 | Associated-Identities | Grouped | V | SAA,RTR (children: User-Name) |
| 634 | Wildcarded-Public-Identity | UTF8String | V | SAR,SAA |
| 635 | SIP-Digest-Authenticate | Grouped | V | SIP-Auth-Data-Item |
| 638 | Loose-Route-Indication | Enumerated | V | SAA |
| 639 | SCSCF-Restoration-Info | Grouped | V | SAR,SAA |
| 640 | Path | OctetString | V | Restoration-Info |
| 641 | Contact | OctetString | V | Restoration-Info, Subscription-Info, P-CSCF-Sub-Info |
| 642 | Subscription-Info | Grouped | V | Restoration-Info |
| 643 | Call-ID-SIP-Header | OctetString | V | Restoration-Info, Subscription-Info, P-CSCF-Sub-Info |
| 644 | From-SIP-Header | OctetString | V | Subscription-Info, P-CSCF-Sub-Info |
| 645 | To-SIP-Header | OctetString | V | Subscription-Info, P-CSCF-Sub-Info |
| 646 | Record-Route | OctetString | V | Subscription-Info |
| 647 | Associated-Registered-Identities | Grouped | V | SAA (children: User-Name) |
| 648 | Multiple-Registration-Indication | Enumerated | V | SAR |
| 649 | Restoration-Info | Grouped | V | SCSCF-Restoration-Info |
| 650 | Session-Priority | Enumerated | V | SAR |
| 651 | Identity-with-Emergency-Registration | Grouped | V | RTA (children: User-Name, Public-Identity) |
| 652 | Priviledged-Sender-Indication | Enumerated | V | SAA |
| 654 | Initial-CSeq-Sequence-Number | Unsigned32 | V | Restoration-Info |
| 655 | SAR-Flags | Unsigned32 | V | SAR |
| 656 | Allowed-WAF-WWSF-Identities | Grouped | V | SAA |
| 657 | WebRTC-Authentication-Function-Name | UTF8String | V | Allowed-WAF-WWSF-Identities |
| 658 | WebRTC-Web-Server-Function-Name | UTF8String | V | Allowed-WAF-WWSF-Identities |
| 659 | RTR-Flags | Unsigned32 | V | RTR |
| 660 | P-CSCF-Subscription-Info | Grouped | V | Restoration-Info |
| 661 | Registration-Time-Out | Time | V | SCSCF-Restoration-Info |
| 662 | Alternate-Digest-Algorithm | UTF8String | V | SIP-Digest-Authenticate |
| 663 | Alternate-Digest-HA1 | UTF8String | V | SIP-Digest-Authenticate |
| 664 | Failed-PCSCF | Grouped | V | SAR |
| 665 | PCSCF-FQDN | DiameterIdentity | V | Failed-PCSCF |
| 666 | PCSCF-IP-Address | Address | V | Failed-PCSCF |

## AVP inventory — new foreign-namespace modules

These AVPs occur **only nested** in `SIP-Auth-Data-Item`/`SIP-Digest-Authenticate`, so per the
mixin-scope rule each module ships **`Constants` + `AVPProvider` only — no mixins**. They must be
on the cxdx classpath so the Reflections scan registers the definitions (decode round-trip).

`sparta-diameter-ietf-radius-digest-authentication` (vendor 0) — defined in **RFC 5090** ("RADIUS
Extension for Digest Authentication", **obsoletes the deprecated RFC 4590**; RFC 4740 §9.5.6 only
*imports* these). Diameter type UTF8String, M-bit set:

| Code | Name | Type | mandatory |
|---|---|---|---|
| 104 | Digest-Realm | UTF8String → `String` | true |
| 110 | Digest-QoP | UTF8String → `String` | true |
| 111 | Digest-Algorithm | UTF8String → `String` | true |
| 121 | Digest-HA1 | UTF8String → `String` | true |

`sparta-diameter-ietf-diameter-nas` (vendor 0) — defined in **RFC 7155** ("Diameter Network
Access Server Application", **obsoletes the deprecated RFC 4005**; codes/types unchanged):

| Code | Name | Type | mandatory |
|---|---|---|---|
| 8 | Framed-IP-Address | OctetString → `byte[]` | true |
| 96 | Framed-Interface-Id | **Unsigned64 → `BigInteger`** | true |
| 97 | Framed-IPv6-Prefix | OctetString → `byte[]` | true |

`sparta-diameter-etsi-e2` (vendor 13019) — ETSI ES 283 035 v3.2.1 §7.3.5 (current published
version; Line-Identifier still referenced by TS 29.229 Rel-18, definition unchanged):

| Code | Name | Type | Flags |
|---|---|---|---|
| 500 | Line-Identifier | OctetString → `byte[]` | V (`mandatory=false`) |

## Grouped-AVP nesting (TS 29.229 §6.3)

```
SIP-Auth-Data-Item (612) = [SIP-Item-Number] [SIP-Authentication-Scheme] [SIP-Authenticate]
    [SIP-Authorization] [SIP-Authentication-Context] [Confidentiality-Key] [Integrity-Key]
    [SIP-Digest-Authenticate] [Framed-IP-Address] [Framed-IPv6-Prefix] [Framed-Interface-Id]
    *[Line-Identifier]
SIP-Digest-Authenticate (635) = {Digest-Realm} [Digest-Algorithm] {Digest-QoP} {Digest-HA1}
    [Alternate-Digest-Algorithm] [Alternate-Digest-HA1]
Deregistration-Reason (615) = {Reason-Code} [Reason-Info]
Charging-Information (618) = [Primary/Secondary-Event-Charging-Function-Name]
    [Primary/Secondary-Charging-Collection-Function-Name]
Associated-Identities (632) = *[User-Name]
Associated-Registered-Identities (647) = *[User-Name]
Identity-with-Emergency-Registration (651) = {User-Name} {Public-Identity}
Allowed-WAF-WWSF-Identities (656) = *[WebRTC-Authentication-Function-Name]
    *[WebRTC-Web-Server-Function-Name]
Failed-PCSCF (664) = [PCSCF-FQDN] *[PCSCF-IP-Address]
SCSCF-Restoration-Info (639) = {User-Name} 1*{Restoration-Info} [Registration-Time-Out]
    [SIP-Authentication-Scheme]
Restoration-Info (649) = {Path} {Contact} [Initial-CSeq-Sequence-Number] [Call-ID-SIP-Header]
    [Subscription-Info] [P-CSCF-Subscription-Info]
Subscription-Info (642) = {Call-ID-SIP-Header} {From-SIP-Header} {To-SIP-Header}
    {Record-Route} {Contact}
P-CSCF-Subscription-Info (660) = {Call-ID-SIP-Header} {From-SIP-Header} {To-SIP-Header} {Contact}
```

### Grouped-AVP modelling rule (no child accessors)

Grouped AVPs stay **flat**, exactly as in the existing repo (`HasServingNodeAVP`): a grouped AVP
gets a single mixin exposing `set(List<AVP>)` + a getter returning `AVPContainer`. **No
getters/setters are generated for the children of a grouped AVP.** Callers assemble nested
content as `List<AVP>` via `AVP.create(new AVPKey(code, vendorId), value)`.

**Mixin scope:** a `Has<Name>AVP` mixin exists **only for AVPs that appear directly at message
level** in an ABNF (this includes top-level grouped AVPs, which get the flat mixin). AVPs that
occur **only nested** inside a grouped AVP get an `AVPDefinition` in their provider — so the
recursive decoder (`AVP.readFrom`) resolves them and the message round-trips losslessly — but
**no mixin**. This mirrors the existing precedent: SGd/Gdd defines `SM-Enumerated-Delivery-Failure-Cause`
(3304, a child of the grouped `SM-Delivery-Failure-Cause`) in its provider with no mixin.

A `Has<Name>AVP` mixin models a single-valued AVP (`{X}` / `[X]`); a `Has<Name>AVPs` mixin
models a repeatable AVP (`*[X]` / `1*{X}`) — pick per the ABNF qualifier of each message (see
Message composition). Four interface-specific AVPs are repeatable and so have a `…AVPs` (list)
mixin: **Public-Identity** (SAR, RTR), **SCSCF-Restoration-Info** (SAA), **SIP-Auth-Data-Item**
(MAA) and **Identity-with-Emergency-Registration** (RTA); Public-Identity and SIP-Auth-Data-Item
*also* keep a single mixin for the messages where they are `{X}`/`[X]` (MAR/MAA resp. MAR/SAR).

Concretely, these message-direct AVPs get a cxdx mixin: Public-Identity, Server-Name,
User-Data, SIP-Number-Auth-Items, SIP-Auth-Data-Item, Server-Assignment-Type,
Deregistration-Reason, Charging-Information, User-Data-Already-Available, Associated-Identities,
Wildcarded-Public-Identity, Loose-Route-Indication, SCSCF-Restoration-Info,
Associated-Registered-Identities, Multiple-Registration-Indication, Session-Priority,
Identity-with-Emergency-Registration, Priviledged-Sender-Indication, SAR-Flags,
Allowed-WAF-WWSF-Identities, RTR-Flags, Failed-PCSCF. All other AVPs in the inventory
(SIP-Authenticate, Reason-Code, charging function names, Path/Contact, Call-ID/From/To/Record-Route,
WebRTC-*-Name, PCSCF-FQDN/IP-Address, SIP-Digest-Authenticate, Subscription-Info,
P-CSCF-Subscription-Info, Restoration-Info, …) are nested-only ⇒ **definition only, no mixin**.

## Message composition (ABNF → mixins)

Each message is an interface with nested `final class In extends Incoming{Request|Answer}` and
`final class Out extends Outgoing{Request|Answer}`, exactly like `MtForwardShortMessageRequest`.
Mandatory `{}` and optional `[]` AVPs are composed through mixins; Session-Id / Origin-Host /
Origin-Realm / Result-Code header AVPs come from the base command classes.

- **SAR (301, REQ)** — `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`,
  `HasPublicIdentityAVPs` *(0..n)*, `HasWildcardedPublicIdentityAVP`, `HasServerNameAVP`,
  `HasServerAssignmentTypeAVP`, `HasUserDataAlreadyAvailableAVP`,
  `HasScscfRestorationInfoAVP`, `HasMultipleRegistrationIndicationAVP`,
  `HasSessionPriorityAVP`, `HasSarFlagsAVP`, `HasFailedPcscfAVP`,
  `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **SAA (301, ANS)** — `_3gppAnswer`, `HasDrmpAVP`, `HasUserNameAVP`, `HasUserDataAVP`,
  `HasChargingInformationAVP`, `HasAssociatedIdentitiesAVP`, `HasLooseRouteIndicationAVP`,
  `HasScscfRestorationInfoAVPs` *(0..n)*, `HasAssociatedRegisteredIdentitiesAVP`, `HasServerNameAVP`,
  `HasWildcardedPublicIdentityAVP`, `HasPriviledgedSenderIndicationAVP`,
  `HasAllowedWafWwsfIdentitiesAVP`, `HasRouteRecordAVPs`
- **MAR (303, REQ)** — `_3gppRequest`, `HasDrmpAVP`, `HasDestinationRealmAVP`,
  `HasDestinationHostAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`,
  `HasPublicIdentityAVP`, `HasSipAuthDataItemAVP`, `HasSipNumberAuthItemsAVP`,
  `HasServerNameAVP`, `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **MAA (303, ANS)** — `_3gppAnswer`, `HasDrmpAVP`, `HasUserNameAVP`, `HasPublicIdentityAVP`,
  `HasSipNumberAuthItemsAVP`, `HasSipAuthDataItemAVPs` *(0..n)*, `HasRouteRecordAVPs`
- **RTR (304, REQ)** — `_3gppRequest`, `HasDrmpAVP`, `HasDestinationRealmAVP`,
  `HasUserNameAVP`, `HasAssociatedIdentitiesAVP`, `HasSupportedFeaturesAVPs`,
  `HasPublicIdentityAVPs` *(0..n)*, `HasDeregistrationReasonAVP`, `HasRtrFlagsAVP`,
  `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **RTA (304, ANS)** — `_3gppAnswer`, `HasDrmpAVP`, `HasAssociatedIdentitiesAVP`,
  `HasIdentityWithEmergencyRegistrationAVPs` *(0..n)*, `HasRouteRecordAVPs`

> Note: MAA carries `*[SIP-Auth-Data-Item]` and RTA `*[Identity-with-Emergency-Registration]`
> (repeatable) — these get `add`/`get`-list mixins (plural), like `HasSupportedFeaturesAVPs`.

## CxDxMessageFactory

`switch` over the three command codes producing the matching `In`/`Out` instances. `createAnswer`
sets `Auth-Session-State = AUTH_SESSION_STATE_NOT_MAINTAINED` for every answer (TS 29.229 §5.2/§5.3
— accounting not used, sessions implicitly terminated), identical to `SgdGddMessageFactory`.

## Testing

AssertJ, instance named by role (`provider`, `factory`, `command`), `it_<behavior>`,
GIVEN/WHEN/THEN:

- `CxDxAVPProviderTest` — every AVP definition has expected code/type/flags/vendor (mirror
  `SgdGddAVPProviderTest`). Same for the `radius-digest-authentication`, `diameter-nas` and
  `etsi-e2` providers.
- `CxDxMessageFactoryTest` — each command code maps to the right In/Out; answers carry
  `NO_STATE_MAINTAINED`; unknown code returns null.
- One encode→decode round-trip test per command pair, including a populated grouped AVP
  (e.g. MAA with a `SIP-Auth-Data-Item` containing a `SIP-Digest-Authenticate`) to prove all
  nested definitions are registered and resolve.

## Resolved during review

1. **Digest-* defining document** — verified against the RFCs: imported by RFC 4740 §9.5.6 from
   the RADIUS digest spec. Current source is **RFC 5090** (codes 104/110/111/121 unchanged), which
   **obsoletes the deprecated RFC 4590** (the version TS 29.229 ref [20] still points to).
   Module: `ietf-radius-digest-authentication`.
2. **Framed-* defining document & type** — current source is **RFC 7155** (Diameter NASREQ), which
   **obsoletes the deprecated RFC 4005**. Verified: Framed-Interface-Id is **Unsigned64**
   (`BigInteger`), Framed-IP-Address/IPv6-Prefix are OctetString. Module: `ietf-diameter-nas`.
3. **Line-Identifier** — ETSI ES 283 035 v3.2.1 §7.3.5: OctetString, vendor 13019, **V only /
   `mandatory=false`**. Module: `etsi-e2` (named after the e2 interface, mirroring the 3gpp
   interface-named modules).
4. **No deprecated AVPs/specs in scope** — the Cx/Dx AVPs are from TS 29.229 Rel-18; DRMP from the
   current RFC 7944. (Note: SIP-Auth `AKAv1-MD5` and the MD5 digest *algorithm value* are flagged
   "backward compatibility only" in TS 29.229, but the carrying AVPs are not deprecated.)
5. **Grouped AVPs** — flat, no child accessors; only message-direct AVPs get mixins (see rule above).
6. **OC-Supported-Features / OC-OLR / Load** — confirmed **out of scope** (optional, M-bit cleared ⇒
   decode-safe; separate DOIC/Load-control RFC domain, addable later as dedicated modules).
