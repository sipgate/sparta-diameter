# S6a/S6d Interface — Requirements

Jira: AWBD-585 — *sparta-diameter um S6a/S6d und Cx/Dx erweitern* (diese Spec deckt **nur
S6a/S6d** ab; Cx/Dx hat eine eigene Spec unter `specs/cx-dx/`).

## Value

Als Plattformbetreiber möchte ich, dass `sparta-diameter` das **S6a/S6d**-Interface
(MME/SGSN ↔ HSS) implementiert, damit **sparta-hss** als HSS-Implementierung für
Subscriber-Authentifizierung, Location-Management und Subscriber-Datenverwaltung im
Mobilfunk-Core genutzt werden kann.

## Referenz

- **3GPP TS 29.272 / ETSI TS 129 272 v18.6.0** (Rel-18) — *einziges* definierendes Dokument
  für S6a/S6d: Command-Codes **und** Message-Contents/ABNF **und** AVP-Codes/Typen/Flags
  **und** Result-Codes liegen alle hier (anders als Cx/Dx mit 29.228+29.229).
  Lokal: `docs/specs/etsi/ts_129272v180600p.pdf`.

Application-ID S6a/S6d: **16777251** (IANA). Vendor 3GPP: **10415**. Alle interface-spezifischen
AVPs tragen Vendor-ID 10415.

Das Dokument definiert zusätzlich **S13/S13'** (16777252) und **S7a/S7d** (16777308) — beide
**out of scope** (siehe unten).

### Reused-AVP-Strategie (Tabelle 7.3.1/2) — nach Bestands-Konvention

S6a/S6d wiederverwendet AVPs aus mehreren Specs. Wir folgen **exakt der etablierten Aufteilung**
(`3gpp-common` + Cx/Dx-Spec), **keine spekulativen Pro-TS-Module**:

- **RFC 6733 (base)** → `sparta-diameter-base` referenzieren.
- **RFC 7944 (DRMP)** → `sparta-diameter-ietf-drmp` referenzieren.
- **Gemeinsame 3GPP-AVPs (Vendor 10415) → `sparta-diameter-3gpp-common`** — *ein* Modul,
  intern per definierender-TS-Kommentar gegliedert (genau wie dort heute 29.229/29.329/29.336/
  **29.272**/29.173/29.338 koexistieren). Bereits vorhanden und nur zu referenzieren:
  Supported-Features (628), Feature-List-ID (629), Feature-List (630), MSISDN (701),
  EPS-Location-Information (1496), MME-Number-for-MT-SMS (1645), SGSN-Number (1489).
  In `3gpp-common` **zu ergänzen**, sofern in den 6 Commands erreichbar und sinnvoll geteilt:
  Confidentiality-Key (625)/Integrity-Key (626)/Visited-Network-Identifier (600) (29.229),
  RAT-Type + QoS-/Bandwidth-AVPs (29.212), GMLC-Address (29.173) u. a.
- **S6a/S6d-spezifische AVPs (Codes 1400–1727) → eigener `S6aAVPProvider`** im Modul
  `sparta-diameter-3gpp-s6a` (analog `SgdGddAVPProvider`).
- **Fremd-Namespace-AVPs (Nicht-3GPP) → eigenes, nach definierender Spec benanntes Modul**
  (Konvention GOTCHA 8) — nur falls in den 6 Commands erreichbar:
  Service-Selection (RFC 5778), MIP6-Agent-Info (RFC 5447) / MIP-Home-Agent-Address/Host
  (RFC 4004). Bestehende Module (`ietf-diameter-nas` …) zuerst auf Wiederverwendbarkeit prüfen.

> Die exakte Zuordnung *welcher* reused-AVP in welchem Modul landet (und welche real in den
> 6 Commands erreichbar sind), wird in `02-design.md` als präzise Tabelle fixiert.

## Scope — Commands (Acceptance Criteria)

Die sechs Pflicht-Command-Paare aus AWBD-585. Application-ID 16777251, alle PXY:

| Command | Abk. | Code | Richtung | Clause |
|---|---|---|---|---|
| Update-Location-Request/Answer | ULR/ULA | 316 | MME/SGSN → HSS | 7.2.3/7.2.4 |
| Cancel-Location-Request/Answer | CLR/CLA | 317 | HSS → MME/SGSN | 7.2.7/7.2.8 |
| Authentication-Information-Request/Answer | AIR/AIA | 318 | MME/SGSN → HSS | 7.2.5/7.2.6 |
| Insert-Subscriber-Data-Request/Answer | IDR/IDA | 319 | HSS → MME/SGSN | 7.2.9/7.2.10 |
| Purge-UE-Request/Answer | PUR/PUA | 321 | MME/SGSN → HSS | 7.2.13/7.2.14 |
| Notify-Request/Answer | NOR/NOA | 323 | MME/SGSN → HSS | 7.2.17/7.2.18 |

