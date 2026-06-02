package com.sipgate.sparta.diameter._3gpp.gx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.212 (Gx) that are reused on other interfaces. */
public final class GxAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(GxConstants.AVP_RAT_TYPE, "RAT-Type", Integer.class, false, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
