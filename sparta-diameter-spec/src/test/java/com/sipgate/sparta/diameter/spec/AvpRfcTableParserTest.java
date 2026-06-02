package com.sipgate.sparta.diameter.spec;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.sipgate.sparta.diameter.spec.AvpFlagRule.MAY;
import static com.sipgate.sparta.diameter.spec.AvpFlagRule.MUST;
import static com.sipgate.sparta.diameter.spec.AvpFlagRule.MUST_NOT;
import static org.assertj.core.api.Assertions.assertThat;

class AvpRfcTableParserTest {

    @Test
    void it_recognizes_a_separator_line_that_uses_plus_instead_of_pipe() {
        // GIVEN: an RFC-style table whose separator line uses '+' as the
        // column-intersection character and contains no '|'. The data row
        // still uses '|' as the column divider.
        final var input = """
            --------------------------------------------------+----+----+
            DRMP                    301  9.1      Enumerated  | M  | V  |
            --------------------------------------------------+----+----+
            """;

        // WHEN
        final Set<AvpDef> actual = AvpRfcTableParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new AvpDef(0, 301, "DRMP", "Enumerated", MUST, MUST_NOT, false)
        );
    }

    @Test
    void it_defaults_an_unset_flag_to_MAY() {
        // GIVEN: a row where the M flag is in neither the MUST nor the
        // MUST NOT column — matches RFC 7944's DRMP definition.
        final var input = """
            --------------------------------------------------+----+----+
            DRMP                    301  9.1      Enumerated  |    | V  |
            --------------------------------------------------+----+----+
            """;

        // WHEN
        final Set<AvpDef> actual = AvpRfcTableParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new AvpDef(0, 301, "DRMP", "Enumerated", MAY, MUST_NOT, false)
        );
    }

    @Test
    void it_parses_rfc4005_table() {
        // GIVEN: representative rows from RFC 4005 §6 — five flag
        // columns (MUST, MAY, SHLD NOT, MUST NOT, Encr) instead of
        // RFC 6733's two; short-form IPFltrRule / QoSFltrRule type
        // aliases; the Configuration-Token row carries P,V in MUST
        // NOT; QoS-Filter-Rule leaves every column empty.
        final var input = """
            -----------------------------------------|----+-----+----+-----|----|
            Service-Type       6   6.1    Enumerated | M  |  P  |    |  V  | Y  |
            NAS-Filter-Rule  400   6.6    IPFltrRule | M  |  P  |    |  V  | Y  |
            Configuration-    78   6.8    OctetString| M  |     |    | P,V |    |
              Token                                  |    |     |    |     |    |
            QoS-Filter-Rule  407   6.9    QoSFltrRule|    |     |    |     |    |
            Framed-           13  6.10.4  Enumerated | M  |  P  |    |  V  | Y  |
              Compression                            |    |     |    |     |    |
            -----------------------------------------|----+-----+----+-----|----|
            """;

        // WHEN
        final Set<AvpDef> actual = AvpRfcTableParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new AvpDef(0, 6, "Service-Type", "Enumerated", MUST, MUST_NOT, true),
            new AvpDef(0, 400, "NAS-Filter-Rule", "IPFilterRule", MUST, MUST_NOT, true),
            new AvpDef(0, 78, "Configuration-Token", "OctetString", MUST, MUST_NOT, false),
            new AvpDef(0, 407, "QoS-Filter-Rule", "QoSFilterRule", MAY, MAY, false),
            new AvpDef(0, 13, "Framed-Compression", "Enumerated", MUST, MUST_NOT, true)
        );
    }

    @Test
    void it_parses_rfc6733_table() {
        // GIVEN: representative rows from RFC 6733 §4.5 — single-line,
        // multi-line name, "V,M" comma-separated mustNot column,
        // Inband-Security-Id's awkward layout (name+flags on line 1,
        // code+section+type on line 2).
        final var input = """
            -----------------------------------------|----+-----|
            Acct-             85  9.8.2   Unsigned32 | M  |  V  |
              Interim-Interval                       |    |     |
            Acct-             44  9.8.4   OctetString| M  |  V  |
             Session-Id                              |    |     |
            Class             25  8.20    OctetString| M  |  V  |
            Error-Message    281  7.3     UTF8String |    | V,M |
            Inband-Security                          | M  |  V  |
               -Id           299  6.10    Unsigned32 |    |     |
            Origin-Host      264  6.3     DiamIdent  | M  |  V  |
            User-Name          1  8.14    UTF8String | M  |  V  |
            Vendor-Specific- 260  6.11    Grouped    | M  |  V  |
               Application-Id                        |    |     |
            -----------------------------------------|----+-----|
            """;

        // WHEN
        final Set<AvpDef> actual = AvpRfcTableParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new AvpDef(0, 85, "Acct-Interim-Interval", "Unsigned32", MUST, MUST_NOT, false),
            new AvpDef(0, 44, "Acct-Session-Id", "OctetString", MUST, MUST_NOT, false),
            new AvpDef(0, 25, "Class", "OctetString", MUST, MUST_NOT, false),
            new AvpDef(0, 281, "Error-Message", "UTF8String", MUST_NOT, MUST_NOT, false),
            new AvpDef(0, 299, "Inband-Security-Id", "Unsigned32", MUST, MUST_NOT, false),
            new AvpDef(0, 264, "Origin-Host", "DiameterIdentity", MUST, MUST_NOT, false),
            new AvpDef(0, 1, "User-Name", "UTF8String", MUST, MUST_NOT, false),
            new AvpDef(0, 260, "Vendor-Specific-Application-Id", "Grouped", MUST, MUST_NOT, false)
        );
    }
}
