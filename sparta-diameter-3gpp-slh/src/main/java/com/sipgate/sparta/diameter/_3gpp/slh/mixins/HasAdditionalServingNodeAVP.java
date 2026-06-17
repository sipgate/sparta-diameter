package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Additional-Serving-Node AVP (3GPP TS 29.173 §6.4.8, code 2406). */
public interface HasAdditionalServingNodeAVP extends AVPContainer {

    default void setAdditionalServingNode(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_ADDITIONAL_SERVING_NODE, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getAdditionalServingNode() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_ADDITIONAL_SERVING_NODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
