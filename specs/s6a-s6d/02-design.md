# S6a/S6d Interface — Design

Mirrors the SGd/Gdd module pattern (`Constants` → `AVPProvider` → `messages/` →
`MessageFactory` → `mixins/`). Providers and factories are discovered automatically via the
Reflections scan over `com.sipgate.sparta.diameter`, so no manual registration is required — a
module just needs to be on the classpath and in the parent `pom.xml` `<modules>`.

**Scope is HSS-driven.** The AVP set below is exactly what `sparta-hss`'s existing S6a code
(`com.sipgate.sparta.protocol.diameter.s6a`) produces/consumes (see `01-requirements.md`
„Scope-Tiefe"). Subscription-Data subtrees the HSS does not use (LCS, Trace, GPRS-Subscription,
CSG, V2X, MDT, ProSe, Monitoring, …) are out of scope and additively extensible later.

## Modules & dependencies

```
sparta-diameter-3gpp-s6a                          (new content; was an empty Constants stub)
  ├─ sparta-diameter-3gpp-common                  (Supported-Features, MSISDN, + new shared 3GPP)
  │    └─ sparta-diameter-base                     (RFC 6733 AVPs, command base classes)
  ├─ sparta-diameter-ietf-drmp                     (DRMP)
  ├─ sparta-diameter-ietf-diameter-mobile-ipv4     (RFC 4004 — MIP-Home-Agent-Address/Host)
  ├─ sparta-diameter-ietf-diameter-mobile-ipv6     (RFC 5447 — MIP6-Agent-Info; → mobile-ipv4)
  └─ sparta-diameter-ietf-diameter-mip6-service-selection (RFC 5778 — Service-Selection)
```

`sparta-diameter-3gpp-s6a/pom.xml` (copy a sibling like `sgdgdd`): depend on `3gpp-common`,
`ietf-drmp`, the three foreign modules below, plus `junit-jupiter` + `assertj-core` (test scope).

### Foreign IETF Mobile-IP AVPs — strict one module per defining RFC (decided)

Per review: **strict** convention (GOTCHA 8) — one module per defining spec, named by protocol
(human-readable, not RFC number; GOTCHA 1). All vendor 0, **encoded-only** by the HSS inside
`APN-Configuration`, so each ships `Constants` + `AVPProvider` only — **no mixins**:

- **`sparta-diameter-ietf-diameter-mobile-ipv4`** — RFC 4004 (*Diameter Mobile IPv4 Application*):
  MIP-Home-Agent-Address (334, Address), MIP-Home-Agent-Host (348, Grouped).
- **`sparta-diameter-ietf-diameter-mobile-ipv6`** — RFC 5447 (*Diameter Mobile IPv6: NAS↔server*):
  MIP6-Agent-Info (486, Grouped). Children (MIP-Home-Agent-*) are RFC 4004 ⇒ this module
  **depends on** `…-mobile-ipv4`.
- **`sparta-diameter-ietf-diameter-mip6-service-selection`** — RFC 5778 (*Diameter Mobile IPv6:
  HA↔server*): Service-Selection (493, UTF8String). NB: RFC 5447 and RFC 5778 are both
  "Diameter Mobile IPv6" — the two module names are deliberately distinguished by content; confirm
  final names at implementation per GOTCHA 1.

## S6aConstants

```
APP_ID_S6A_S6D = 16777251                         // TS 29.272 §7.1.8

// Command codes (TS 29.272 Table 7.2.2/1)
CMD_UPDATE_LOCATION             = 316
CMD_CANCEL_LOCATION             = 317
CMD_AUTHENTICATION_INFORMATION  = 318
CMD_INSERT_SUBSCRIBER_DATA      = 319
CMD_PURGE_UE                    = 321
CMD_NOTIFY                      = 323

// S6a/S6d-specific AVP codes (TS 29.272 Table 7.3.1/1) — in-scope subset, see inventory below
// 1400 Subscription-Data … 1643 A-MSISDN

// Experimental-Result values (TS 29.272 §7.4). 5001 reused from _3gppConstants
//   (EXP_RES_DIAMETER_ERROR_USER_UNKNOWN). Defined here:
EXP_RES_DIAMETER_AUTHENTICATION_DATA_UNAVAILABLE      = 4181
EXP_RES_DIAMETER_ERROR_CAMEL_SUBSCRIPTION_PRESENT     = 4182
EXP_RES_DIAMETER_ERROR_ROAMING_NOT_ALLOWED            = 5004
EXP_RES_DIAMETER_ERROR_UNKNOWN_EPS_SUBSCRIPTION       = 5420
EXP_RES_DIAMETER_ERROR_RAT_NOT_ALLOWED                = 5421
EXP_RES_DIAMETER_ERROR_EQUIPMENT_UNKNOWN              = 5422
EXP_RES_DIAMETER_ERROR_UNKNOWN_SERVING_NODE           = 5423
```

