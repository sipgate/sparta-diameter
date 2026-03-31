package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying a Serving-Node AVP (3GPP TS 29.173, code 2401).
 */
public interface HasServingNodeAVP<T extends HasServingNodeAVP<T>> extends AVPContainer<T> {

    default T setServingNode(final GroupedAVP value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SERVING_NODE, _3gppConstants.VENDOR_ID_3GPP), value.getAVPs()));
        return self();
    }

    default GroupedAVP getServingNode() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SERVING_NODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
