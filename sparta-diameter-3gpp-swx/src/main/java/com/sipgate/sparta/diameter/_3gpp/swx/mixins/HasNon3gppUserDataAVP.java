package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Non-3GPP-User-Data grouped AVP (3GPP TS 29.273 §8.2.3.1, code 1500). */
public interface HasNon3gppUserDataAVP extends AVPContainer {

    /**
     * Sets the Non-3GPP-User-Data AVP.
     *
     * @param avps the list of child AVPs for the grouped Non-3GPP-User-Data AVP.
     */
    default void setNon3gppUserData(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_NON_3GPP_USER_DATA, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    /**
     * Gets the Non-3GPP-User-Data grouped AVP from this message.
     *
     * @return the Non-3GPP-User-Data AVPContainer, or null if not present.
     */
    default AVPContainer getNon3gppUserData() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_NON_3GPP_USER_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
