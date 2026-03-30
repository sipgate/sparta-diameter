package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an HSS-ID AVP (3GPP TS 29.338 §6.3.3.14, code 3325).
 * <p>
 * UTF8String — identifies the destination user's HSS. V flag only.
 * </p>
 */
public interface HasHssIdAVP<T extends HasHssIdAVP<T>> extends AVPContainer<T> {

    default T setHssId(final String value) {
        setAVP(AVP.create(SgdGddConstants.AVP_HSS_ID, value));
        return self();
    }

    default String getHssId() {
        final var avp = findAVP(SgdGddConstants.AVP_HSS_ID);
        return avp != null ? avp.getDataAsString() : null;
    }
}