## AVP inventory — `s6a` module (3GPP vendor 10415)

Type mapping: OctetString→`byte[]`, UTF8String/DiameterURI/DiameterIdentity→`String`,
Unsigned32→`Long`, Enumerated→`Integer`, Time→`Date`, Grouped→`GroupedAVP`, Address→`InetAddress`.
Flags from Table 7.3.1/1: `M,V` ⇒ `mandatory=true, vendorSpecific=true, 10415`; `V` ⇒
`mandatory=false, vendorSpecific=true, 10415`.

| Code | Name | Type | Flags | In groups / commands |
|---|---|---|---|---|
| 1400 | Subscription-Data | Grouped | M,V | ULA, IDR |
| 1401 | Terminal-Information | Grouped | M,V | ULR |
| 1402 | IMEI | UTF8String | M,V | Terminal-Information |
| 1403 | Software-Version | UTF8String | M,V | Terminal-Information |
| 1405 | ULR-Flags | Unsigned32 | M,V | ULR |
| 1406 | ULA-Flags | Unsigned32 | M,V | ULA |
| 1407 | Visited-PLMN-Id | OctetString | M,V | ULR, AIR |
| 1408 | Requested-EUTRAN-Authentication-Info | Grouped | M,V | AIR |
| 1409 | Requested-UTRAN-GERAN-Authentication-Info | Grouped | M,V | AIR |
| 1410 | Number-Of-Requested-Vectors | Unsigned32 | M,V | Requested-*-Auth-Info |
| 1411 | Re-Synchronization-Info | OctetString | M,V | Requested-*-Auth-Info |
| 1412 | Immediate-Response-Preferred | Unsigned32 | M,V | Requested-*-Auth-Info |
| 1413 | Authentication-Info | Grouped | M,V | AIA |
| 1414 | E-UTRAN-Vector | Grouped | M,V | Authentication-Info |
| 1415 | UTRAN-Vector | Grouped | M,V | Authentication-Info |
| 1416 | GERAN-Vector | Grouped | M,V | Authentication-Info |
| 1417 | Network-Access-Mode | Enumerated | M,V | Subscription-Data |
| 1419 | Item-Number | Unsigned32 | M,V | E-/UTRAN-/GERAN-Vector |
| 1420 | Cancellation-Type | Enumerated | M,V | CLR |
| 1423 | Context-Identifier | Unsigned32 | M,V | APN-Config(-Profile) |
| 1424 | Subscriber-Status | Enumerated | M,V | Subscription-Data |
| 1427 | APN-OI-Replacement | UTF8String | M,V | Subscription-Data, APN-Configuration |
| 1428 | All-APN-Configurations-Included-Indicator | Enumerated | M,V | APN-Configuration-Profile |
| 1429 | APN-Configuration-Profile | Grouped | M,V | Subscription-Data |
| 1430 | APN-Configuration | Grouped | M,V | APN-Configuration-Profile |
| 1431 | EPS-Subscribed-QoS-Profile | Grouped | M,V | APN-Configuration |
| 1432 | VPLMN-Dynamic-Address-Allowed | Enumerated | M,V | APN-Configuration |
| 1435 | AMBR | Grouped | M,V | Subscription-Data, APN-Configuration |
| 1438 | PDN-GW-Allocation-Type | Enumerated | M,V | APN-Configuration |
| 1440 | RAT-Frequency-Selection-Priority-ID | Unsigned32 | M,V | Subscription-Data |
| 1441 | IDA-Flags | Unsigned32 | M,V | IDA |
| 1442 | PUA-Flags | Unsigned32 | M,V | PUA |
| 1443 | NOR-Flags | Unsigned32 | M,V | NOR |
| 1447 | RAND | OctetString | M,V | *-Vector |
| 1448 | XRES | OctetString | M,V | E-UTRAN/UTRAN-Vector |
| 1449 | AUTN | OctetString | M,V | E-UTRAN/UTRAN-Vector |
| 1450 | KASME | OctetString | M,V | E-UTRAN-Vector |
| 1453 | Kc | OctetString | M,V | GERAN-Vector |
| 1454 | SRES | OctetString | M,V | GERAN-Vector |
| 1456 | PDN-Type | Enumerated | M,V | APN-Configuration |
| 1490 | IDR-Flags | Unsigned32 | M,V | IDR |
| 1613 | SIPTO-Permission | Enumerated | V | APN-Configuration |
| 1615 | UE-SRVCC-Capability | Enumerated | V | ULR |
| 1618 | LIPA-Permission | Enumerated | V | APN-Configuration |
| 1638 | CLR-Flags | Unsigned32 | V | CLR |

