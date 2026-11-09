package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.List;

/** The children of the Vendor-Specific-Application-Id that SGd/Gdd requests carry (TS 29.338 §6.3.2). */
final class SgdGddVendorSpecificApplicationId {

    private SgdGddVendorSpecificApplicationId() {}

    static List<AVP> children() {
        return List.of(
            AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), (long) _3gppConstants.VENDOR_ID_3GPP),
            AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), (long) SgdGddConstants.APP_ID_SGD_GDD));
    }
}
