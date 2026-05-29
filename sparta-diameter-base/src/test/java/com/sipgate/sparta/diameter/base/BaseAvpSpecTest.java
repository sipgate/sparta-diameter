package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class BaseAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        -----------------------------------------|----+-----|
        Acct-             85  9.8.2   Unsigned32 | M  |  V  |
          Interim-Interval                       |    |     |
        Accounting-      483  9.8.7   Enumerated | M  |  V  |
          Realtime-Required                      |    |     |
        Acct-            50   9.8.5   UTF8String | M  |  V  |
          Multi-Session-Id                       |    |     |
        Accounting-      485  9.8.3   Unsigned32 | M  |  V  |
          Record-Number                          |    |     |
        Accounting-      480  9.8.1   Enumerated | M  |  V  |
          Record-Type                            |    |     |
        Acct-             44  9.8.4   OctetString| M  |  V  |
         Session-Id                              |    |     |
        Accounting-      287  9.8.6   Unsigned64 | M  |  V  |
          Sub-Session-Id                         |    |     |
        Acct-            259  6.9     Unsigned32 | M  |  V  |
          Application-Id                         |    |     |
        Auth-            258  6.8     Unsigned32 | M  |  V  |
          Application-Id                         |    |     |
        Auth-Request-    274  8.7     Enumerated | M  |  V  |
           Type                                  |    |     |
        Authorization-   291  8.9     Unsigned32 | M  |  V  |
          Lifetime                               |    |     |
        Auth-Grace-      276  8.10    Unsigned32 | M  |  V  |
          Period                                 |    |     |
        Auth-Session-    277  8.11    Enumerated | M  |  V  |
          State                                  |    |     |
        Re-Auth-Request- 285  8.12    Enumerated | M  |  V  |
          Type                                   |    |     |
        Class             25  8.20    OctetString| M  |  V  |
        Destination-Host 293  6.5     DiamIdent  | M  |  V  |
        Destination-     283  6.6     DiamIdent  | M  |  V  |
          Realm                                  |    |     |
        Disconnect-Cause 273  5.4.3   Enumerated | M  |  V  |
        Error-Message    281  7.3     UTF8String |    | V,M |
        Error-Reporting- 294  7.4     DiamIdent  |    | V,M |
          Host                                   |    |     |
        Event-Timestamp   55  8.21    Time       | M  |  V  |
        Experimental-    297  7.6     Grouped    | M  |  V  |
           Result                                |    |     |
        Experimental-    298  7.7     Unsigned32 | M  |  V  |
           Result-Code                           |    |     |
        Failed-AVP       279  7.5     Grouped    | M  |  V  |
        Firmware-        267  5.3.4   Unsigned32 |    | V,M |
          Revision                               |    |     |
        Host-IP-Address  257  5.3.5   Address    | M  |  V  |
        Inband-Security                          | M  |  V  |
           -Id           299  6.10    Unsigned32 |    |     |
        Multi-Round-     272  8.19    Unsigned32 | M  |  V  |
          Time-Out                               |    |     |
        Origin-Host      264  6.3     DiamIdent  | M  |  V  |
        Origin-Realm     296  6.4     DiamIdent  | M  |  V  |
        Origin-State-Id  278  8.16    Unsigned32 | M  |  V  |
        Product-Name     269  5.3.7   UTF8String |    | V,M |
        Proxy-Host       280  6.7.3   DiamIdent  | M  |  V  |
        Proxy-Info       284  6.7.2   Grouped    | M  |  V  |
        Proxy-State       33  6.7.4   OctetString| M  |  V  |
        Redirect-Host    292  6.12    DiamURI    | M  |  V  |
        Redirect-Host-   261  6.13    Enumerated | M  |  V  |
           Usage                                 |    |     |
        Redirect-Max-    262  6.14    Unsigned32 | M  |  V  |
           Cache-Time                            |    |     |
        Result-Code      268  7.1     Unsigned32 | M  |  V  |
        Route-Record     282  6.7.1   DiamIdent  | M  |  V  |
        Session-Id       263  8.8     UTF8String | M  |  V  |
        Session-Timeout   27  8.13    Unsigned32 | M  |  V  |
        Session-Binding  270  8.17    Unsigned32 | M  |  V  |
        Session-Server-  271  8.18    Enumerated | M  |  V  |
          Failover                               |    |     |
        Supported-       265  5.3.6   Unsigned32 | M  |  V  |
          Vendor-Id                              |    |     |
        Termination-     295  8.15    Enumerated | M  |  V  |
           Cause                                 |    |     |
        User-Name          1  8.14    UTF8String | M  |  V  |
        Vendor-Id        266  5.3.3   Unsigned32 | M  |  V  |
        Vendor-Specific- 260  6.11    Grouped    | M  |  V  |
           Application-Id                        |    |     |
        """);

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS);
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.base.core.avp.mixins";
    }

    @Override
    protected int exampleEnumValueFor(final AvpDef def) {
        return switch (def.attributeName()) {
            case "Auth-Session-State" -> DiameterConstants.AUTH_SESSION_STATE_MAINTAINED;
            default -> super.exampleEnumValueFor(def);
        };
    }
}
