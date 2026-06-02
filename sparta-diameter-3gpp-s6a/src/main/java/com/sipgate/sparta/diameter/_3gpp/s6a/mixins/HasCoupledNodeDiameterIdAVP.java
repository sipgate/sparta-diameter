package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Coupled-Node-Diameter-ID AVP (3GPP TS 29.272 §7.3.162, code 1666). */
public interface HasCoupledNodeDiameterIdAVP extends AVPContainer {

    default void setCoupledNodeDiameterId(final String value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_COUPLED_NODE_DIAMETER_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getCoupledNodeDiameterId() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_COUPLED_NODE_DIAMETER_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsDiameterIdentity() : null;
    }
}
