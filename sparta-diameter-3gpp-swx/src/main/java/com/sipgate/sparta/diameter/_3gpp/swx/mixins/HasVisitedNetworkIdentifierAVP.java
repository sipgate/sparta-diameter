package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Visited-Network-Identifier AVP (3GPP, code 600). */
public interface HasVisitedNetworkIdentifierAVP extends AVPContainer {

    default void setVisitedNetworkIdentifier(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_VISITED_NETWORK_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getVisitedNetworkIdentifier() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_VISITED_NETWORK_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
