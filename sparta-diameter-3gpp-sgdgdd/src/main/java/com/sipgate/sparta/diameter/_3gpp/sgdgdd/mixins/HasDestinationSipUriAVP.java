package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying a Destination-SIP-URI AVP (3GPP TS 29.338 §6.3.3.16, code 3327).
 * <p>
 * UTF8String — public identity of the IMS UE without MSISDN that is the recipient. V flag only.
 * </p>
 */
public interface HasDestinationSipUriAVP<T extends HasDestinationSipUriAVP<T>> extends AVPContainer<T> {

    default T setDestinationSipUri(final String value) {
        setAVP(AVP.create(SgdGddConstants.AVP_DESTINATION_SIP_URI, value));
        return self();
    }

    default String getDestinationSipUri() {
        final var avp = findAVP(SgdGddConstants.AVP_DESTINATION_SIP_URI);
        return avp != null ? avp.getDataAsString() : null;
    }
}
