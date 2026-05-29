package com.sipgate.sparta.diameter.spec;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupedAvpFormatParserTest {

    @Test
    void it_parses_rfc_6733_example() {
        // GIVEN
        final var input = """
            Example-AVP  ::= < AVP Header: 999999 >
                             { Origin-Host }
                           1*{ Session-Id }
                            *[ AVP ]
            """;

        // WHEN
        final Set<GroupedAvpDef> actual = GroupedAvpFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(new GroupedAvpDef(
            0,
            999999,
            "Example-AVP",
            List.of(),
            List.of(
                new AvpRule(1, 1, "Origin-Host"),
                new AvpRule(1, Long.MAX_VALUE, "Session-Id")
            ),
            List.of(new AvpRule(0, Long.MAX_VALUE, "AVP"))
        ));
    }

    @Test
    void it_parses_rfc6733_grouped_avps() {
        // GIVEN: some sample grouped AVPs from RFC 6733
        final var input = """
            Proxy-Info ::= < AVP Header: 284 >
                        { Proxy-Host }
                        { Proxy-State }
                      * [ AVP ]
            <Vendor-Specific-Application-Id> ::= < AVP Header: 260 >
                                           { Vendor-Id }
                                           [ Auth-Application-Id ]
                                           [ Acct-Application-Id ]
            <Failed-AVP> ::= < AVP Header: 279 >
                       1* {AVP}
            Experimental-Result ::= < AVP Header: 297 >
                                 { Vendor-Id }
                                 { Experimental-Result-Code }
            """;

        // WHEN
        final Set<GroupedAvpDef> actual = GroupedAvpFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new GroupedAvpDef(0, 284, "Proxy-Info",
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Proxy-Host"),
                    new AvpRule(1, 1, "Proxy-State")
                ),
                List.of(new AvpRule(0, Long.MAX_VALUE, "AVP"))
            ),
            new GroupedAvpDef(0, 260, "Vendor-Specific-Application-Id",
                List.of(),
                List.of(new AvpRule(1, 1, "Vendor-Id")),
                List.of(
                    new AvpRule(0, 1, "Auth-Application-Id"),
                    new AvpRule(0, 1, "Acct-Application-Id")
                )
            ),
            new GroupedAvpDef(0, 279, "Failed-AVP",
                List.of(),
                List.of(new AvpRule(1, Long.MAX_VALUE, "AVP")),
                List.of()
            ),
            new GroupedAvpDef(0, 297, "Experimental-Result",
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Vendor-Id"),
                    new AvpRule(1, 1, "Experimental-Result-Code")
                ),
                List.of()
            )
        );
    }

    @Test
    void it_parses_3gpp_ts_29_229_grouped_avps() {
        // GIVEN: some sample grouped AVPs from 3GPP TS 29.229
        final var input = """
Server-Capabilities ::= <AVP header: 603 10415>
*[Mandatory-Capability]
*[Optional-Capability]
*[Server-Name]
*[AVP]
Supported-Features ::=	< AVP header: 628 10415 >
{ Vendor-Id }
{ Feature-List-ID }
{ Feature-List }
*[AVP]
Supported-Applications ::=	< AVP header: 631 10415 >
  *[ Auth-Application-Id ]
  *[ Acct-Application-Id ]
  *[ Vendor-Specific-Application-Id ]
  *[ AVP ]
Associated-Identities ::= < AVP header: 632, 10415 >
*[ User-Name ]
*[ AVP ]
SIP-Digest-Authenticate ::= < AVP Header: 635 10415>
{ Digest-Realm }
 [ Digest-Algorithm ]
{ Digest-QoP }
{ Digest-HA1}
[ Alternate-Digest-Algorithm ]
[ Alternate-Digest-HA1 ]
*[ AVP ]
""";

        // WHEN
        final Set<GroupedAvpDef> actual = GroupedAvpFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new GroupedAvpDef(10415, 603, "Server-Capabilities",
                List.of(),
                List.of(),
                List.of(
                    new AvpRule(0, Long.MAX_VALUE, "Mandatory-Capability"),
                    new AvpRule(0, Long.MAX_VALUE, "Optional-Capability"),
                    new AvpRule(0, Long.MAX_VALUE, "Server-Name"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            ),
            new GroupedAvpDef(10415, 628, "Supported-Features",
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Vendor-Id"),
                    new AvpRule(1, 1, "Feature-List-ID"),
                    new AvpRule(1, 1, "Feature-List")
                ),
                List.of(new AvpRule(0, Long.MAX_VALUE, "AVP"))
            ),
            new GroupedAvpDef(10415, 631, "Supported-Applications",
                List.of(),
                List.of(),
                List.of(
                    new AvpRule(0, Long.MAX_VALUE, "Auth-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Acct-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Vendor-Specific-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            ),
            new GroupedAvpDef(10415, 632, "Associated-Identities",
                List.of(),
                List.of(),
                List.of(
                    new AvpRule(0, Long.MAX_VALUE, "User-Name"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            ),
            new GroupedAvpDef(10415, 635, "SIP-Digest-Authenticate",
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Digest-Realm"),
                    new AvpRule(1, 1, "Digest-QoP"),
                    new AvpRule(1, 1, "Digest-HA1")
                ),
                List.of(
                    new AvpRule(0, 1, "Digest-Algorithm"),
                    new AvpRule(0, 1, "Alternate-Digest-Algorithm"),
                    new AvpRule(0, 1, "Alternate-Digest-HA1"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            )
        );
    }
}
