package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the TFT-Packet-Filter-Information AVP (3GPP TS 29.212, code 1013). */
public interface HasTftPacketFilterInformationAVP extends AVPContainer {

    default void setTftPacketFilterInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_TFT_PACKET_FILTER_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getTftPacketFilterInformation() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_TFT_PACKET_FILTER_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
