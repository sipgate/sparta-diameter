package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SM-RP-UI AVP (3GPP TS 29.338 §6.3.3.3, code 3301).
 * <p>
 * OctetString — short message transfer protocol data unit. M,V flags.
 * </p>
 */
public interface HasSmRpUiAVP<T extends HasSmRpUiAVP<T>> extends AVPContainer<T> {

    default T setSmRpUi(final byte[] value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SM_RP_UI, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default byte[] getSmRpUi() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SM_RP_UI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
