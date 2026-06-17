package com.sipgate.sparta.diameter._3gpp.rx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.214 (Rx) that are reused on other interfaces. */
public final class RxAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(RxConstants.AVP_MAX_REQUESTED_BANDWIDTH_DL, "Max-Requested-Bandwidth-DL", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(RxConstants.AVP_MAX_REQUESTED_BANDWIDTH_UL, "Max-Requested-Bandwidth-UL", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
