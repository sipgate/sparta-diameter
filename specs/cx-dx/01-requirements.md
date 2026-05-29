# Cx/Dx Interface — Requirements

Jira: AWBD-585 — *sparta-diameter um S6a/S6d und Cx/Dx erweitern* (diese Spec deckt **nur Cx/Dx** ab; S6a/S6d bekommt eine eigene Spec).

## Value

Als Plattformbetreiber möchte ich, dass `sparta-diameter` das **Cx/Dx**-Interface
(S-/I-CSCF ↔ HSS) implementiert, damit **sparta-hss** als HSS-Implementierung für
IMS-Registrierung, Multimedia-Authentifizierung und Location-Info genutzt werden kann.

## Referenz

- **3GPP TS 29.229 / ETSI TS 129 229 v18.1.0** (Rel-18) — Protocol details: Command-Codes,
  AVP-Codes/Typen/Flags, Result-Codes. Lokal: `docs/specs/etsi/ts_129229_v18.1.0.pdf`.
- **3GPP TS 29.228 / ETSI TS 129 228 v18.0.0** (Rel-18) — Signalling flows & message contents.
  Lokal: `docs/specs/etsi/ts_129228_v18.0.0.pdf`.
- **IETF RFC 5090** — RADIUS Extension for Digest Authentication (definiert Digest-Realm/QoP/Algorithm/HA1;
  **obsoletet RFC 4590**, das von TS 29.229 noch referenziert wird).
- **IETF RFC 7155** — Diameter Network Access Server Application (definiert Framed-* AVPs;
  **obsoletet RFC 4005**, das von TS 29.229 noch referenziert wird).
- **ETSI ES 283 035 v3.2.1** — TISPAN NASS e2 interface (definiert Line-Identifier, AVP 500, Vendor 13019;
  aktuellste publizierte Version). Lokal: `docs/specs/etsi/es_283035v030201p.pdf`.

Application-ID Cx/Dx: **16777216**. Vendor 3GPP: **10415**. Vendor ETSI: **13019**.

## Scope — Commands (Acceptance Criteria)

Implementiert werden die drei Pflicht-Command-Paare:

| Command | Abk. | Code | Richtung |
|---|---|---|---|
| Server-Assignment-Request/Answer | SAR/SAA | 301 | S-CSCF → HSS |
| Multimedia-Auth-Request/Answer | MAR/MAA | 303 | S-CSCF → HSS |
| Registration-Termination-Request/Answer | RTR/RTA | 304 | HSS → S-CSCF |

Out of Scope (eigene Story/Spec, kein hartes Kriterium laut AWBD-585): UAR/UAA, LIR/LIA, PPR/PPA.

## Scope — AVPs

- **Volle ABNF-Treue**: jeder AVP, der in den drei Command-Paaren vorkommt — direkt oder
  verschachtelt in Grouped-AVPs — wird vollständig nach Spec modelliert (Definition + Mixin).
- **Keine AVP-Duplikate**: gemeinsam genutzte AVPs werden referenziert, nicht neu definiert:
  - aus `sparta-diameter-base` (RFC 6733): Session-Id, Vendor-Specific-Application-Id,
    Auth-Session-State, Origin/Destination-Host/Realm, User-Name, Result-Code,
    Experimental-Result(-Code), Failed-AVP, Proxy-Info, Route-Record, Vendor-Id u. a.
  - aus `sparta-diameter-ietf-drmp` (RFC 7944): DRMP
  - aus `sparta-diameter-3gpp-common` (TS 29.229): Supported-Features (628),
    Feature-List-ID (629), Feature-List (630)
