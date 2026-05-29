package com.sipgate.sparta.diameter.spec;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommandCodeFormatParserTest {

    @Test
    void it_parses_rfc6733_example() {
        // GIVEN
        final var input = """
            Example-Request ::= < Diameter Header: 9999999, REQ, PXY >
                               { User-Name }
                            1* { Origin-Host }
                             * [ AVP ]
            """;

        // WHEN
        final Set<CommandDef> actual = CommandCodeFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new CommandDef(0, 9999999, "Example-Request", true, true, false,
                List.of(),
                List.of(
                    new AvpRule(1, 1, "User-Name"),
                    new AvpRule(1, Long.MAX_VALUE, "Origin-Host")
                ),
                List.of(new AvpRule(0, Long.MAX_VALUE, "AVP"))
            ));
    }

    @Test
    void it_parses_rfc6733_commands() {
        // GIVEN: some sample CCFs from RFC 6733
        final var input = """
            <CER> ::= < Diameter Header: 257, REQ >
                      { Origin-Host }
                      { Origin-Realm }
                   1* { Host-IP-Address }
                      { Vendor-Id }
                      { Product-Name }
                      [ Origin-State-Id ]
                    * [ Supported-Vendor-Id ]
                    * [ Auth-Application-Id ]
                    * [ Inband-Security-Id ]
                    * [ Acct-Application-Id ]
                    * [ Vendor-Specific-Application-Id ]
                      [ Firmware-Revision ]
                    * [ AVP ]
            <CEA> ::= < Diameter Header: 257 >
                      { Result-Code }
                      { Origin-Host }
                      { Origin-Realm }
                   1* { Host-IP-Address }
                      { Vendor-Id }
                      { Product-Name }
                      [ Origin-State-Id ]
                      [ Error-Message ]
                      [ Failed-AVP ]
                    * [ Supported-Vendor-Id ]
                    * [ Auth-Application-Id ]
                    * [ Inband-Security-Id ]
                    * [ Acct-Application-Id ]
                    * [ Vendor-Specific-Application-Id ]
                      [ Firmware-Revision ]
                    * [ AVP ]
            <DPR>  ::= < Diameter Header: 282, REQ >
                       { Origin-Host }
                       { Origin-Realm }
                       { Disconnect-Cause }
                     * [ AVP ]
            <DPA>  ::= < Diameter Header: 282 >
                       { Result-Code }
                       { Origin-Host }
                       { Origin-Realm }
                       [ Error-Message ]
                       [ Failed-AVP ]
                     * [ AVP ]
            """;

        // WHEN
        final Set<CommandDef> actual = CommandCodeFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new CommandDef(0, 257, "CER", true, false, false,
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm"),
                    new AvpRule(1, Long.MAX_VALUE, "Host-IP-Address"),
                    new AvpRule(1, 1, "Vendor-Id"),
                    new AvpRule(1, 1, "Product-Name")
                ),
                List.of(
                    new AvpRule(0, 1, "Origin-State-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Vendor-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Auth-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Inband-Security-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Acct-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Vendor-Specific-Application-Id"),
                    new AvpRule(0, 1, "Firmware-Revision"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            ),
            new CommandDef(0, 257, "CEA", false, false, false,
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Result-Code"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm"),
                    new AvpRule(1, Long.MAX_VALUE, "Host-IP-Address"),
                    new AvpRule(1, 1, "Vendor-Id"),
                    new AvpRule(1, 1, "Product-Name")
                ),
                List.of(
                    new AvpRule(0, 1, "Origin-State-Id"),
                    new AvpRule(0, 1, "Error-Message"),
                    new AvpRule(0, 1, "Failed-AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Vendor-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Auth-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Inband-Security-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Acct-Application-Id"),
                    new AvpRule(0, Long.MAX_VALUE, "Vendor-Specific-Application-Id"),
                    new AvpRule(0, 1, "Firmware-Revision"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            ),
            new CommandDef(0, 282, "DPR", true, false, false,
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm"),
                    new AvpRule(1, 1, "Disconnect-Cause")
                ),
                List.of(new AvpRule(0, Long.MAX_VALUE, "AVP"))
            ),
            new CommandDef(0, 282, "DPA", false, false, false,
                List.of(),
                List.of(
                    new AvpRule(1, 1, "Result-Code"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm")
                ),
                List.of(
                    new AvpRule(0, 1, "Error-Message"),
                    new AvpRule(0, 1, "Failed-AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP")
                )
            )
        );
    }

    @Test
    void it_parses_3gpp_ts_29_229_commands() {
        // GIVEN: some sample CCFs from 3GPP TS 29.229
        final var input = """
< User-Authorization-Request> ::=	< Diameter Header: 300, REQ, PXY, 16777216 >
< Session-Id >
[ DRMP ]
{ Vendor-Specific-Application-Id }
{ Auth-Session-State }
{ Origin-Host }
{ Origin-Realm }
[ Destination-Host ]
{ Destination-Realm }
{ User-Name }
[ OC-Supported-Features ]
*[ Supported-Features ]
{ Public-Identity }
{ Visited-Network-Identifier }
[ User-Authorization-Type ]
[ UAR-Flags ]
*[ AVP ]
*[ Proxy-Info ]
*[ Route-Record ]
< User-Authorization-Answer> ::=	< Diameter Header: 300, PXY, 16777216 >
< Session-Id >
[ DRMP ]
{ Vendor-Specific-Application-Id }
[ Result-Code ]
[ Experimental-Result ]
{ Auth-Session-State }
{ Origin-Host }
{ Origin-Realm }
[ OC-Supported-Features ]
[ OC-OLR ]
*[ Load ]
*[ Supported-Features ]
[ Server-Name ]
[ Server-Capabilities ]
*[ AVP ]
[ Failed-AVP ]
*[ Proxy-Info ]
*[ Route-Record ]
<Server-Assignment-Request> ::=	< Diameter Header: 301, REQ, PXY, 16777216 >
< Session-Id >
[ DRMP ]
{ Vendor-Specific-Application-Id }
{ Auth-Session-State }
{ Origin-Host }
{ Origin-Realm }
[ Destination-Host ]
{ Destination-Realm }
[ User-Name ]
[ OC-Supported-Features ]
*[ Supported-Features ]
*[ Public-Identity ]
[ Wildcarded-Public-Identity ]
{ Server-Name }
{ Server-Assignment-Type }
{ User-Data-Already-Available }
[ SCSCF-Restoration-Info ]
[ Multiple-Registration-Indication ]
[ Session-Priority ]
[ SAR-Flags ]
[ Failed-PCSCF ]
*[ AVP ]
*[ Proxy-Info ]
*[ Route-Record ]
<Server-Assignment-Answer> ::=	< Diameter Header: 301, PXY, 16777216 >
	< Session-Id >
[ DRMP ]
{ Vendor-Specific-Application-Id }
[ Result-Code ]
[ Experimental-Result ]
{ Auth-Session-State }
{ Origin-Host }
{ Origin-Realm }
[ User-Name ]
[ OC-Supported-Features ]
[ OC-OLR ]
*[ Load ]
*[ Supported-Features ]
[ User-Data ]
[ Charging-Information ]
[ Associated-Identities ]
[ Loose-Route-Indication ]
*[ SCSCF-Restoration-Info ]
[ Associated-Registered-Identities ]
[ Server-Name ]
[ Wildcarded-Public-Identity ]
[ Priviledged-Sender-Indication ]
[ Allowed-WAF-WWSF-Identities ]
*[ AVP ]
[ Failed-AVP ]
*[ Proxy-Info ]
*[ Route-Record ]
            """;

        // WHEN
        final Set<CommandDef> actual = CommandCodeFormatParser.parse(input);

        // THEN
        assertThat(actual).containsOnly(
            new CommandDef(16777216, 300, "User-Authorization-Request", true, true, false,
                List.of(new AvpRule(1, 1, "Session-Id")),
                List.of(
                    new AvpRule(1, 1, "Vendor-Specific-Application-Id"),
                    new AvpRule(1, 1, "Auth-Session-State"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm"),
                    new AvpRule(1, 1, "Destination-Realm"),
                    new AvpRule(1, 1, "User-Name"),
                    new AvpRule(1, 1, "Public-Identity"),
                    new AvpRule(1, 1, "Visited-Network-Identifier")
                ),
                List.of(
                    new AvpRule(0, 1, "DRMP"),
                    new AvpRule(0, 1, "Destination-Host"),
                    new AvpRule(0, 1, "OC-Supported-Features"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Features"),
                    new AvpRule(0, 1, "User-Authorization-Type"),
                    new AvpRule(0, 1, "UAR-Flags"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "Proxy-Info"),
                    new AvpRule(0, Long.MAX_VALUE, "Route-Record")
                )
            ),
            new CommandDef(16777216, 300, "User-Authorization-Answer", false, true, false,
                List.of(new AvpRule(1, 1, "Session-Id")),
                List.of(
                    new AvpRule(1, 1, "Vendor-Specific-Application-Id"),
                    new AvpRule(1, 1, "Auth-Session-State"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm")
                ),
                List.of(
                    new AvpRule(0, 1, "DRMP"),
                    new AvpRule(0, 1, "Result-Code"),
                    new AvpRule(0, 1, "Experimental-Result"),
                    new AvpRule(0, 1, "OC-Supported-Features"),
                    new AvpRule(0, 1, "OC-OLR"),
                    new AvpRule(0, Long.MAX_VALUE, "Load"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Features"),
                    new AvpRule(0, 1, "Server-Name"),
                    new AvpRule(0, 1, "Server-Capabilities"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP"),
                    new AvpRule(0, 1, "Failed-AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "Proxy-Info"),
                    new AvpRule(0, Long.MAX_VALUE, "Route-Record")
                )
            ),
            new CommandDef(16777216, 301, "Server-Assignment-Request", true, true, false,
                List.of(new AvpRule(1, 1, "Session-Id")),
                List.of(
                    new AvpRule(1, 1, "Vendor-Specific-Application-Id"),
                    new AvpRule(1, 1, "Auth-Session-State"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm"),
                    new AvpRule(1, 1, "Destination-Realm"),
                    new AvpRule(1, 1, "Server-Name"),
                    new AvpRule(1, 1, "Server-Assignment-Type"),
                    new AvpRule(1, 1, "User-Data-Already-Available")
                ),
                List.of(
                    new AvpRule(0, 1, "DRMP"),
                    new AvpRule(0, 1, "Destination-Host"),
                    new AvpRule(0, 1, "User-Name"),
                    new AvpRule(0, 1, "OC-Supported-Features"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Features"),
                    new AvpRule(0, Long.MAX_VALUE, "Public-Identity"),
                    new AvpRule(0, 1, "Wildcarded-Public-Identity"),
                    new AvpRule(0, 1, "SCSCF-Restoration-Info"),
                    new AvpRule(0, 1, "Multiple-Registration-Indication"),
                    new AvpRule(0, 1, "Session-Priority"),
                    new AvpRule(0, 1, "SAR-Flags"),
                    new AvpRule(0, 1, "Failed-PCSCF"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "Proxy-Info"),
                    new AvpRule(0, Long.MAX_VALUE, "Route-Record")
                )
            ),
            new CommandDef(16777216, 301, "Server-Assignment-Answer", false, true, false,
                List.of(new AvpRule(1, 1, "Session-Id")),
                List.of(
                    new AvpRule(1, 1, "Vendor-Specific-Application-Id"),
                    new AvpRule(1, 1, "Auth-Session-State"),
                    new AvpRule(1, 1, "Origin-Host"),
                    new AvpRule(1, 1, "Origin-Realm")
                ),
                List.of(
                    new AvpRule(0, 1, "DRMP"),
                    new AvpRule(0, 1, "Result-Code"),
                    new AvpRule(0, 1, "Experimental-Result"),
                    new AvpRule(0, 1, "User-Name"),
                    new AvpRule(0, 1, "OC-Supported-Features"),
                    new AvpRule(0, 1, "OC-OLR"),
                    new AvpRule(0, Long.MAX_VALUE, "Load"),
                    new AvpRule(0, Long.MAX_VALUE, "Supported-Features"),
                    new AvpRule(0, 1, "User-Data"),
                    new AvpRule(0, 1, "Charging-Information"),
                    new AvpRule(0, 1, "Associated-Identities"),
                    new AvpRule(0, 1, "Loose-Route-Indication"),
                    new AvpRule(0, Long.MAX_VALUE, "SCSCF-Restoration-Info"),
                    new AvpRule(0, 1, "Associated-Registered-Identities"),
                    new AvpRule(0, 1, "Server-Name"),
                    new AvpRule(0, 1, "Wildcarded-Public-Identity"),
                    new AvpRule(0, 1, "Priviledged-Sender-Indication"),
                    new AvpRule(0, 1, "Allowed-WAF-WWSF-Identities"),
                    new AvpRule(0, Long.MAX_VALUE, "AVP"),
                    new AvpRule(0, 1, "Failed-AVP"),
                    new AvpRule(0, Long.MAX_VALUE, "Proxy-Info"),
                    new AvpRule(0, Long.MAX_VALUE, "Route-Record")
                )
            )
        );
    }
}
