package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Software-Version AVP (3GPP, code 1403). */
public interface HasSoftwareVersionAVP extends AVPContainer {

    default void setSoftwareVersion(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SOFTWARE_VERSION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getSoftwareVersion() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SOFTWARE_VERSION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
