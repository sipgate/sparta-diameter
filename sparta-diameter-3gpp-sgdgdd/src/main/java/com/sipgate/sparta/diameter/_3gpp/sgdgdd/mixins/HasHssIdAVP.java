package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an HSS-ID AVP (3GPP TS 29.338 §6.3.3.14, code 3325).
 * <p>
 * UTF8String — identifies the destination user's HSS. V flag only.
 * </p>
 */
public interface HasHssIdAVP extends AVPContainer {

    default void setHssId(final String value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_HSS_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getHssId() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_HSS_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
