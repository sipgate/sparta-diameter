package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Visited-Network-Identifier AVP (3GPP TS 29.229 §6.3.1, code 600). */
public interface HasVisitedNetworkIdentifierAVP extends AVPContainer {

    default void setVisitedNetworkIdentifier(final byte[] value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_VISITED_NETWORK_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getVisitedNetworkIdentifier() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_VISITED_NETWORK_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
