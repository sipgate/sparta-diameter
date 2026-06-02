package com.sipgate.sparta.diameter.ietf.mip6.integrated.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.mip6.integrated.Mip6IntegratedConstants;

/** Mixin for messages carrying the Service-Selection AVP (RFC 5778 §3.4, code 493). */
public interface HasServiceSelectionAVP extends AVPContainer {

    default void setServiceSelection(final String value) {
        setAVP(AVP.create(new AVPKey(Mip6IntegratedConstants.AVP_SERVICE_SELECTION, 0), value));
    }

    default String getServiceSelection() {
        final var avp = findAVP(new AVPKey(Mip6IntegratedConstants.AVP_SERVICE_SELECTION, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
