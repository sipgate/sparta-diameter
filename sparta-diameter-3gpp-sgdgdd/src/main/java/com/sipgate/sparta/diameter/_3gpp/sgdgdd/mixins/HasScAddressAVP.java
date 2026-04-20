package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SC-Address AVP (3GPP TS 29.338 §6.3.3.2, code 3300).
 * <p>
 * OctetString — E.164 number encoded as TBCD-string. M,V flags.
 * </p>
 */
public interface HasScAddressAVP extends AVPContainer {

    default void setScAddress(final byte[] value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SC_ADDRESS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getScAddress() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SC_ADDRESS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
