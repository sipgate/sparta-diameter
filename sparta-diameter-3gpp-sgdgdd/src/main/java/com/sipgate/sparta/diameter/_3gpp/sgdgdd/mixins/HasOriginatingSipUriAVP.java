package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an Originating-SIP-URI AVP (3GPP TS 29.338 §6.3.3.15, code 3326).
 * <p>
 * UTF8String — public identity of the IMS UE without MSISDN that is the sender. V flag only.
 * </p>
 */
public interface HasOriginatingSipUriAVP<T extends HasOriginatingSipUriAVP<T>> extends AVPContainer<T> {

    default T setOriginatingSipUri(final String value) {
        setAVP(AVP.create(SgdGddConstants.AVP_ORIGINATING_SIP_URI, value));
        return self();
    }

    default String getOriginatingSipUri() {
        final var avp = findAVP(SgdGddConstants.AVP_ORIGINATING_SIP_URI);
        return avp != null ? avp.getDataAsString() : null;
    }
}
