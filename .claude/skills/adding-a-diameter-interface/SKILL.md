---
name: adding-a-diameter-interface
description: Use when adding a new Diameter interface/application to sparta-diameter or modelling new 3GPP/ETSI/IETF AVPs from spec PDFs (e.g. S6a/S6d, Cx/Dx, any TS 29.xxx interface and its AVPs). Covers fetching ETSI/3GPP specs, verifying AVP definitions, module layout, and the encode/decode gotchas.
---

# Adding a Diameter Interface

## Overview

New interfaces mirror the **SGd/Gdd module pattern**: `Constants` → `AVPProvider` →
`messages/` (Request/Answer with `In`/`Out`) → `MessageFactory` → `mixins/`. Work is
**spec-driven** (ADR-0002): write `specs/<feature>/` first, implement second. Providers and
factories are auto-discovered by a Reflections scan over `com.sipgate.sparta.diameter` — a new
module only needs to be on the classpath and listed in the parent `pom.xml` `<modules>`.

Use `sparta-diameter-3gpp-sgdgdd` as the live reference implementation for every file shape.

## When to use

- Filling an empty interface stub (`3gpp-s6a`, `3gpp-cxdx`, …) or adding a brand-new one.
- Adding AVPs defined by a 3GPP TS / ETSI ES / IETF RFC.

## Process

1. **Scope: one interface per spec.** If a ticket spans two interfaces (e.g. S6a/S6d *and*
   Cx/Dx), split into `specs/<a>/` and `specs/<b>/` and do one at a time (ADR-0002).
2. **Get the spec PDFs** (see *Fetching specs* below). For 3GPP interfaces you usually need
   the *protocol details* doc (AVP codes/types/flags, e.g. TS 29.229) **and** the *signalling
   flows / message contents* doc (which AVPs per message, e.g. TS 29.228).
3. **Extract the facts** from the AVP table and command ABNF: Application-ID, command codes,
   each AVP's code + type + **M/V flags**, Experimental-Result values, grouped-AVP nesting.
4. **Verify every borrowed AVP recursively** (see GOTCHA 2 & 3) — defining document and
   deprecation status.
5. **Write `specs/<feature>/01-requirements.md` and `02-design.md`**, get review, then produce
   `03-tasks.md`. Mark anything you could not pin down as an explicit "open item".
6. **Implement** mirroring SGd/Gdd, then wire the module into the parent pom.
7. **Test** every provider/factory/message (AssertJ, `it_<behavior>`, GIVEN/WHEN/THEN, encode→
   decode round-trip incl. a populated grouped AVP).

## GOTCHAS (these bit us — do not repeat)

### 1. Name modules/packages by interface or protocol, never by spec number
Convention: `sparta-diameter-<sdo>-<short-interface-or-protocol-name>`. Look at existing siblings.
- ✅ `ietf-drmp`, `3gpp-cxdx`, `ietf-radius-digest-authentication`, `ietf-diameter-nas`, `etsi-e2`
- ❌ `ietf-rfc4740`, `ietf-rfc4005`, `etsi-es283035`
The name must be human-readable; accuracy alone (a number) is not enough. If several readable
names fit, ask rather than guessing a number-based one.

### 2. "Defined in RFC X" is often an *import* — follow it to the real source
3GPP specs frequently say an AVP is "defined in RFC X" when X only *imports* it. Verify in the
actual document. Example: TS 29.229 §6.3.37 says Digest-* are "defined in RFC 4740", but RFC
4740 §9.5.6 states they are defined in the **RADIUS Digest** spec and merely imported. Always
confirm code + type + flags in the document that actually assigns them.

### 3. Check for deprecated / obsoleted documents — use the current one
RADIUS/Diameter RFCs get obsoleted often. Verify before citing (the `Obsoletes:`/`Obsoleted by`
header). We hit: **RFC 4590 → obsoleted by RFC 5090**; **RFC 4005 → obsoleted by RFC 7155**
(codes/types unchanged, but cite the current RFC). Search online if unsure.

### 4. Grouped AVPs stay flat — NO child getters/setters
A grouped AVP gets exactly **one** flat mixin: `set(List<AVP>)` + a getter returning
`AVPContainer` (pattern: `HasServingNodeAVP`). Do **not** generate accessors for its children.
A `Has<Name>AVP` mixin exists **only for AVPs that appear directly at message level** (incl.
top-level grouped AVPs). AVPs that occur **only nested** get an `AVPDefinition` in the provider
(so they round-trip) but **no mixin** — exactly like SGd/Gdd's `SM-Enumerated-Delivery-Failure-Cause`
(defined, no mixin).

### 5. Register EVERY AVP that can appear, including nested ones
Decoder behaviour (`AVP.readFrom`): an unknown AVP with the **M-bit set throws 5001**; without
M-bit it is only logged. Grouped children are parsed recursively. So every AVP reachable in a
command pair — directly or nested in any grouped AVP — must have an `AVPDefinition`, or decoding
a real message fails. Walk the grouped trees to the leaves.

