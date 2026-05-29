# S6a/S6d Interface — Tasks

Execution order for `awbd-585-s6a-s6d`. Each task is TDD where it has logic (provider/factory/
round-trip); pure data classes get their assertion test alongside. Build with `mvn` directly.

## 0. Pre-flight verification (blocks coding)

- [ ] Verify reused-AVP **codes/types/flags** against defining specs (skill GOTCHA 2/3):
      TS 29.212 (RAT-Type 1032, QoS-Class-Identifier 1028, Allocation-Retention-Priority 1034,
      Priority-Level 1046, Pre-emption-Capability 1047, Pre-emption-Vulnerability 1048),
      TS 29.214 (Max-Requested-Bandwidth-UL 516 / -DL 515), TS 32.299 (3GPP-Charging-
      Characteristics 13), RFC 5778 (Service-Selection 493), RFC 5447 (MIP6-Agent-Info 486),
      RFC 4004 (MIP-Home-Agent-Address 334, MIP-Home-Agent-Host 348). Fetch & cite each.
- [ ] Confirm final names of the three per-RFC foreign Mobile-IP modules (GOTCHA 1).
- [ ] **Discover** `Confidentiality-Key` (625) / `Integrity-Key` (626) home at this later stage:
      if the Cx/Dx branch added them to `3gpp-common`, reuse; else add there (avoid duplicate).
      Only needed once `UTRAN-Vector` is wired.

## 1. Shared 3GPP AVPs → `3gpp-common`

- [ ] Add to `_3gppConstants` + `_3gppAVPProvider` (grouped under a `// TS 29.212` / `// TS 32.299`
      comment, matching existing style): RAT-Type, QoS-Class-Identifier,
      Allocation-Retention-Priority, Priority-Level, Pre-emption-Capability,
      Pre-emption-Vulnerability, Max-Requested-Bandwidth-UL/DL, 3GPP-Charging-Characteristics
      (+ Confidentiality-Key/Integrity-Key if not present).
- [ ] `HasRatTypeAVP` mixin in `3gpp-common/mixins` (RAT-Type is message-direct in ULR).
- [ ] Extend `_3gppAVPProviderTest`: code/type/flags/vendor per new AVP.

## 2. Foreign IETF Mobile-IP modules (strict: one per RFC, vendor 0, no mixins)

- [ ] `…-mobile-ipv4` (RFC 4004): MIP-Home-Agent-Address (334, Address),
      MIP-Home-Agent-Host (348, Grouped). `Constants` + `AVPProvider` only.
- [ ] `…-mobile-ipv6` (RFC 5447): MIP6-Agent-Info (486, Grouped). Depends on `…-mobile-ipv4`
      (its children are RFC 4004).
- [ ] `…-mip6-service-selection` (RFC 5778): Service-Selection (493, UTF8String).
- [ ] `pom.xml` per sibling for each; register all three in parent `pom.xml` `<modules>`.
- [ ] Provider test per module: code/type/flags/vendor.

## 3. `S6aConstants`

- [ ] App-ID 16777251, command codes 316/317/318/319/321/323, in-scope S6a AVP codes,
      Experimental-Result values (§7.4). (Enumerated value sets / flag bits as needed by mixins.)

## 4. `S6aAVPProvider`

- [ ] Define every in-scope S6a AVP from the design inventory (codes 1400–1638 subset), correct
      type/flags/vendor 10415. Include **all nested-only** AVPs (RAND/XRES/AUTN/KASME/Kc/SRES,
      Item-Number, Context-Identifier, PDN-Type, EPS-Subscribed-QoS-Profile, vectors, …) so
      round-trip resolves.
- [ ] `S6aAVPProviderTest` (mirror `SgdGddAVPProviderTest`): assert each definition.

## 5. Mixins (`s6a/mixins`) — message-direct AVPs only

- [ ] Flat grouped mixins (`set(List<AVP>)` + `AVPContainer` getter, no child accessors):
      `HasSubscriptionDataAVP`, `HasTerminalInformationAVP`, `HasAuthenticationInfoAVP`,
      `HasRequestedEutranAuthenticationInfoAVP`, `HasRequestedUtranGeranAuthenticationInfoAVP`.
- [ ] Scalar mixins: `HasUlrFlagsAVP`, `HasUlaFlagsAVP`, `HasIdrFlagsAVP`, `HasIdaFlagsAVP`,
      `HasPuaFlagsAVP`, `HasNorFlagsAVP`, `HasClrFlagsAVP`, `HasVisitedPlmnIdAVP`,
      `HasUeSrvccCapabilityAVP`, `HasCancellationTypeAVP`.
- [ ] (Reuse existing `HasUserNameAVP`, `HasSupportedFeaturesAVPs`, `HasDrmp*`, `HasDestination*`,
      `HasProxyInfoAVPs`, `HasRouteRecordAVPs`, `HasFailedAvpAVP`, and `HasRatTypeAVP` from common.)

## 6. Messages (`s6a/messages`)

- [ ] One interface per command with `In`/`Out` per the design composition: ULR/ULA, AIR/AIA,
      CLR/CLA, IDR/IDA, PUR/PUA, NOR/NOA (mirror `MtForwardShortMessageRequest`/`...Answer`).

## 7. `S6aMessageFactory`

- [ ] `DiameterPackageFactory` with `switch` over the six command codes; `createAnswer` sets
      `Auth-Session-State = NOT_MAINTAINED` (like `SgdGddMessageFactory`).
- [ ] `S6aMessageFactoryTest`: command code → In/Out mapping; answers carry `NO_STATE_MAINTAINED`;
      unknown code → null.

## 8. Round-trip tests

- [ ] Encode→decode per command pair. Mandatory: a ULA with `Subscription-Data` →
      `APN-Configuration-Profile` → `APN-Configuration` → `EPS-Subscribed-QoS-Profile` +
      `AMBR` + `Service-Selection` + `MIP6-Agent-Info`; an AIA with `Authentication-Info` →
      `E-UTRAN-Vector`. Asserts all nested definitions resolve (no 5001).

## 9. Wire-up & build

- [ ] `sparta-diameter-3gpp-s6a/pom.xml`: deps (`3gpp-common`, `ietf-drmp`, foreign module(s),
      junit + assertj test scope). Confirm `s6a` and any new modules are in parent `<modules>`.
- [ ] `mvn -q -pl sparta-diameter-3gpp-s6a -am test` green; then full `mvn test`.

## 10. Done

- [ ] Update `docs/` overview if it enumerates interfaces (DoD).
- [ ] Self-review against AWBD-585 ACs; request review.

## Out of scope (explicit — see 01-requirements)

S13/S7a, DSR/RSR, OC-*/Load; Subscription-Data branches the HSS doesn't use (LCS, Trace,
GPRS-Subscription, CSG, V2X, MDT, ProSe, Monitoring, …). Additively extensible later.
