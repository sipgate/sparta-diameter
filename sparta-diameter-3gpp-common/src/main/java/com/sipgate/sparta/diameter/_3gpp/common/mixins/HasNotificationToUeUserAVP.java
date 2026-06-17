package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Notification-To-UE-User AVP (3GPP, code 1478). */
public interface HasNotificationToUeUserAVP extends AVPContainer {

    default void setNotificationToUeUser(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_NOTIFICATION_TO_UE_USER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getNotificationToUeUser() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_NOTIFICATION_TO_UE_USER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