> Codes 1604/1635/1643 etc. (PUR-Flags, A-MSISDN, …) are reachable only via out-of-scope
> branches and are **not** modelled in the first delivery (no HSS use, no decode obligation).

## Reused-AVP mapping (no duplicates)

| AVP | Code | Type | Source module | Status |
|---|---|---|---|---|
| Session-Id, Origin/Destination-Host/Realm, User-Name, Auth-Session-State, Result-Code, Experimental-Result(-Code), Vendor-Specific-Application-Id, Failed-AVP, Proxy-Info, Route-Record | — | — | `base` (RFC 6733) | exists |
| DRMP | — | Enumerated | `ietf-drmp` | exists |
| Supported-Features / Feature-List-ID / Feature-List | 628/629/630 | — | `3gpp-common` | exists |
| MSISDN | 701 | OctetString | `3gpp-common` | exists |
| RAT-Type | 1032 | Enumerated | `3gpp-common` (TS 29.212) | **add** ⚠verify code/flags |
| QoS-Class-Identifier | 1028 | Enumerated | `3gpp-common` (TS 29.212) | **add** ⚠ |
| Allocation-Retention-Priority | 1034 | Grouped | `3gpp-common` (TS 29.212) | **add** ⚠ |
| Priority-Level | 1046 | Unsigned32 | `3gpp-common` (TS 29.212) | **add** ⚠ |
| Pre-emption-Capability | 1047 | Enumerated | `3gpp-common` (TS 29.212) | **add** ⚠ |
| Pre-emption-Vulnerability | 1048 | Enumerated | `3gpp-common` (TS 29.212) | **add** ⚠ |
| Max-Requested-Bandwidth-UL | 516 | Unsigned32 | `3gpp-common` (TS 29.214) | **add** ⚠ |
| Max-Requested-Bandwidth-DL | 515 | Unsigned32 | `3gpp-common` (TS 29.214) | **add** ⚠ |
| 3GPP-Charging-Characteristics | 13 | OctetString | `3gpp-common` (TS 32.299) | **add** ⚠ |
| Service-Selection | 493 | UTF8String | foreign IETF (RFC 5778, vendor 0) | **new** ⚠ |
| MIP6-Agent-Info | 486 | Grouped | foreign IETF (RFC 5447, vendor 0) | **new** ⚠ |
| MIP-Home-Agent-Address | 334 | Address | foreign IETF (RFC 4004, vendor 0) | **new** ⚠ |
| MIP-Home-Agent-Host | 348 | Grouped | foreign IETF (RFC 4004, vendor 0) | **new** ⚠ |

