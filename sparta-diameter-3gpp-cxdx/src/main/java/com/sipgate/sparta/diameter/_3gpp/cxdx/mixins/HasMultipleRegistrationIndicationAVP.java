package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Multiple-Registration-Indication AVP (3GPP TS 29.229 §6.3.48, code 648). */
public interface HasMultipleRegistrationIndicationAVP extends AVPContainer {

    default void setMultipleRegistrationIndication(final int value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_MULTIPLE_REGISTRATION_INDICATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getMultipleRegistrationIndication() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_MULTIPLE_REGISTRATION_INDICATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
