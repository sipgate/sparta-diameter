package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying an Allowed-WAF-WWSF-Identities grouped AVP (TS 29.229 §6.3.56, code 656). */
public interface HasAllowedWafWwsfIdentitiesAVP extends AVPContainer {

    default void setAllowedWafWwsfIdentities(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_ALLOWED_WAF_WWSF_IDENTITIES, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getAllowedWafWwsfIdentities() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_ALLOWED_WAF_WWSF_IDENTITIES, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
