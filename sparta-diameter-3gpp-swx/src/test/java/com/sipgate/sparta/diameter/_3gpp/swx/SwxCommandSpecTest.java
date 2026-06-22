package com.sipgate.sparta.diameter._3gpp.swx;

import com.sipgate.sparta.diameter.base.CommandSpecTestBase;
import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class SwxCommandSpecTest extends CommandSpecTestBase {

    /// ABNFs from 3GPP TS 29.273 §8.1.2 (SWx Diameter application).
    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
        < Multimedia-Auth-Request > ::= < Diameter Header: 303, REQ, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        [ Destination-Host ]
        { User-Name }
        [ RAT-Type ]
        [ AN-Trusted ]
        [ ANID ]
        [ Visited-Network-Identifier ]
        [ Terminal-Information ]
        { SIP-Auth-Data-Item }
        { SIP-Number-Auth-Items }
        [ AAA-Failure-Indication ]
        [ OC-Supported-Features ]
        *[ Supported-Features ]

        < Multimedia-Auth-Answer > ::= < Diameter Header: 303, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { User-Name }
        [ SIP-Number-Auth-Items ]
        *[ SIP-Auth-Data-Item ]
        [ 3GPP-AAA-Server-Name ]
        [ OC-Supported-Features ]
        [ OC-OLR ]
        *[ Load ]
        *[ Supported-Features ]

        < Server-Assignment-Request > ::= < Diameter Header: 301, REQ, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        [ Destination-Host ]
        { Destination-Realm }
        [ Service-Selection ]
        [ Context-Identifier ]
        [ MIP6-Agent-Info ]
        [ Visited-Network-Identifier ]
        { User-Name }
        { Server-Assignment-Type }
        *[ Active-APN ]
        [ OC-Supported-Features ]
        *[ Supported-Features ]
        [ Terminal-Information ]
        [ Emergency-Services ]

        < Server-Assignment-Answer > ::= < Diameter Header: 301, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { User-Name }
        [ Non-3GPP-User-Data ]
        [ 3GPP-AAA-Server-Name ]
        [ OC-Supported-Features ]
        [ OC-OLR ]
        *[ Load ]
        *[ Supported-Features ]

        < Registration-Termination-Request > ::= < Diameter Header: 304, REQ, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Host }
        { Destination-Realm }
        { User-Name }
        { Deregistration-Reason }
        *[ Supported-Features ]

        < Registration-Termination-Answer > ::= < Diameter Header: 304, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        *[ Supported-Features ]

        < Push-Profile-Request > ::= < Diameter Header: 305, REQ, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Host }
        { Destination-Realm }
        { User-Name }
        [ Non-3GPP-User-Data ]
        [ PPR-Flags ]
        *[ Supported-Features ]

        < Push-Profile-Answer > ::= < Diameter Header: 305, PXY, 16777265 >
        < Session-Id >
        [ DRMP ]
        { Vendor-Specific-Application-Id }
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        [ Access-Network-Info ]
        [ Local-Time-Zone ]
        *[ Supported-Features ]
        """);

    @SuppressWarnings("unused")
    static Stream<Arguments> provideCommandDefs() {
        return named(COMMAND_DEFS);
    }

    @Override
    protected String messagesPackage() {
        return "com.sipgate.sparta.diameter._3gpp.swx.messages";
    }
}
