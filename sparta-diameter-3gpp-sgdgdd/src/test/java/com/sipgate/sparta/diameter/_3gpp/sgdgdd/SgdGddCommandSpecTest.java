package com.sipgate.sparta.diameter._3gpp.sgdgdd;

import com.sipgate.sparta.diameter.base.CommandSpecTestBase;
import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class SgdGddCommandSpecTest extends CommandSpecTestBase {

    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
        < MO-Forward-Short-Message-Request > ::= < Diameter Header: 8388645, REQ, PXY, 16777313 >
        < Session-Id >
        [ DRMP ]
        [ Vendor-Specific-Application-Id ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        [ Destination-Host ]
        { Destination-Realm }
        { SC-Address }
        [ OFR-Flags ]
        *[ Supported-Features ]
        { User-Identifier }
        [ EPS-Location-Information ]
        { SM-RP-UI }
        [ SMSMI-Correlation-ID ]
        [ SM-Delivery-Outcome ]
        *[ AVP ]
         *[ Proxy-Info ]
        *[ Route-Record ]
        < MO-Forward-Short-Message-Answer > ::= < Diameter Header: 8388645, PXY, 16777313 >
        < Session-Id >
        [ DRMP ]
        [ Vendor-Specific-Application-Id ]
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        *[ Supported-Features ]
        [ SM-Delivery-Failure-Cause ]
        [ SM-RP-UI ]
        [ External-Identifier ]
        *[ AVP ]
        [ Failed-AVP ]
         *[ Proxy-Info ]
        *[ Route-Record ]
        < MT-Forward-Short-Message-Request > ::= < Diameter Header: 8388646, REQ, PXY, 16777313 >
        < Session-Id >
        [ DRMP ]
        [ Vendor-Specific-Application-Id ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Host }
        { Destination-Realm }
        { User-Name }
        *[ Supported-Features ]
        [ SMSMI-Correlation-ID ]
        { SC-Address }
        { SM-RP-UI }
        [ MME-Number-for-MT-SMS ]
        [ SGSN-Number ]
        [ TFR-Flags ]
        [ SM-Delivery-Timer ]
        [ SM-Delivery-Start-Time ]
        [ Maximum-Retransmission-Time ]
        [ SMS-GMSC-Address ]
        *[ AVP ]
        *[ Proxy-Info ]
        *[ Route-Record ]
        < MT-Forward-Short-Message-Answer > ::= < Diameter Header: 8388646, PXY, 16777313 >
        < Session-Id >
        [ DRMP ]
        [ Vendor-Specific-Application-Id ]
        [ Result-Code ]
        [ Experimental-Result ]
        { Auth-Session-State }
        { Origin-Host }
        { Origin-Realm }
        *[ Supported-Features ]
        [ Absent-User-Diagnostic-SM ]
        [ SM-Delivery-Failure-Cause ]
        [ SM-RP-UI ]
        [ Requested-Retransmission-Time ]
        [ User-Identifier ]
        [ EPS-Location-Information ]
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
        return "com.sipgate.sparta.diameter._3gpp.sgdgdd.messages";
    }
}
