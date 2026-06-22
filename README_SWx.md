# SWx Interface — Branch `user/towi/swx-duplicate`

Implementierung des 3GPP TS 29.273 §8 SWx Diameter Interface als neues Modul
`sparta-diameter-3gpp-swx` im **Duplicate-Ansatz**: jede 3GPP-Interface-AVP
bekommt einen lokalen `swx.mixins`-Accessor, nichts wird nach `common`
verschoben, cxdx/s6a/gx/rx bleiben unangetastet.

## Ansatz (gegenüber `user/towi/SWx`)

Der bestehende Branch `user/towi/SWx` teilt AVP-Accessor-Mixins über `_3gpp.common`
(teilt Verschiebung von Mixins aus cxdx/s6a/gx nach `common`). Dieser Branch geht
den entgegengesetzten Weg — **Duplikation** auf Mixin-Ebene, passend zum Muster,
das `cxdx` auf `main` bereits praktiziert (lokale `cxdx.mixins` trotz
common-Varianten).

Zwei Fakten tragen die Entscheidung:

1. **Die AVP-Registry erlaubt identische Zweitregistrierung als No-Op.**
   `AVP.registerProvider` (base/`avp/AVP.java`) nutzt `putIfAbsent` und lehnt nur
   *genuine* Konflikte — zwei *verschiedene* Definitionen für dasselbe
   `(code, vendorId)` — ab. Eine AVP-Definition kann also einmal in
   `_3gppAVPProvider` (common) liegen und von jedem Protokoll referenziert
   werden, ohne Registry-Konflikt.

2. **Der Cx↔SWx-Unterschied liegt auf Command-Ebene, nicht auf
   AVP-Definitions-Ebene.** Eine 3GPP-AVP (z. B. SIP-Auth-Data-Item, Code 612)
   hat *eine* Wire-Definition über 29.229 und 29.273. Was sich unterscheidet, ist
   die **Command Code Format (CCF)** — welche AVPs in einem Command
   mandatory/optional sind. Das ist per-Protocol in separaten Message-Klassen +
   CCF-Test ausgedrückt.

Schluss: AVP-*Definitionen* bleiben single-source (ein Registry-Eintrag pro
Code). AVP-*Accessor-Mixins* sind die Schicht, wo Duplikation entkoppelt — eine
Cx-bedingte Änderung an einem `common`-Mixin darf SWx nicht beeinflussen.

## Branch-Commits

```
ba0f71b feat(swx): SWx module — local mixins, 8 messages, factory, spec tests
f1ee68b fix(spec): parse digit-prefixed AVP names (3GPP-AAA-Server-Name) in CCF
b9cd737 feat(swx): SwxConstants, SwxAVPProvider, swx-avps.json
aa7535b feat(swx): module skeleton sparta-diameter-3gpp-swx
d6ca2e1 feat(common,spec-extractor): shared SWx AVP constants, definitions, SpecSource
```

## Modulstruktur

```
sparta-diameter-3gpp-swx/
  pom.xml                                  mirror cxdx + mip6-integrated + mip6-split
  src/main/java/.../_3gpp/swx/
    SwxConstants.java                      APP_ID_SWX=16777265, 4 Cmd-Codes, 15 SWx-AVP-Codes
    SwxAVPProvider.java                    15 SWx-spezifische AVPDefinitionen
    SwxAnswer.java                         swx-lokale Answer-Basis (s. unten)
    messages/
      MultimediaAuthRequest/Answer.java    } je interface + In/Out
      ServerAssignmentRequest/Answer.java  }
      RegistrationTerminationRequest/Answer.java }
      PushProfileRequest/Answer.java       } (PPR ist SWx-only)
      SwxMessageFactory.java               DiameterPackageFactory, Dispatch (code, APP_ID_SWX)
    mixins/
      Has<SWx-spezifisch>AVP.java          7: Non-3GPP-User-Data, AN-Trusted, ANID,
                                              AAA-Failure-Indication, 3GPP-AAA-Server-Name,
                                              Access-Network-Info, PPR-Flags
      Has<shared-3GPP>AVP.java             13: RAT-Type, SIP-Auth-Data-Item(s),
                                              SIP-Number-Auth-Items, Supported-Features(+s),
                                              Visited-Network-Identifier, Terminal-Information,
                                              Deregistration-Reason, Server-Assignment-Type,
                                              Active-APN(+s), Emergency-Services, Local-Time-Zone,
                                              Context-Identifier
  src/test/.../_3gpp/swx/
    SwxCommandSpecTest.java                CCF §8.1.2 (4 Cmd-Paare)
    SwxAvpSpecTest.java                    swx-avps.json (nur SWx-spezifisch)
    SwxMessageFactoryTest.java            In/Out-Roundtrip
    resources/swx-avps.json               29.273-sourced
```

## Duplikations-Policy