⚠ = code/type/flags must be confirmed against the **defining** spec before coding (skill
GOTCHA 2/3 + „verify defining spec"). The 3GPP TS 29.212 QoS family + 3GPP-Charging-Characteristics
go into `3gpp-common` (3GPP, cross-interface reusables — same rationale as the existing 29.329/
29.336/29.173/29.338 entries there). The IETF Mobile-IP AVPs go into the foreign module(s) per the
decision above.

## Grouped-AVP nesting (in-scope subset, TS 29.272 §7.3)

```
Subscription-Data (1400) = [Subscriber-Status] [MSISDN] [APN-OI-Replacement] [AMBR]
    [APN-Configuration-Profile] [Network-Access-Mode] [RAT-Frequency-Selection-Priority-ID]
    [3GPP-Charging-Characteristics]
APN-Configuration-Profile (1429) = {Context-Identifier} {All-APN-Configurations-Included-Indicator}
    1*{APN-Configuration}
APN-Configuration (1430) = {Context-Identifier} {PDN-Type} {Service-Selection}
    [EPS-Subscribed-QoS-Profile] [APN-OI-Replacement] [3GPP-Charging-Characteristics] [AMBR]
    [MIP6-Agent-Info] [SIPTO-Permission] [LIPA-Permission] [VPLMN-Dynamic-Address-Allowed]
    [PDN-GW-Allocation-Type]
EPS-Subscribed-QoS-Profile (1431) = {QoS-Class-Identifier} {Allocation-Retention-Priority}
Allocation-Retention-Priority (1034) = {Priority-Level} [Pre-emption-Capability]
    [Pre-emption-Vulnerability]
AMBR (1435) = {Max-Requested-Bandwidth-UL} {Max-Requested-Bandwidth-DL}
MIP6-Agent-Info (486) = [MIP-Home-Agent-Address] [MIP-Home-Agent-Host]
MIP-Home-Agent-Host (348) = {Destination-Realm} {Destination-Host}   (base AVPs)
Terminal-Information (1401) = [IMEI] [Software-Version]
Requested-EUTRAN-Authentication-Info (1408) =
    [Number-Of-Requested-Vectors] [Immediate-Response-Preferred] [Re-Synchronization-Info]
Requested-UTRAN-GERAN-Authentication-Info (1409) = (same children)
Authentication-Info (1413) = *[E-UTRAN-Vector] *[UTRAN-Vector] *[GERAN-Vector]
E-UTRAN-Vector (1414) = [Item-Number] {RAND} {XRES} {AUTN} {KASME}
UTRAN-Vector  (1415) = [Item-Number] {RAND} {XRES} {AUTN} {Confidentiality-Key} {Integrity-Key}
GERAN-Vector  (1416) = [Item-Number] {RAND} {SRES} {Kc}
```

`Confidentiality-Key` (625) / `Integrity-Key` (626) are TS 29.229 AVPs used by `UTRAN-Vector`.
**Their final home is left open — to be discovered at a later stage** (the Cx/Dx branch may land
them in `3gpp-common`; if so, reuse, else add there). Resolve when wiring `UTRAN-Vector`, just
before coding the provider — see `03-tasks.md` task 0.

### Grouped-AVP modelling rule & mixin scope

Identical to the Cx/Dx design: grouped AVPs stay **flat** — one mixin per **message-direct**
grouped AVP exposing `set(List<AVP>)` + a getter returning `AVPContainer`; **no child accessors**.
Nested-only AVPs get an `AVPDefinition` in the provider but **no mixin**.

Message-direct AVPs that get an `S6a` mixin: `Subscription-Data`, `Terminal-Information`,
`ULR-Flags`, `ULA-Flags`, `IDR-Flags`, `IDA-Flags`, `PUA-Flags`, `NOR-Flags`, `CLR-Flags`,
`Visited-PLMN-Id`, `RAT-Type`*, `UE-SRVCC-Capability`,
`Requested-EUTRAN-Authentication-Info`, `Requested-UTRAN-GERAN-Authentication-Info`,
`Authentication-Info`, `Cancellation-Type`. (`User-Name`, `Supported-Features`,
`Vendor-Specific-Application-Id` reuse existing base/common mixins.)
All vector/QoS/APN children are nested-only ⇒ **definition only, no mixin**.
*RAT-Type mixin lives in `3gpp-common` if added there.

## Message composition (ABNF → mixins)

Each message = interface with nested `final class In extends Incoming{Request|Answer}` and
`final class Out extends Outgoing{Request|Answer}` (like `MtForwardShortMessageRequest`). Header
AVPs (Session-Id, Origin-*, Result-Code, Auth-Session-State) come from the base command classes.
Only the **HSS-used** AVPs are composed (full ABNF optional AVPs the HSS ignores are omitted):

- **ULR (316, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasTerminalInformationAVP`,
  `HasRatTypeAVP`, `HasUlrFlagsAVP`, `HasUeSrvccCapabilityAVP`, `HasVisitedPlmnIdAVP`,
  `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **ULA (316, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`,
  `HasUlaFlagsAVP`, `HasSubscriptionDataAVP`, `HasFailedAvpAVP`, `HasRouteRecordAVPs`
- **AIR (318, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`,
  `HasRequestedEutranAuthenticationInfoAVP`, `HasRequestedUtranGeranAuthenticationInfoAVP`,
  `HasVisitedPlmnIdAVP`, `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **AIA (318, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`,
  `HasAuthenticationInfoAVP`, `HasFailedAvpAVP`, `HasRouteRecordAVPs`
- **CLR (317, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasCancellationTypeAVP`,
  `HasClrFlagsAVP`, `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **CLA (317, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`, `HasFailedAvpAVP`,
  `HasRouteRecordAVPs`
- **IDR (319, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasSubscriptionDataAVP`,
  `HasIdrFlagsAVP`, `HasProxyInfoAVPs`, `HasRouteRecordAVPs`
- **IDA (319, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`, `HasIdaFlagsAVP`,
  `HasFailedAvpAVP`, `HasRouteRecordAVPs`
- **PUR (321, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasProxyInfoAVPs`,
  `HasRouteRecordAVPs`
- **PUA (321, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`, `HasPuaFlagsAVP`,
  `HasFailedAvpAVP`, `HasRouteRecordAVPs`
- **NOR (323, REQ)** → `_3gppRequest`, `HasDrmpAVP`, `HasDestinationHostAVP`,
  `HasDestinationRealmAVP`, `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasProxyInfoAVPs`,
  `HasRouteRecordAVPs`
- **NOA (323, ANS)** → `_3gppAnswer`, `HasDrmpAVP`, `HasSupportedFeaturesAVPs`, `HasFailedAvpAVP`,
  `HasRouteRecordAVPs`

> `Authentication-Info` carries `*[E-UTRAN-Vector]` etc. (repeatable) — these are nested inside
> the grouped AVP, assembled as `List<AVP>`; no plural message-level mixin needed.

## S6aMessageFactory

`switch` over the six command codes producing the matching `In`/`Out` instances. `createAnswer`
sets `Auth-Session-State = AUTH_SESSION_STATE_NOT_MAINTAINED` for every answer (TS 29.272 §7.1.3
accounting not used, §7.1.4 sessions implicitly terminated) — identical to `SgdGddMessageFactory`.

## Testing

AssertJ, instance named by role (`provider`, `factory`, `command`), `it_<behavior>`,
GIVEN/WHEN/THEN:

- `S6aAVPProviderTest` — every AVP definition has expected code/type/flags/vendor (mirror
  `SgdGddAVPProviderTest`). Same for any new foreign Mobile-IP provider; extend
  `_3gppAVPProviderTest` for the AVPs added to `3gpp-common`.
- `S6aMessageFactoryTest` — each command code maps to the right In/Out; answers carry
  `NO_STATE_MAINTAINED`; unknown code returns null.
- One encode→decode round-trip per command pair, incl. a populated grouped AVP — at minimum a
  ULA carrying a `Subscription-Data` with an `APN-Configuration-Profile → APN-Configuration →
  EPS-Subscribed-QoS-Profile` and an `AMBR`, and an AIA carrying an `Authentication-Info` with an
  `E-UTRAN-Vector` — proving all nested definitions resolve.

## To resolve during implementation

1. ⚠ reused-AVP codes/types/flags verified against TS 29.212 / TS 29.214 / TS 32.299 / RFC 5778 /
   RFC 5447 / RFC 4004.
2. `Confidentiality-Key` / `Integrity-Key` home — discover at a later stage (see above + task 0).
3. Final names of the three foreign Mobile-IP modules (GOTCHA 1).