### 6. Read flags from the AVP table — don't assume
Take M/V from the spec's AVP-flag table per AVP. `M,V` ⇒ `AVPDefinition(..., mandatory=true,
vendorSpecific=true, vendorId)`; `V` only ⇒ `mandatory=false`. We wrongly assumed Line-Identifier
was `M,V`; ES 283 035 §7.3.5 has it `V` only (`mandatory=false`).

### 7. Get the Java type right (incl. the rare ones)
| Diameter type | Java |
|---|---|
| OctetString | `byte[]` |
| UTF8String / DiameterURI / DiameterIdentity | `String` |
| Unsigned32 | `Long` |
| **Unsigned64** | **`BigInteger`** |
| Enumerated | `Integer` |
| Time | `Date` |
| Address | `InetAddress` |
| Grouped | `GroupedAVP` |

We wrongly typed Framed-Interface-Id as OctetString; RFC 7155 defines it **Unsigned64**.

### 8. No AVP duplicates — reuse, don't redefine
Reference shared AVPs instead of redefining: RFC 6733 base AVPs from `sparta-diameter-base`
(`CoreAVPProvider`), DRMP from `ietf-drmp`, and the common 3GPP AVPs (Supported-Features 628,
Feature-List(-ID) 629/630, etc.) from `sparta-diameter-3gpp-common`. Foreign-namespace AVPs
(non-RFC-6733 IETF, or non-3GPP vendors) get their **own** module per defining spec.

### 9. Auth-Session-State for sessionless interfaces
If the spec's "Use of sessions" / "Accounting functionality" clauses say accounting is not used
and sessions are implicitly terminated (typical: Cx/Dx, S6c, SGd/Gdd), `createAnswer` must set
`Auth-Session-State = AUTH_SESSION_STATE_NOT_MAINTAINED`. Verify the clause; don't assume.

### 10. Wire the module
Add the new module(s) to the parent `pom.xml` `<modules>`. Copy a sibling pom (`sgdgdd`/`drmp`):
depend on what you reference (`3gpp-common`, `ietf-drmp`, any new foreign-AVP modules) plus
`junit-jupiter` + `assertj-core` (test scope). Discovery is automatic via Reflections.

## Fetching specs

ETSI publishes 3GPP specs (TS 29.229 = ETSI TS 129 229) as a **browsable directory tree** under
`https://www.etsi.org/deliver/`. Browse it directly — no search service needed — but **ETSI
returns HTTP 403 (Cloudflare) without a browser `User-Agent`** (GOTCHA: this also applies to the
PDF download). Path layout:

```
deliver/etsi_ts/<range>/<docnum>/<version>_NN/<file>.pdf
                 ^ docnum floored to /100, e.g. 129229 -> 129200_129299
```
Use `etsi_ts` for a TS, `etsi_es` for an ES (e.g. ES 283 035 → `etsi_es/283000_283099/283035/`).

```bash
UA="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
BASE="https://www.etsi.org/deliver/etsi_ts/129200_129299/129229"   # etsi_ts|etsi_es + range + docnum

# 1. List available versions (newest last) — pick the one for your target Release
curl -sSL -A "$UA" "$BASE/" | grep -oE '[0-9]{2}\.[0-9]{2}\.[0-9]{2}_[0-9]{2}/' | sort -u | tail

# 2. Read the actual PDF filename from the version dir (works for TS and ES) and download it
V="18.01.00_60"
PDF=$(curl -sSL -A "$UA" "$BASE/$V/" | grep -oiE '[a-z]+_[0-9]+v[0-9]+[a-z]\.pdf' | head -1)
curl -sSL -A "$UA" -H "Accept: application/pdf,*/*" -o "docs/specs/etsi/$PDF" "$BASE/$V/$PDF"

# 3. Extract text (the Read tool needs poppler/pdftoppm; pypdf is reliable and present)
python3 - "docs/specs/etsi/$PDF" <<'PY'
import sys, pypdf
src = sys.argv[1]; r = pypdf.PdfReader(src)
with open(src.replace(".pdf",".txt"),"w") as f:
    for i,p in enumerate(r.pages):
        f.write(f"\n===== PAGE {i+1} =====\n"); f.write(p.extract_text() or "")
PY
```

**Full-text search** (only when you don't know the document number — "which spec defines AVP X"):
`getsi.org` indexes the same ETSI PDFs — `curl -sSL "https://getsi.org/?q=body:Line-Identifier"`
or `?q=title:29.229`; it returns the `etsi.org/deliver/...` URLs you then fetch as above. Its
version index can lag (it may miss the newest version), so once you know the document number,
treat the deliver tree above as authoritative for *which versions exist*.

RFCs: `curl -sSL https://www.rfc-editor.org/rfc/rfc5090.txt` (no UA needed). Grep the
`Obsoletes:` header and the AVP code listing.

Keep downloaded spec PDFs under `docs/specs/etsi/` and reference them from the spec.

## Project conventions (AGENTS.md / ADRs)

- `final` on every field, parameter, local. For-loops only (no streams) in production code.
- Tests: `it_<behavior>`, GIVEN/WHEN/THEN blocks, AssertJ only, instance named by role
  (`provider`, `factory`, `command`).
- Specs live in `specs/<feature>/` (ADR-0002); per-package message factories (ADR-0006).
- Build with `mvn` directly (no wrapper).

## Common mistakes

| Mistake | Fix |
|---|---|
| Module named `ietf-rfc4740` | Name by protocol/interface: `ietf-radius-digest-authentication` |
| Trusting "defined in RFC X" | Open RFC X; it may only import — find the real defining doc |
| Citing an obsoleted RFC | Check `Obsoletes`/`Obsoleted by`; use the current one |
| Child getters on grouped AVPs | Flat mixin only; children get definitions, no mixins |
| Skipping nested AVP definitions | Register every reachable AVP or M-bit ones fail decode (5001) |
| Assuming M/V flags or types | Read the spec's AVP-flag table per AVP; Unsigned64 → BigInteger |
| Redefining shared AVPs | Reuse base/drmp/3gpp-common; new module only for foreign namespaces |
| `curl` ETSI PDF → 403 | Add a browser `User-Agent` header |
