package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.spec.CommandCodeFormatParser;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class BaseCommandSpecTest extends CommandSpecTestBase {

    private static final Set<CommandDef> COMMAND_DEFS = CommandCodeFormatParser.parse("""
         <ASR>  ::= < Diameter Header: 274, REQ, PXY >
                    < Session-Id >
                    { Origin-Host }
                    { Origin-Realm }
                    { Destination-Realm }
                    { Destination-Host }
                    { Auth-Application-Id }
                    [ User-Name ]
                    [ Origin-State-Id ]
                  * [ Proxy-Info ]
                  * [ Route-Record ]
                  * [ AVP ]
         <ASA>  ::= < Diameter Header: 274, PXY >
                    < Session-Id >
                    { Result-Code }
                    { Origin-Host }
                    { Origin-Realm }
                    [ User-Name ]
                    [ Origin-State-Id ]
                    [ Error-Message ]
                    [ Error-Reporting-Host ]
                    [ Failed-AVP ]
                  * [ Redirect-Host ]
                    [ Redirect-Host-Usage ]
                    [ Redirect-Max-Cache-Time ]
                  * [ Proxy-Info ]
                  * [ AVP ]
         <ACR> ::= < Diameter Header: 271, REQ, PXY >
                   < Session-Id >
                   { Origin-Host }
                   { Origin-Realm }
                   { Destination-Realm }
                   { Accounting-Record-Type }
                   { Accounting-Record-Number }
                   [ Acct-Application-Id ]
                   [ Vendor-Specific-Application-Id ]
                   [ User-Name ]
                   [ Destination-Host ]
                   [ Accounting-Sub-Session-Id ]
                   [ Acct-Session-Id ]
                   [ Acct-Multi-Session-Id ]
                   [ Acct-Interim-Interval ]
                   [ Accounting-Realtime-Required ]
                   [ Origin-State-Id ]
                   [ Event-Timestamp ]
                 * [ Proxy-Info ]
                 * [ Route-Record ]
                 * [ AVP ]
         <ACA> ::= < Diameter Header: 271, PXY >
                   < Session-Id >
                   { Result-Code }
                   { Origin-Host }
                   { Origin-Realm }
                   { Accounting-Record-Type }
                   { Accounting-Record-Number }
                   [ Acct-Application-Id ]
                   [ Vendor-Specific-Application-Id ]
                   [ User-Name ]
                   [ Accounting-Sub-Session-Id ]
                   [ Acct-Session-Id ]
                   [ Acct-Multi-Session-Id ]
                   [ Error-Message ]
                   [ Error-Reporting-Host ]
                   [ Failed-AVP ]
                   [ Acct-Interim-Interval ]
                   [ Accounting-Realtime-Required ]
                   [ Origin-State-Id ]
                   [ Event-Timestamp ]
                 * [ Proxy-Info ]
                 * [ AVP ]
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
         <DWR>  ::= < Diameter Header: 280, REQ >
                    { Origin-Host }
                    { Origin-Realm }
                    [ Origin-State-Id ]
                  * [ AVP ]
         <DWA>  ::= < Diameter Header: 280 >
                    { Result-Code }
                    { Origin-Host }
                    { Origin-Realm }
                    [ Error-Message ]
                    [ Failed-AVP ]
                    [ Origin-State-Id ]
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
         <RAR>  ::= < Diameter Header: 258, REQ, PXY >
                    < Session-Id >
                    { Origin-Host }
                    { Origin-Realm }
                    { Destination-Realm }
                    { Destination-Host }
                    { Auth-Application-Id }
                    { Re-Auth-Request-Type }
                    [ User-Name ]
                    [ Origin-State-Id ]
                  * [ Proxy-Info ]
                  * [ Route-Record ]
                  * [ AVP ]
         <RAA>  ::= < Diameter Header: 258, PXY >
                    < Session-Id >
                    { Result-Code }
                    { Origin-Host }
                    { Origin-Realm }
                    [ User-Name ]
                    [ Origin-State-Id ]
                    [ Error-Message ]
                    [ Error-Reporting-Host ]
                    [ Failed-AVP ]
                  * [ Redirect-Host ]
                    [ Redirect-Host-Usage ]
                    [ Redirect-Max-Cache-Time ]
                  * [ Proxy-Info ]
                  * [ AVP ]
        <STR>  ::= < Diameter Header: 275, REQ, PXY >
                   < Session-Id >
                   { Origin-Host }
                   { Origin-Realm }
                   { Destination-Realm }
                   { Auth-Application-Id }
                   { Termination-Cause }
                   [ User-Name ]
                   [ Destination-Host ]
                 * [ Class ]
                   [ Origin-State-Id ]
                 * [ Proxy-Info ]
                 * [ Route-Record ]
                 * [ AVP ]
         <STA> ::= < Diameter Header: 275, PXY >
                    < Session-Id >
                    { Result-Code }
                    { Origin-Host }
                    { Origin-Realm }
                    [ User-Name ]
                  * [ Class ]
                    [ Error-Message ]
                    [ Error-Reporting-Host ]
                    [ Failed-AVP ]
                    [ Origin-State-Id ]
                  * [ Redirect-Host ]
                    [ Redirect-Host-Usage ]
                    [ Redirect-Max-Cache-Time ]
                  * [ Proxy-Info ]
                  * [ AVP ]
        """);

    static Stream<Arguments> provideCommandDefs() {
        return named(COMMAND_DEFS);
    }

    @Override
    protected String messagesPackage() {
        return "com.sipgate.sparta.diameter.base.messages";
    }

    @Override
    protected String classNameFor(final String commandName) {
        return switch (commandName) {
            case "ASR" -> "AbortSessionRequest";
            case "ASA" -> "AbortSessionAnswer";
            case "ACR" -> "AccountingRequest";
            case "ACA" -> "AccountingAnswer";
            case "CER" -> "CapabilitiesExchangeRequest";
            case "CEA" -> "CapabilitiesExchangeAnswer";
            case "DWR" -> "DeviceWatchdogRequest";
            case "DWA" -> "DeviceWatchdogAnswer";
            case "DPR" -> "DisconnectPeerRequest";
            case "DPA" -> "DisconnectPeerAnswer";
            case "RAR" -> "ReAuthRequest";
            case "RAA" -> "ReAuthAnswer";
            case "STR" -> "SessionTerminationRequest";
            case "STA" -> "SessionTerminationAnswer";
            default -> throw new IllegalArgumentException("No mapping for CCF abbreviation: " + commandName);
        };
    }

    @Override
    protected long expectedApplicationId(final CommandDef def) {
        // RFC 6733 §9.7 omits the application-id from the ACR/ACA CCF header; §9.3 split-accounting model
        // requires the Diameter base accounting Application Id (3, per §2.4).
        if (def.applicationId() == 0 && (def.commandName().equals("ACR") || def.commandName().equals("ACA"))) {
            return 3;
        }
        return def.applicationId();
    }
}
