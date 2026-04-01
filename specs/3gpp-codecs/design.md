# Design: 3GPP Codecs

## Package

All codecs live in `sparta-diameter-3gpp-common`:

```
com.sipgate.sparta.diameter._3gpp.common.codec.TbcdCodec
com.sipgate.sparta.diameter._3gpp.common.codec.Gsm7BitCodec
com.sipgate.sparta.diameter._3gpp.common.codec.AddressString
com.sipgate.sparta.diameter._3gpp.common.codec.NumericAddressString
com.sipgate.sparta.diameter._3gpp.common.codec.AlphanumericAddressString
com.sipgate.sparta.diameter._3gpp.common.codec.PlmnId
com.sipgate.sparta.diameter._3gpp.common.codec.DiameterFlag
com.sipgate.sparta.diameter._3gpp.common.codec.DiameterFlags
```

Flag enums live alongside their AVP mixins in the relevant module, e.g.:

```
com.sipgate.sparta.diameter._3gpp.s6a.avp.UlrFlag
com.sipgate.sparta.diameter._3gpp.s6a.avp.UlaFlag
```

## TbcdCodec

Static utility class, no instances.

```java
final class TbcdCodec {
    private TbcdCodec() {}

    static byte[] encode(String digits);  // throws IllegalArgumentException
    static String decode(byte[] tbcd);
}
```

Encoding: iterate digit pairs; write `(d2 << 4) | d1`; pad last nibble with `0xF`
if the digit count is odd.

Decoding: for each byte extract low nibble then high nibble; stop if a nibble is `0xF`.

## Gsm7BitCodec

Static utility class, no instances.

```java
final class Gsm7BitCodec {
    private Gsm7BitCodec() {}

    static byte[] encode(String text);                        // throws IllegalArgumentException
    static String decode(byte[] packed, int characterCount);  // throws IllegalArgumentException
}
```

The codec holds a private lookup table mapping the 128 GSM 7-bit code points to
their Unicode equivalents and back. No escape sequences. No extension table.

Encoding packs characters LSB-first: character `n` contributes its 7 bits starting
at bit position `(n * 7) % 8` in the output byte stream.

Decoding reverses the bit extraction using `characterCount` to know when to stop.

## AddressString

Sealed interface with two record implementations.

```java
sealed interface AddressString permits NumericAddressString, AlphanumericAddressString {
    byte ton();
    byte npi();
    String value();
    byte[] encode();

    static AddressString decode(byte[] bytes);  // throws IllegalArgumentException
}
```

`decode` reads byte 0: `ton = (b & 0x70) >> 4`, `npi = b & 0x0F`, dispatches on TON:

```java
record NumericAddressString(byte ton, byte npi, String value)
        implements AddressString {
    // encode: TON/NPI byte (0x80 | ton << 4 | npi) + TbcdCodec.encode(value)
}

record AlphanumericAddressString(byte npi, String value)
        implements AddressString {
    byte ton() { return 0b101; }
    // encode: TON/NPI byte + Gsm7BitCodec.encode(value)
    // character count on decode: floor(payloadBytes.length * 8 / 7)
}
```

## PlmnId

```java
record PlmnId(String mcc, String mnc) {
    // compact constructor validates:
    //   mcc.length() == 3, all digits
    //   mnc.length() == 2 or 3, all digits

    static PlmnId decode(byte[] bytes);  // throws IllegalArgumentException
    byte[] encode();
}
```

Wire layout (3GPP TS 24.008 §10.5.1.13):

| Byte | High nibble       | Low nibble        |
|------|-------------------|-------------------|
| 0    | MCC digit 2       | MCC digit 1       |
| 1    | MNC digit 3 / 0xF | MCC digit 3       |
| 2    | MNC digit 2       | MNC digit 1       |

`0xF` in MNC digit 3 position → 2-digit MNC.

## Bitmask Flags

Common marker interface for all 3GPP flag enums:

```java
interface DiameterFlag {
    int bitPosition();  // 0 = LSB
}
```

Utility for encode/decode (static methods, no instances):

```java
final class DiameterFlags {
    private DiameterFlags() {}

    static <F extends Enum<F> & DiameterFlag> EnumSet<F> decode(int raw, Class<F> type);
    static <F extends Enum<F> & DiameterFlag> int encode(EnumSet<F> flags);
}
```

`decode` iterates `type.getEnumConstants()`, tests each bit, builds the set.
`encode` reduces the set to an `int` by OR-ing `1 << f.bitPosition()` for each member.

Per-AVP flag enum example:

```java
enum UlrFlag implements DiameterFlag {
    SINGLE_REGISTRATION_INDICATION(0),
    S6A_S6D_INDICATOR(1),
    SKIP_SUBSCRIBER_DATA(2),
    GPRS_SUBSCRIPTION_REQUIRED(3),
    // ...
    ;

    private final int bitPosition;

    UlrFlag(final int bitPosition) {
        this.bitPosition = bitPosition;
    }

    @Override
    public int bitPosition() {
        return bitPosition;
    }
}
```

## AVP Mixin Integration

Typed accessors are default methods added to existing mixins, delegating through
the codec. Existing `byte[]` accessors are unchanged.

Example for `HasMsisdnAVP`:

```java
default AddressString getMsisdnAsAddressString() {
    final var raw = getMsisdn();
    return raw != null ? AddressString.decode(raw) : null;
}

default T setMsisdn(final AddressString address) {
    return setMsisdn(address.encode());
}
```

The same pattern applies to `HasScAddressAVP`, `HasSmsGmscAddressAVP`,
`HasMmeNumberForMtSmsAVP`, and `HasSgsnNumberAVP`.

Flag mixin example:

```java
default EnumSet<UlrFlag> getUlrFlagsTyped() {
    return DiameterFlags.decode(getUlrFlags(), UlrFlag.class);
}

default T setUlrFlags(final EnumSet<UlrFlag> flags) {
    return setUlrFlags(DiameterFlags.encode(flags));
}
```
