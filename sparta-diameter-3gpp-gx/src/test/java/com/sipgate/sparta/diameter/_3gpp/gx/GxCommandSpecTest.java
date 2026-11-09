package com.sipgate.sparta.diameter._3gpp.gx;

import com.sipgate.sparta.diameter.base.CommandSpecTestBase;
import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class GxCommandSpecTest extends CommandSpecTestBase {

    /// from 3GPP TS 29.212 §5.6.2 to §5.6.5. A subset: the AVPs the module has accessors for are listed, and the
    /// commands are named after the RFC 4006 / RFC 6733 commands they extend (the TS names them CC-Request,
    /// CC-Answer, RA-Request and RA-Answer).
    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
        < Credit-Control-Request > ::= < Diameter Header: 272, REQ, PXY, 16777238 >
        < Session-Id >
        { Auth-Application-Id }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        { CC-Request-Type }
        { CC-Request-Number }
        [ Destination-Host ]
        [ Origin-State-Id ]
        *[ Subscription-Id ]
        [ Network-Request-Support ]
        [ Bearer-Identifier ]
        [ Bearer-Operation ]
        [ IP-CAN-Type ]
        [ RAT-Type ]
        [ Termination-Cause ]
        [ User-Equipment-Info ]
        [ QoS-Information ]
        [ Default-EPS-Bearer-QoS ]
        [ AN-GW-Address ]
        *[ Proxy-Info ]
        *[ Route-Record ]
        *[ AVP ]
        < Credit-Control-Answer > ::= < Diameter Header: 272, PXY, 16777238 >
        < Session-Id >
        { Auth-Application-Id }
        { Origin-Host }
        { Origin-Realm }
        { CC-Request-Type }
        { CC-Request-Number }
        [ Result-Code ]
        [ Experimental-Result ]
        [ Origin-State-Id ]
        *[ Event-Trigger ]
        [ Bearer-Control-Mode ]
        *[ Charging-Rule-Remove ]
        *[ Charging-Rule-Install ]
        [ Default-EPS-Bearer-QoS ]
        [ QoS-Information ]
        [ Revalidation-Time ]
        [ Error-Message ]
        [ Error-Reporting-Host ]
        [ Failed-AVP ]
        *[ Proxy-Info ]
        *[ AVP ]
        < Re-Auth-Request > ::= < Diameter Header: 258, REQ, PXY, 16777238 >
        < Session-Id >
        { Auth-Application-Id }
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        { Destination-Host }
        { Re-Auth-Request-Type }
        [ Session-Release-Cause ]
        [ Origin-State-Id ]
        *[ Event-Trigger ]
        *[ Charging-Rule-Remove ]
        *[ Charging-Rule-Install ]
        [ Default-EPS-Bearer-QoS ]
        [ QoS-Information ]
        [ Revalidation-Time ]
        *[ Proxy-Info ]
        *[ Route-Record ]
        *[ AVP ]
        < Re-Auth-Answer > ::= < Diameter Header: 258, PXY, 16777238 >
        < Session-Id >
        { Origin-Host }
        { Origin-Realm }
        [ Result-Code ]
        [ Experimental-Result ]
        [ Origin-State-Id ]
        *[ Charging-Rule-Report ]
        [ Error-Message ]
        [ Error-Reporting-Host ]
        [ Failed-AVP ]
        *[ Proxy-Info ]
        *[ AVP ]
        """);

    @SuppressWarnings("unused") // Used by methodSource in base class
    static Stream<Arguments> provideCommandDefs() {
        return named(COMMAND_DEFS);
    }

    @Override
    protected String messagesPackage() {
        return "com.sipgate.sparta.diameter._3gpp.gx.messages";
    }

    @Override
    protected String classNameFor(final String commandName) {
        return "Gx" + super.classNameFor(commandName);
    }
}
