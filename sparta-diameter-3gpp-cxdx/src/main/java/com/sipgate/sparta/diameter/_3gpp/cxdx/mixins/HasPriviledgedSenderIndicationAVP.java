package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Priviledged-Sender-Indication AVP (3GPP TS 29.229 §6.3.52, code 652). */
public interface HasPriviledgedSenderIndicationAVP extends AVPContainer {

    default void setPriviledgedSenderIndication(final int value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_PRIVILEDGED_SENDER_INDICATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getPriviledgedSenderIndication() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_PRIVILEDGED_SENDER_INDICATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
