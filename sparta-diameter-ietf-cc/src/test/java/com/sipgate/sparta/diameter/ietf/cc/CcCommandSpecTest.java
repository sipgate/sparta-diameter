package com.sipgate.sparta.diameter.ietf.cc;

import com.sipgate.sparta.diameter.base.CommandSpecTestBase;
import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class CcCommandSpecTest extends CommandSpecTestBase {

    /// from RFC 4006, §3.1 and §3.2. The RFC's headers carry no application id; Auth-Application-Id is 4 (§3.1).
    /// The CCA lists "*[ Failed-AVP ]" in the RFC; RFC 6733 §7.5 defines it as optional-single, which is what
    /// DiameterAnswer provides.
    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
        < Credit-Control-Request > ::= < Diameter Header: 272, REQ, PXY, 4 >
        < Session-Id >
        { Origin-Host }
        { Origin-Realm }
        { Destination-Realm }
        { Auth-Application-Id }
        { Service-Context-Id }
        { CC-Request-Type }
        { CC-Request-Number }
        [ Destination-Host ]
        [ User-Name ]
        [ CC-Sub-Session-Id ]
        [ Acct-Multi-Session-Id ]
        [ Origin-State-Id ]
        [ Event-Timestamp ]
        *[ Subscription-Id ]
        [ Service-Identifier ]
        [ Termination-Cause ]
        [ Requested-Service-Unit ]
        [ Requested-Action ]
        *[ Used-Service-Unit ]
        [ Multiple-Services-Indicator ]
        *[ Multiple-Services-Credit-Control ]
        *[ Service-Parameter-Info ]
        [ CC-Correlation-Id ]
        [ User-Equipment-Info ]
        *[ Proxy-Info ]
        *[ Route-Record ]
        *[ AVP ]
        < Credit-Control-Answer > ::= < Diameter Header: 272, PXY, 4 >
        < Session-Id >
        { Result-Code }
        { Origin-Host }
        { Origin-Realm }
        { Auth-Application-Id }
        { CC-Request-Type }
        { CC-Request-Number }
        [ User-Name ]
        [ CC-Session-Failover ]
        [ CC-Sub-Session-Id ]
        [ Acct-Multi-Session-Id ]
        [ Origin-State-Id ]
        [ Event-Timestamp ]
        [ Granted-Service-Unit ]
        *[ Multiple-Services-Credit-Control ]
        [ Cost-Information ]
        [ Final-Unit-Indication ]
        [ Check-Balance-Result ]
        [ Credit-Control-Failure-Handling ]
        [ Direct-Debiting-Failure-Handling ]
        [ Validity-Time ]
        *[ Redirect-Host ]
        [ Redirect-Host-Usage ]
        [ Redirect-Max-Cache-Time ]
        *[ Proxy-Info ]
        *[ Route-Record ]
        [ Failed-AVP ]
        *[ AVP ]
        """);

    @SuppressWarnings("unused") // Used by methodSource in base class
    static Stream<Arguments> provideCommandDefs() {
        return named(COMMAND_DEFS);
    }

    @Override
    protected String messagesPackage() {
        return "com.sipgate.sparta.diameter.ietf.cc.messages";
    }
}
