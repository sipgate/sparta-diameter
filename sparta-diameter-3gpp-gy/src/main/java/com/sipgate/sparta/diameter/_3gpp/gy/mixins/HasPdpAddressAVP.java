package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.net.InetAddress;

/** Mixin for containers carrying the PDP-Address AVP (TS 32.299, code 1227). */
public interface HasPdpAddressAVP extends AVPContainer {

    default void setPdpAddress(final InetAddress value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_PDP_ADDRESS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default InetAddress getPdpAddress() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_PDP_ADDRESS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsIPAddress() : null;
    }
}
