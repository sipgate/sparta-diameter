package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Roaming-Restricted-Due-To-Unsupported-Feature AVP (3GPP, code 1457). */
public interface HasRoamingRestrictedDueToUnsupportedFeatureAVP extends AVPContainer {

    default void setRoamingRestrictedDueToUnsupportedFeature(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ROAMING_RESTRICTED_DUE_TO_UNSUPPORTED_FEATURE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getRoamingRestrictedDueToUnsupportedFeature() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ROAMING_RESTRICTED_DUE_TO_UNSUPPORTED_FEATURE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