## Scope — AVPs

- **Volle ABNF-Treue** (wie Cx/Dx): jeder AVP, der in den sechs Command-Paaren vorkommt —
  direkt oder verschachtelt in Grouped-AVPs — wird vollständig nach Spec modelliert
  (Definition + ggf. Top-Level-Mixin). Grouped-AVPs **flach** (`HasServingNodeAVP`-Stil):
  ein Mixin pro Top-Level-Grouped-AVP mit `set(List<AVP>)`/Getter auf `AVPContainer`;
  Leaf-AVPs getypt; rein verschachtelte AVPs bekommen nur eine `AVPDefinition`, kein Mixin.
- **Achtung Größe:** TS 29.272 definiert **~230 interface-spezifische AVPs** (Tabelle 7.3.1/1,
  Codes 1400–1727). Die ULA und IDR tragen **Subscription-Data (1400)** — einen sehr tief
  geschachtelten Grouped-AVP (APN-Configuration-Profile → APN-Configuration →
  EPS-Subscribed-QoS-Profile/AMBR, LCS-Info, Trace-Data, GPRS-Subscription-Data,
  EPS-Location-Information, MDT-Configuration, V2X-Subscription-Data …). Das ist die
  dominierende Modellierungsarbeit dieser Story.
- **Keine AVP-Duplikate:** gemeinsame AVPs werden referenziert (siehe Reused-AVP-Quellen),
  nicht neu definiert.
- **Experimental-Result-Codes** (Clause 7.4) als Konstanten in `S6aConstants`:
  - 4181 DIAMETER_AUTHENTICATION_DATA_UNAVAILABLE
  - 4182 DIAMETER_ERROR_CAMEL_SUBSCRIPTION_PRESENT
  - 5004 DIAMETER_ERROR_ROAMING_NOT_ALLOWED
  - 5420 DIAMETER_ERROR_UNKNOWN_EPS_SUBSCRIPTION
  - 5421 DIAMETER_ERROR_RAT_NOT_ALLOWED
  - 5422 DIAMETER_ERROR_EQUIPMENT_UNKNOWN
  - 5423 DIAMETER_ERROR_UNKNOWN_SERVING_NODE
  - (5001 DIAMETER_ERROR_USER_UNKNOWN — base; 5510/5513/5514 nur S13/Monitoring → out of scope)

## Acceptance Criteria

- [ ] ULR/ULA, CLR/CLA, AIR/AIA, IDR/IDA, PUR/PUA, NOR/NOA sind als Request/Answer mit
      `In`/`Out` implementiert und über `S6aMessageFactory` parse- und erzeugbar.
- [ ] Alle in diesen Command-Paaren vorkommenden AVPs (inkl. verschachtelter) sind im
      jeweils zuständigen `AVPProvider` registriert → verlustfreier Encode→Decode-Round-Trip.
- [ ] Keine AVP-Duplikate: gemeinsame AVPs werden aus `base`/`drmp`/`3gpp-common`
      (bzw. neuen, nach definierender Spec benannten Modulen) referenziert.
- [ ] `createAnswer` setzt `Auth-Session-State = NO_STATE_MAINTAINED`
      (TS 29.272 §7.1.3 Accounting nicht genutzt, §7.1.4 Sessions implizit terminiert).
- [ ] Tests (AssertJ, `it_<behavior>`, GIVEN/WHEN/THEN) für Provider, Factory und je
      Command-Paar mindestens ein Encode→Decode-Round-Trip inkl. eines befüllten
      Grouped-AVP (mind. einmal Subscription-Data-Teilbaum).

## Out of Scope (begründet)

- **S13/S13' (16777252) und S7a/S7d (16777308):** eigene Application-IDs, in AWBD-585 nicht
  gefordert. Damit auch ECR/ECA (324), UVR/UVA (8388638), CVR/CVA (8388642).
- **DSR/DSA (320), RSR/RSA (322):** laut AWBD-585 kein hartes Kriterium (eigene Story/Spec).
  AVPs, die *ausschließlich* hier vorkommen (z. B. DSR-Flags 1421, DSA-Flags 1422,
  Subscription-Data-Deletion 1685), entfallen — sofern nicht über Subscription-Data erreichbar.
- **OC-Supported-Features / OC-OLR (RFC 7683), Load (RFC 8583):** in der ABNF optional und mit
  **gelöschtem M-Bit** → unbekannt führt beim Decode nur zu Log-Warn, kein Fehler. Eigene
  IETF-Overload/Load-Domäne, für einen HSS nicht erforderlich. Später als eigene Module
  nachrüstbar.
- **Monitoring-Event-/SCEF-AVPs (TS 29.336), ProSe-Subscription-Data:** „Must not set" M-Bit
  bzw. nur in NOR/IDA optionale Tiefe; sofern M-Bit gelöscht und nicht zwingend erreichbar,
  beim Round-Trip unkritisch — Modellierungstiefe in 02-design zu fixieren.

