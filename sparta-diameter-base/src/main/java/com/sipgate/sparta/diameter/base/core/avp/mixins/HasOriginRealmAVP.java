package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Origin-Realm AVP.
 * <p>
 * This interface provides default implementations for handling the Origin-Realm AVP
 * as defined in RFC 6733. The Origin-Realm AVP contains the realm of the originating host.
 * </p>
 */
public interface HasOriginRealmAVP<T extends HasOriginRealmAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Origin-Realm AVP.
     *
     * @param originRealm The origin realm identifier to set.
     */
    default T setOriginRealm(final String originRealm) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ORIGIN_REALM, 0), originRealm));
        return self();
    }

    /**
     * Gets the Origin-Realm from this message.
     *
     * @return The origin realm identifier, or null if not found.
     */
    default String getOriginRealm() {
        final AVP originRealmAVP = findAVP(new AVPKey(DiameterConstants.AVP_ORIGIN_REALM, 0));
        if (originRealmAVP != null) {
            return originRealmAVP.getDataAsString();
        }
        return null;
    }
}
