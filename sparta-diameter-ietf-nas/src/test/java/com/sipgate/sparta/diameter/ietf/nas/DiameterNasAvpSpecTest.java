package com.sipgate.sparta.diameter.ietf.nas;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.CoreAVPProvider;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class DiameterNasAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 4005, §6.  NAS Authorization AVPs
    /// We use the obsolete RFC here because RFC 7155 doesn't have an overview table including AVP codes
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
       -----------------------------------------|----+-----+----+-----|----|
       Service-Type       6   6.1    Enumerated | M  |  P  |    |  V  | Y  |
       Callback-Number   19   6.2    UTF8String | M  |  P  |    |  V  | Y  |
       Callback-Id       20   6.3    UTF8String | M  |  P  |    |  V  | Y  |
       Idle-Timeout      28   6.4    Unsigned32 | M  |  P  |    |  V  | Y  |
       Port-Limit        62   6.5    Unsigned32 | M  |  P  |    |  V  | Y  |
       NAS-Filter-Rule  400   6.6    IPFltrRule | M  |  P  |    |  V  | Y  |
       Filter-Id         11   6.7    UTF8String | M  |  P  |    |  V  | Y  |
       Configuration-    78   6.8    OctetString| M  |     |    | P,V |    |
         Token                                  |    |     |    |     |    |
       QoS-Filter-Rule  407   6.9    QoSFltrRule|    |     |    |     |    |
       Framed-Protocol    7  6.10.1  Enumerated | M  |  P  |    |  V  | Y  |
       Framed-Routing    10  6.10.2  Enumerated | M  |  P  |    |  V  | Y  |
       Framed-MTU        12  6.10.3  Unsigned32 | M  |  P  |    |  V  | Y  |
       Framed-           13  6.10.4  Enumerated | M  |  P  |    |  V  | Y  |
         Compression                            |    |     |    |     |    |
       Framed-IP-Address  8  6.11.1  OctetString| M  |  P  |    |  V  | Y  |
       Framed-IP-Netmask  9  6.11.2  OctetString| M  |  P  |    |  V  | Y  |
       Framed-Route      22  6.11.3  UTF8String | M  |  P  |    |  V  | Y  |
       Framed-Pool       88  6.11.4  OctetString| M  |  P  |    |  V  | Y  |
       Framed-           96  6.11.5  Unsigned64 | M  |  P  |    |  V  | Y  |
         Interface-Id                           |    |     |    |     |    |
       Framed-IPv6-      97  6.11.6  OctetString| M  |  P  |    |  V  | Y  |
         Prefix                                 |    |     |    |     |    |
       Framed-IPv6-      99  6.11.7  UTF8String | M  |  P  |    |  V  | Y  |
         Route                                  |    |     |    |     |    |
       Framed-IPv6-Pool 100  6.11.8  OctetString| M  |  P  |    |  V  | Y  |
       Framed-IPX-       23  6.12.1  UTF8String | M  |  P  |    |  V  | Y  |
         Network                                |    |     |    |     |    |
       Framed-Appletalk- 37  6.13.1  Unsigned32 | M  |  P  |    |  V  | Y  |
         Link                                   |    |     |    |     |    |
       Framed-Appletalk- 38  6.13.2  Unsigned32 | M  |  P  |    |  V  | Y  |
         Network                                |    |     |    |     |    |
       Framed-Appletalk- 39  6.13.3  OctetString| M  |  P  |    |  V  | Y  |
         Zone                                   |    |     |    |     |    |
       ARAP-Features     71  6.14.1  OctetString| M  |  P  |    |  V  | Y  |
       ARAP-Zone-Access  72  6.14.2  Enumerated | M  |  P  |    |  V  | Y  |
       Login-IP-Host     14  6.15.1  OctetString| M  |  P  |    |  V  | Y  |
       Login-IPv6-Host   98  6.15.2  OctetString| M  |  P  |    |  V  | Y  |
       Login-Service     15  6.15.3  Enumerated | M  |  P  |    |  V  | Y  |
       Login-TCP-Port    16  6.16.1  Unsigned32 | M  |  P  |    |  V  | Y  |
       Login-LAT-Service 34  6.17.1  OctetString| M  |  P  |    |  V  | Y  |
       Login-LAT-Node    35  6.17.2  OctetString| M  |  P  |    |  V  | Y  |
       Login-LAT-Group   36  6.17.3  OctetString| M  |  P  |    |  V  | Y  |
       Login-LAT-Port    63  6.17.4  OctetString| M  |  P  |    |  V  | Y  |
       -----------------------------------------|----+-----+----+-----|----|
       """);

    private static final Collection<AVPDefinition> DEFINITIONS = new DiameterNasAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.nas.mixins";
    }

    @Override
    protected int exampleEnumValueFor(final AvpDef def) {
        return switch (def.attributeName()) {
            case "Auth-Session-State" -> DiameterConstants.AUTH_SESSION_STATE_MAINTAINED;
            default -> super.exampleEnumValueFor(def);
        };
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