## Assumptions

1. Implementierung folgt dem etablierten SGd/Gdd-Pattern:
   `Constants` → `AVPProvider` → `messages/` → `MessageFactory` → `mixins/`.
2. Grouped-AVPs flach modelliert (`HasServingNodeAVP`-Stil).
3. sparta-hss nutzt primär EPS-AKA (E-UTRAN-Vector); übrige Authentication-Vector-Varianten
   (UTRAN/GERAN) werden dennoch modelliert (volle ABNF-Treue).
4. Branch separat von Cx/Dx (`awbd-585-s6a-s6d`, von `main`); S6a/S6d ist unabhängig von Cx/Dx.

## Scope-Tiefe — aus `sparta-hss` abgeleitet (maßgeblich)

`sparta-hss` besitzt bereits eine eigene S6a-Implementierung
(`com.sipgate.sparta.protocol.diameter.s6a`), die AWBD-585 auf dieses Modul migriert. Sie
definiert **exakt**, welche AVPs der HSS produziert/konsumiert — das ist die maßgebliche
Scope-Grenze für die optionalen Zweige (Mandatory-AVPs ohnehin Pflicht):

- **ULR (decode):** User-Name, Visited-PLMN-Id, ULR-Flags, RAT-Type, UE-SRVCC-Capability,
  Terminal-Information {IMEI, Software-Version}, Supported-Features.
- **ULA / IDR (encode):** Subscription-Data, ULA-Flags / IDR-Flags, (IDR: User-Name).
- **AIR (decode):** User-Name, Visited-PLMN-Id, Requested-EUTRAN-Authentication-Info,
  Requested-UTRAN-GERAN-Authentication-Info {Number-Of-Requested-Vectors,
  Immediate-Response-Preferred, Re-Synchronization-Info}.
- **AIA (encode):** Authentication-Info {E-UTRAN-Vector, UTRAN-Vector, GERAN-Vector},
  Vendor-Specific-Application-Id.
- **CLR (encode):** CLR-Flags, User-Name, Cancellation-Type. **PUR/NOR:** User-Name.
- **Subscription-Data (HSS-Teilmenge):** Subscriber-Status, MSISDN, APN-OI-Replacement, AMBR,
  APN-Configuration-Profile, Network-Access-Mode, RAT-Frequency-Selection-Priority-ID,
  3GPP-Charging-Characteristics.
- **APN-Configuration-Profile → APN-Configuration → EPS-Subscribed-QoS-Profile →
  Allocation-Retention-Priority** vollständig; APN-Configuration mit Service-Selection,
  PDN-Type, MIP6-Agent-Info {MIP-Home-Agent-Address, MIP-Home-Agent-Host},
  SIPTO-/LIPA-Permission, VPLMN-Dynamic-Address-Allowed, PDN-GW-Allocation-Type, AMBR.

> **Hinweis Decode-Sicherheit (GOTCHA 5):** Der HSS *decodiert* nur die Requests (ULR, AIR, PUR,
> NOR) und die Answers zu HSS-initiierten Commands (IDA, CLA) — die großen Subscription-Data-
> Bäume werden vom HSS *encodiert* (ULA/IDR). Eingehende Nachrichten haben kleine AVP-Sets;
> daher entsteht durch Auslassen ungenutzter Subscription-Data-Zweige **kein** 5001-Decode-Risiko.

## Out of Scope — Subscription-Data-Zweige (vom HSS nicht genutzt)

LCS-Info, Trace-Data, GPRS-Subscription-Data (PDP-Context), CSG-Subscription-Data,
Teleservice-List, Call-Barring-Info, V2X-Subscription-Data(-Nr), MDT-Configuration(-NR),
ProSe-Subscription-Data, AESE-Communication-Pattern / Monitoring-Event-* (TS 29.336),
WLAN-offloadability, Adjacent-Access-Restriction-Data, IMSI-Group-Id, Emergency-Info,
eDRX-* u. a. — als bewusst „später nachrüstbar" markiert (kein HSS-Bedarf, keine
Decode-Pflicht). Erweiterung jederzeit additiv möglich.

## Offene Punkte (für 02-design)

1. **Reused-AVP-Zuordnung + Codes:** präzise Tabelle in `02-design.md` — je reused-AVP Zielmodul
   (`base` / `3gpp-common` / Fremd-Modul) und Code/Typ aus der **definierenden** Spec
   verifiziert (RFC 5778 Service-Selection, TS 29.212 RAT-Type/QCI/ARP/Bandwidth,
   RFC 5447 MIP6-Agent-Info, RFC 4004 MIP-Home-Agent-*, 3GPP-Charging-Characteristics).
   Strategie entschieden (Bestands-Konvention).
