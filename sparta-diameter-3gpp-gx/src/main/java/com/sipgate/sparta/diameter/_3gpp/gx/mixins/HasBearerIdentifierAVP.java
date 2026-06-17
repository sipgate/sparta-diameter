package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Bearer-Identifier AVP (3GPP TS 29.212, code 1020). */
public interface HasBearerIdentifierAVP extends AVPContainer {

    default void setBearerIdentifier(final byte[] value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_BEARER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getBearerIdentifier() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_BEARER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
