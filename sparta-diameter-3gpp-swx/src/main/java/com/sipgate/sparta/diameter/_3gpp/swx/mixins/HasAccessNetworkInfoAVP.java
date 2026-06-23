package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Access-Network-Info grouped AVP (3GPP TS 29.273 §8.2.3.32, code 1526). */
public interface HasAccessNetworkInfoAVP extends AVPContainer {

    /**
     * Sets the Access-Network-Info AVP.
     *
     * @param avps the list of child AVPs for the grouped Access-Network-Info AVP.
     */
    default void setAccessNetworkInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_ACCESS_NETWORK_INFO, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    /**
     * Gets the Access-Network-Info grouped AVP from this message.
     *
     * @return the Access-Network-Info AVPContainer, or null if not present.
     */
    default AVPContainer getAccessNetworkInfo() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_ACCESS_NETWORK_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
