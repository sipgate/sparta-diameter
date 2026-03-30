package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SM-RP-UI AVP (3GPP TS 29.338 §6.3.3.3, code 3301).
 * <p>
 * OctetString — short message transfer protocol data unit. M,V flags.
 * </p>
 */
public interface HasSmRpUiAVP<T extends HasSmRpUiAVP<T>> extends AVPContainer<T> {

    default T setSmRpUi(final byte[] value) {
        setAVP(AVP.create(SgdGddConstants.AVP_SM_RP_UI, value));
        return self();
    }

    default byte[] getSmRpUi() {
        final var avp = findAVP(SgdGddConstants.AVP_SM_RP_UI);
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