- **Fremd-Namespace-AVPs** (genutzt in `SIP-Auth-Data-Item`/`SIP-Digest-Authenticate`)
  bekommen je definierende Spec ein eigenes, sprechend benanntes Modul (Konvention wie
  `ietf-drmp` / `3gpp-cxdx`):
  - **RFC 5090** ("RADIUS Extension for Digest Authentication", obsoletet RFC 4590) →
    `sparta-diameter-ietf-radius-digest-authentication`:
    Digest-Realm (104), Digest-QoP (110), Digest-Algorithm (111), Digest-HA1 (121).
    *(RFC 4740 §9.5.6 importiert diese AVPs nur — verifiziert.)*
  - **RFC 7155** ("Diameter Network Access Server Application", obsoletet RFC 4005) →
    `sparta-diameter-ietf-diameter-nas`: Framed-IP-Address (8, OctetString),
    Framed-Interface-Id (96, **Unsigned64**), Framed-IPv6-Prefix (97, OctetString)
  - **ETSI ES 283 035** ("TISPAN; NASS; e2 interface based on Diameter", Vendor 13019) →
    `sparta-diameter-etsi-e2`: Line-Identifier (500, OctetString, V/`mandatory=false`)

## Acceptance Criteria

- [ ] SAR/SAA, MAR/MAA, RTR/RTA sind als Request/Answer mit `In`/`Out` implementiert und
      über `CxDxMessageFactory` parse- und erzeugbar.
- [ ] Alle in diesen Command-Paaren vorkommenden AVPs (inkl. verschachtelter) sind im
      jeweils zuständigen `AVPProvider` registriert, sodass eine Nachricht verlustfrei
      encodiert → decodiert werden kann (Round-Trip).
- [ ] Keine AVP-Duplikate: gemeinsame AVPs werden aus `base`/`drmp`/`3gpp-common` referenziert.
- [ ] `createAnswer` setzt `Auth-Session-State = NO_STATE_MAINTAINED` (TS 29.229 §5.3).
- [ ] Tests (AssertJ, `it_<behavior>`, GIVEN/WHEN/THEN) für Provider, Factory und ein
      modulübergreifender AVP-Level Encode→Decode-Round-Trip, der die Registrierung aller
      verschachtelten AVPs über alle vier Module hinweg beweist.
      *(Ein Message-Wire-Round-Trip pro Command-Paar ist aus dem cxdx-Testpaket nicht möglich,
      da `Command.writeTo` package-private zu `base.core` ist; das vollständige Message-Encoding
      wird durch die Transport-Schicht abgedeckt. Der AVP-Level-Round-Trip über `AVP.writeTo`/
      `AVP.readFrom` prüft denselben rekursiven Grouped-Encode/Decode-Pfad und ist der
      kritische Registrierungs-Nachweis.)*

## Out of Scope (begründet)

- **OC-Supported-Features / OC-OLR (RFC 7683), Load (RFC 8583):** in der ABNF optional und
  mit **gelöschtem M-Bit** — unbekannt führt beim Decode nur zu einem Log-Warn, nicht zu
  einem Fehler. Eigene IETF-Domäne (Overload-/Load-Control), für einen HSS nicht erforderlich.
  Später als eigene, sprechend benannte Module nachrüstbar (z. B. `ietf-diameter-overload` für
  RFC 7683 DOIC, `ietf-diameter-load` für RFC 8583).
- AVPs, die ausschließlich in UAR/LIR/PPR vorkommen (z. B. Visited-Network-Identifier 600,
  Server-Capabilities 603, User-Authorization-Type 623, UAR-Flags 637, LIA-Flags 653).

## Assumptions

1. Implementierung folgt dem etablierten SGd/Gdd-Pattern:
   `Constants` → `AVPProvider` → `messages/` → `MessageFactory` → `mixins/`.
2. Grouped-AVPs werden wie im Bestandscode **flach** modelliert (`HasServingNodeAVP`-Stil):
   ein Mixin pro Grouped-AVP mit `set(List<AVP>)`/Getter auf `AVPContainer`; Leaf-AVPs getypt.
3. sparta-hss nutzt primär IMS-AKA; SIP-Digest/NASS-Bundled-AVPs werden dennoch modelliert
   (volle ABNF-Treue), liegen aber in den neuen RFC/ETSI-Modulen.
