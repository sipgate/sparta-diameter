package com.sipgate.sparta.diameter.ietf.cc;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class CcAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 4006, §9.  AVPs
    /// The RFC prints the Restriction-Filter-Rule type as "IPFiltrRule"; the parser knows it as "IPFltrRule".
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        -----------------------------------------|----+-----+----+----|----|
        CC-Correlation-Id 411  8.1    OctetString|    | P,M |    |  V | Y  |
        CC-Input-Octets   412  8.24   Unsigned64 | M  |  P  |    |  V | Y  |
        CC-Money          413  8.22   Grouped    | M  |  P  |    |  V | Y  |
        CC-Output-Octets  414  8.25   Unsigned64 | M  |  P  |    |  V | Y  |
        CC-Request-Number 415  8.2    Unsigned32 | M  |  P  |    |  V | Y  |
        CC-Request-Type   416  8.3    Enumerated | M  |  P  |    |  V | Y  |
        CC-Service-       417  8.26   Unsigned64 | M  |  P  |    |  V | Y  |
          Specific-Units                         |    |     |    |    |    |
        CC-Session-       418  8.4    Enumerated | M  |  P  |    |  V | Y  |
          Failover                               |    |     |    |    |    |
        CC-Sub-Session-Id 419  8.5    Unsigned64 | M  |  P  |    |  V | Y  |
        CC-Time           420  8.21   Unsigned32 | M  |  P  |    |  V | Y  |
        CC-Total-Octets   421  8.23   Unsigned64 | M  |  P  |    |  V | Y  |
        CC-Unit-Type      454  8.32   Enumerated | M  |  P  |    |  V | Y  |
        Check-Balance-    422  8.6    Enumerated | M  |  P  |    |  V | Y  |
          Result                                 |    |     |    |    |    |
        Cost-Information  423  8.7    Grouped    | M  |  P  |    |  V | Y  |
        Cost-Unit         424  8.12   UTF8String | M  |  P  |    |  V | Y  |
        Credit-Control    426  8.13   Enumerated | M  |  P  |    |  V | Y  |
        Credit-Control-   427  8.14   Enumerated | M  |  P  |    |  V | Y  |
          Failure-Handling                       |    |     |    |    |    |
        Currency-Code     425  8.11   Unsigned32 | M  |  P  |    |  V | Y  |
        Direct-Debiting-  428  8.15   Enumerated | M  |  P  |    |  V | Y  |
          Failure-Handling                       |    |     |    |    |    |
        Exponent          429  8.9    Integer32  | M  |  P  |    |  V | Y  |
        Final-Unit-Action 449  8.35   Enumerated | M  |  P  |    |  V | Y  |
        Final-Unit-       430  8.34   Grouped    | M  |  P  |    |  V | Y  |
          Indication                             |    |     |    |    |    |
        Granted-Service-  431  8.17   Grouped    | M  |  P  |    |  V | Y  |
          Unit                                   |    |     |    |    |    |
        G-S-U-Pool-       453  8.31   Unsigned32 | M  |  P  |    |  V | Y  |
          Identifier                             |    |     |    |    |    |
        G-S-U-Pool-       457  8.30   Grouped    | M  |  P  |    |  V | Y  |
          Reference                              |    |     |    |    |    |
        Multiple-Services 456  8.16   Grouped    | M  |  P  |    |  V | Y  |
         -Credit-Control                         |    |     |    |    |    |
        Multiple-Services 455  8.40   Enumerated | M  |  P  |    |  V | Y  |
         -Indicator                              |    |     |    |    |    |
        Rating-Group      432  8.29   Unsigned32 | M  |  P  |    |  V | Y  |
        Redirect-Address  433  8.38   Enumerated | M  |  P  |    |  V | Y  |
          -Type                                  |    |     |    |    |    |
        Redirect-Server   434  8.37   Grouped    | M  |  P  |    |  V | Y  |
        Redirect-Server   435  8.39   UTF8String | M  |  P  |    |  V | Y  |
          -Address                               |    |     |    |    |    |
        Requested-Action  436  8.41   Enumerated | M  |  P  |    |  V | Y  |
        Requested-Service 437  8.18   Grouped    | M  |  P  |    |  V | Y  |
          -Unit                                  |    |     |    |    |    |
        Restriction       438  8.36   IPFltrRule| M  |  P  |    |  V | Y  |
          -Filter-Rule                           |    |     |    |    |    |
        Service-Context   461  8.42   UTF8String | M  |  P  |    |  V | Y  |
          -Id                                    |    |     |    |    |    |
        Service-          439  8.28   Unsigned32 | M  |  P  |    |  V | Y  |
          Identifier                             |    |     |    |    |    |
        Service-Parameter 440  8.43   Grouped    |    | P,M |    |  V | Y  |
          -Info                                  |    |     |    |    |    |
        Service-          441  8.44   Unsigned32 |    | P,M |    |  V | Y  |
          Parameter-Type                         |    |     |    |    |    |
        Service-          442  8.45   OctetString|    | P,M |    |  V | Y  |
          Parameter-Value                        |    |     |    |    |    |
        Subscription-Id   443  8.46   Grouped    | M  |  P  |    |  V | Y  |
        Subscription-Id   444  8.48   UTF8String | M  |  P  |    |  V | Y  |
          -Data                                  |    |     |    |    |    |
        Subscription-Id   450  8.47   Enumerated | M  |  P  |    |  V | Y  |
          -Type                                  |    |     |    |    |    |
        Tariff-Change     452  8.27   Enumerated | M  |  P  |    |  V | Y  |
          -Usage                                 |    |     |    |    |    |
        Tariff-Time       451  8.20   Time       | M  |  P  |    |  V | Y  |
          -Change                                |    |     |    |    |    |
        Unit-Value        445  8.8    Grouped    | M  |  P  |    |  V | Y  |
        Used-Service-Unit 446  8.19   Grouped    | M  |  P  |    |  V | Y  |
        User-Equipment    458  8.49   Grouped    |    | P,M |    |  V | Y  |
          -Info                                  |    |     |    |    |    |
        User-Equipment    459  8.50   Enumerated |    | P,M |    |  V | Y  |
          -Info-Type                             |    |     |    |    |    |
        User-Equipment    460  8.51   OctetString|    | P,M |    |  V | Y  |
          -Info-Value                            |    |     |    |    |    |
        Value-Digits      447  8.10   Integer64  | M  |  P  |    |  V | Y  |
        Validity-Time     448  8.33   Unsigned32 | M  |  P  |    |  V | Y  |
        """);

    private static final Collection<AVPDefinition> DEFINITIONS = new CcAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.cc.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
