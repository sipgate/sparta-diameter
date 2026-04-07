# Requirements: 3GPP Codecs

## Context

Several 3GPP Diameter AVPs carry structured data encoded in ways not defined by the
base Diameter spec: TBCD-packed phone numbers, GSM 7-bit alphanumeric strings,
MAP-style address envelopes, packed PLMN identifiers, and bitfield flag registers.
Currently these AVPs expose raw `byte[]` or `int`, forcing every consumer to implement
the encoding themselves.

This feature introduces codecs for all 3GPP-specific encodings used in AVPs within
this library, and updates the affected AVP mixins to expose clean typed accessors
alongside the existing raw ones.

## Scope

**In scope:**
- TBCD encoding/decoding (phone digits)
- GSM 7-bit basic alphabet encoding/decoding (alphanumeric addresses only — no
  extension tables, no national language variants)
- AddressString (MAP ISDN-AddressString: TON/NPI byte + TBCD or GSM 7-bit payload)
- PLMN-Id (MCC + MNC packed into 3 bytes)
- Bitmask flag AVPs (typed `EnumSet` accessors for 3GPP `Unsigned32` flag fields)

**Out of scope:**
- GSM 7-bit extension table / language shift tables
- Authentication vectors (RAND, XRES, AUTN, KASME) — opaque binary
- SM-RP-UI / TPDU internals — separate SMS protocol layer
- Diameter Time — already handled in base

## REQ-01 — TBCD Codec

1. `encode(String)` packs digit characters (`0`–`9`) into bytes, two per byte, lower nibble first.
2. An odd number of digits is padded with a `0xF` nibble in the high nibble of the last byte.
3. `decode(byte[])` reverses the encoding, stripping any trailing `0xF` padding nibble.
4. Non-digit characters in the input to `encode` are rejected with `IllegalArgumentException`.
5. Empty input encodes to an empty byte array and decodes back to an empty string.

## REQ-02 — GSM 7-bit Codec (basic alphabet only)

1. `encode(String)` maps each character to its 7-bit code point and packs the bits
   into bytes, LSB-first, 7 bits per character.
2. `decode(byte[], int characterCount)` unpacks the bit stream and maps each 7-bit
   code point back to a character.
3. Only the 128-character GSM 7-bit default alphabet table is supported. Characters
   outside the table are rejected with `IllegalArgumentException` on encode;
   unrecognised code points on decode throw `IllegalArgumentException`.
4. `characterCount` is a required parameter because byte count alone does not
   determine the number of encoded characters.
5. No extension table. No language tables. No escape sequence handling.

## REQ-03 — AddressString

1. An `AddressString` carries: TON (3 bits), NPI (4 bits), and a string value.
2. `decode(byte[])` reads the first byte as the TON/NPI octet (bit 7 always 1 per
   MAP spec), then decodes the remaining bytes:
   - TON = `0b101` (alphanumeric): payload decoded as GSM 7-bit; character count
     derived from byte count as `floor(byteCount * 8 / 7)`.
   - All other TON values: payload decoded as TBCD.
3. `encode()` produces the TON/NPI byte (bit 7 set) followed by the encoded payload.
4. For numeric addresses the value is a digit string (e.g. `"4915123456"`).
5. For alphanumeric addresses the value is a plain string (e.g. `"MyBank"`).
6. The international prefix `+` is NOT part of the encoded value — it is a
   presentation concern for callers.

## REQ-04 — PLMN-Id

1. A `PlmnId` carries an MCC (exactly 3 decimal digits) and an MNC (2 or 3 decimal
   digits).
2. Wire encoding is 3 bytes per 3GPP TS 24.008 §10.5.1.13:
   - Byte 0: MCC digit 2 (high nibble) | MCC digit 1 (low nibble)
   - Byte 1: MNC digit 3 or `0xF` for 2-digit MNC (high nibble) | MCC digit 3 (low nibble)
   - Byte 2: MNC digit 2 (high nibble) | MNC digit 1 (low nibble)
3. `decode(byte[])` reconstructs MCC and MNC strings; a `0xF` third MNC nibble means
   2-digit MNC.
4. `encode()` produces exactly 3 bytes.
5. MCC or MNC containing non-digit characters or wrong length are rejected with
   `IllegalArgumentException`.

## REQ-05 — Bitmask Flag AVPs

1. Each 3GPP flag AVP (e.g. Feature-List, ULR-Flags, IDA-Flags) exposes typed
   accessors using an `EnumSet` of a per-AVP flag enum.
2. Each enum constant encodes its bit position (0 = LSB).
3. The mixin getter decodes the raw `Unsigned32` value into an `EnumSet` of the
   flags present.
4. The mixin setter accepts an `EnumSet` and writes the corresponding `Unsigned32`.
5. Raw `int` access is preserved alongside the typed accessors for wire-level use.

## REQ-06 — AVP Mixin Integration

1. All AVP mixins whose AVPs carry one of the above encodings gain typed accessor
   pairs. The typed accessors are additive — existing raw accessors remain unchanged.
2. Address AVPs that gain typed accessors:
   - `HasMsisdnAVP`
   - `HasScAddressAVP`
   - `HasSmsGmscAddressAVP`
   - `HasMmeNumberForMtSmsAVP`
   - `HasSgsnNumberAVP`
3. PLMN-Id AVPs gain typed accessors when introduced.
4. Flag AVPs each gain an `EnumSet`-based accessor pair as defined in REQ-05.
