package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an Originating-SIP-URI AVP (3GPP TS 29.338 §6.3.3.15, code 3326).
 * <p>
 * UTF8String — public identity of the IMS UE without MSISDN that is the sender. V flag only.
 * </p>
 */
public interface HasOriginatingSipUriAVP extends AVPContainer {

    default void setOriginatingSipUri(final String value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_ORIGINATING_SIP_URI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getOriginatingSipUri() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_ORIGINATING_SIP_URI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
