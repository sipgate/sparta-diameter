package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.net.InetAddress;

/** Mixin for messages carrying the GMLC-Address AVP (3GPP TS 29.173 §6.4.7, code 2405). */
public interface HasGmlcAddressAVP extends AVPContainer {

    default void setGmlcAddress(final InetAddress value) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_GMLC_ADDRESS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default InetAddress getGmlcAddress() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_GMLC_ADDRESS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsIPAddress() : null;
    }
}
