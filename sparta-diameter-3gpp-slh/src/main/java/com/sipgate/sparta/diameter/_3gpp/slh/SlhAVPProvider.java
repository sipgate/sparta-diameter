package com.sipgate.sparta.diameter._3gpp.slh;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.173 (SLh) that are reused on other interfaces. */
public final class SlhAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(SlhConstants.AVP_GMLC_ADDRESS, "GMLC-Address", InetAddress.class, true, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
