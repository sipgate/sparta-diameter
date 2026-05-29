package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.List;

/** Mixin for messages carrying zero or more SIP-Auth-Data-Item grouped AVPs (TS 29.229 §6.3.13, code 612). */
public interface HasSipAuthDataItemAVPs extends AVPContainer {

    default void addSipAuthDataItem(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getSipAuthDataItems() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }
}
