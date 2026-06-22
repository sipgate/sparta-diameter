package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the AAA-Failure-Indication AVP (3GPP TS 29.273 §8.2.3.24, code 1518). */
public interface HasAaaFailureIndicationAVP extends AVPContainer {

    /**
     * Sets the AAA-Failure-Indication AVP.
     *
     * @param value the AAA-Failure-Indication Unsigned32 value to set.
     */
    default void setAaaFailureIndication(final long value) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_AAA_FAILURE_INDICATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    /**
     * Gets the AAA-Failure-Indication value from this message.
     *
     * @return the AAA-Failure-Indication Unsigned32 value, or -1L if not present.
     */
    default long getAaaFailureIndication() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_AAA_FAILURE_INDICATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
