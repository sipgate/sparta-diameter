package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter.base.CommandSpecTestBase;
import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class CxDxCommandSpecTest extends CommandSpecTestBase {

    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
        < Multimedia-Auth-Request > ::= < Diameter Header: 303, REQ, PXY, 16777216 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        [ Destination-Host ]
        { User-Name }
        [ OC-Supported-Features ]
        *[ Supported-Features ]
        { Public-Identity }
        { SIP-Auth-Data-Item }
        { SIP-Number-Auth-Items }
        { Server-Name }
        *[ AVP ]
        *[ Proxy-Info ]
        *[ Route-Record ]

        < Multimedia-Auth-Answer > ::= < Diameter Header: 303, PXY, 16777216 >
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
        [ Public-Identity ]
        [ SIP-Number-Auth-Items ]
        *[SIP-Auth-Data-Item ]
        *[ AVP ]
        [ Failed-AVP ]
        *[ Proxy-Info ]
        *[ Route-Record ]

        <Registration-Termination-Request> ::= < Diameter Header: 304, REQ, PXY, 16777216 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        { User-Name }
        [ Associated-Identities ]
        *[ Supported-Features ]
        *[ Public-Identity ]
        { Deregistration-Reason }
        [ RTR-Flags ]
        *[ AVP ]
        *[ Proxy-Info ]
        *[ Route-Record ]

        <Registration-Termination-Answer> ::= < Diameter Header: 304, PXY, 16777216 >
        < Session-Id >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        [ Associated-Identities ]
        *[ Supported-Features ]
        *[ Identity-with-Emergency-Registration ]
        *[ AVP ]
        [ Failed-AVP ]
        *[ Proxy-Info ]
        *[ Route-Record ]

        <Server-Assignment-Request> ::= < Diameter Header: 301, REQ, PXY, 16777216 >
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

        <Server-Assignment-Answer> ::= < Diameter Header: 301, PXY, 16777216 >
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
        """);

    @SuppressWarnings("unused") // Used by methodSource in base class
    static Stream<Arguments> provideCommandDefs() {
        return named(COMMAND_DEFS);
    }

    @Override
    protected String messagesPackage() {
        return "com.sipgate.sparta.diameter._3gpp.cxdx.messages";
    }
}
