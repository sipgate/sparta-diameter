package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the 3GPP-AAA-Server-Name AVP (3GPP TS 29.273 §8.2.3.6, code 318). */
public interface Has3gppAaaServerNameAVP extends AVPContainer {

    /**
     * Sets the 3GPP-AAA-Server-Name AVP.
     *
     * @param value the 3GPP-AAA-Server-Name DiameterIdentity value to set.
     */
    default void set3gppAaaServerName(final String value) {
        setAVP(AVP.create(new AVPKey(SwxConstants.AVP_3GPP_AAA_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    /**
     * Gets the 3GPP-AAA-Server-Name value from this message.
     *
     * @return the 3GPP-AAA-Server-Name DiameterIdentity value, or null if not present.
     */
    default String get3gppAaaServerName() {
        final AVP avp = findAVP(new AVPKey(SwxConstants.AVP_3GPP_AAA_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