| AVP-Kategorie | Mixin liegt in | AVP-Definition (Registry) |
|---|---|---|
| SWx-spezifisch (§8.2.3) | `swx.mixins` (lokal) | `SwxAVPProvider` |
| Shared 3GPP Interface-AVP (Namesake in Cx/S6a/Gx) | `swx.mixins` (lokale Kopie) | `_3gppAVPProvider` (common), single-source |
| Base/IETF AVP (Session-Id, Origin-*, User-Name, DRMP, OC, Load, MIP6-Agent-Info, Service-Selection, Proxy-Info, Route-Record) | referenziert aus existierendem Modul | existierender Provider |

- **Konstanten:** SWx-spezifische Codes in `SwxConstants`; shared 3GPP-Codes via
  `_3gppConstants` (common) — deshalb die 4 additiven Constant-Definitionen in
  `_3gppConstants`.
- **Grouped AVPs** folgen dem `HasSipAuthDataItemAVPs`-Muster
  (`addX(List<AVP>)` / `List<AVPContainer> getXs()` / `addAllXs`).

## Tests

- `SwxCommandSpecTest` (16) — CCF §8.1.2; prüft pro Command, dass jeder CCF-AVP
  als Accessor exponiert ist. **Das ist die eigentliche Cx↔SWx-Differenz-Prüfung.**
- `SwxAvpSpecTest` (48, 18 skipped) — Roundtrip + Accessoren **nur** für
  SWx-spezifische AVPs. Shared-3GPP-Mixins bewusst *nicht* roundtrip-getestet
  (CCF-only, da identische Definition schon in s6a/cxdx getestet). Die 18
  Skipped sind SWx-AVPs ohne Mixin (by design — JSON ist die volle 29.273-Tabelle,
  Mixins nur für CCF-referenzierte AVPs).
- `SwxMessageFactoryTest` (6) — In/Out-Roundtrip der 8 Messages.
- `CommandCodeFormatParserTest#it_parses_a_digit_prefixed_avp_name` — treibt
  den `Ccf.java`-Tokenizerfix für ziffernpräfixe AVP-Namen
  (`3GPP-AAA-Server-Name`).

**Gesamt: 70/70 PASS (18 skipped).**

## Verifiziert

- swx: 70/70 Tests grün.
- Full-Reactor-Compile (`mvn -T1 compile`): exit 0.
- s6a/gx/cxdx-Regression: 785+234+219 Tests, 0 Failures (die 4 identischen
  common-Definitionen sind No-Ops, keine `IllegalState`-Konflikte).
- Whole-Branch-Reviewer (fresh context, report-only): **APPROVED** —
  0 Critical/Important Issues; Duplicate-Integrität bestätigt
  (0 `_3gpp.common.mixins`-Referenzen in swx/src; `common/mixins` unverändert;
  cxdx/s6a/gx/rx unangetastet).

## Implementations-Entdeckungen (Abweichungen vom Plan, transparent)

1. **`SwxAnswer`-Basis neu** — der Plan sah das nicht vor. `_3gppAnswer` (common)
   bäckt `common.mixins.HasSupportedFeaturesAVPs` ein und erzwingt damit
   Common-Coupling auf jede 3GPP-Antwort; mit der swx-lokalen Variante direkt
   entsteht „unrelated defaults". Lösung: swx-lokales `SwxAnswer` (Mirror von
   `_3gppAnswer` ohne `HasSupportedFeaturesAVPs`); Antworten deklarieren
   `swx.mixins.HasSupportedFeaturesAVPs` selbst. Lässt bewusst auch das singular
   `HasProxyInfoAVP` weg (SWx-Answer-CCFs tragen nur plural Proxy-Info).

2. **20 Mixins, nicht 21** — Service-Selection ist IETF (RFC 5778, aus
   `mip6-integrated` referenziert), kein 3GPP-AVP → nicht dupliziert.
   (Plan-Eintrag war ein Fehler.)

3. **`mip6-split`-Dep ergänzt** — `HasMip6AgentInfoAVP` liegt in `mip6-split`,
   nicht in `mip6-integrated`. Pom braucht beide mip6-Module.

4. **SIP-Auth-Data-Item singular + plural** — MAR benötigt `{ SIP-Auth-Data-Item }`
   (singular, `HasSipAuthDataItemAVP`), MAA `*[ SIP-Auth-Data-Item ]` (plural,
   `HasSipAuthDataItemAVPs`). Beide Mixins implementiert.

## Offen (Minor, nicht blockierend)

- `getDataAsInt()` vs `getDataAsEnumerated()`-Inkonsistenz bei 3 Enumerated-Mixins
  (aus Branch-Originalen übernommen); Verhalten wire-äquivalent.
- `SwxAvpSpecTest` lädt `Mip6SplitAVPProvider` nicht (JSON ist SWx-only — aktuell
  irrelevant).

## Nicht-Ziele

- `sparta-hss`-Integration (deferred).
- Refactor bestehender `common` 3GPP-Mixins auf `main`.
- Auflösen der cxdx↔common-Mixin-Doppelung, die auf `main` bereits existiert.

## Referenzen

- Design: `specs/swx-interface/02-design.md`
- Plan: `specs/swx-interface/03-tasks.md`
- Spec: 3GPP TS 29.273 §8 (SWx), app-id `16777265`, vendor 3GPP `10415`.
