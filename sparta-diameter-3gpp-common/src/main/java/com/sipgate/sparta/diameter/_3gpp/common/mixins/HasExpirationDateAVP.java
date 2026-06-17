package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import java.util.Date;

/** Mixin for messages carrying the Expiration-Date AVP (3GPP, code 1439). */
public interface HasExpirationDateAVP extends AVPContainer {

    default void setExpirationDate(final Date value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_EXPIRATION_DATE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getExpirationDate() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_EXPIRATION_DATE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
