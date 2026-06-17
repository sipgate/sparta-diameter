package com.sipgate.sparta.diameter._3gpp.slh;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by 3GPP TS 29.173 (SLh) that are reused on other interfaces. */
public final class SlhAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(SlhConstants.AVP_LMSI, "LMSI", byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_SERVING_NODE, "Serving-Node", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_MME_NAME, "MME-Name", String.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_MSC_NUMBER, "MSC-Number", byte[].class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_LCS_CAPABILITIES_SETS, "LCS-Capabilities-Sets", Long.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_GMLC_ADDRESS, "GMLC-Address", InetAddress.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_ADDITIONAL_SERVING_NODE, "Additional-Serving-Node", GroupedAVP.class, true, true, _3gppConstants.VENDOR_ID_3GPP),
            new AVPDefinition(SlhConstants.AVP_PPR_ADDRESS, "PPR-Address", InetAddress.class, true, true, _3gppConstants.VENDOR_ID_3GPP)
        );
    }
}
