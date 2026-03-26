package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Destination-Realm AVP.
 * <p>
 * This interface provides default implementations for handling the Destination-Realm AVP
 * as defined in RFC 6733. The Destination-Realm AVP contains the realm to which the message should be routed.
 * </p>
 */
public interface HasDestinationRealmAVP<T extends HasDestinationRealmAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Destination-Realm AVP.
     *
     * @param destinationRealm The destination realm identifier to set.
     */
    default T setDestinationRealm(final String destinationRealm) {
        setAVP(AVP.create(DiameterConstants.AVP_DESTINATION_REALM, destinationRealm));
        return self();
    }

    /**
     * Gets the Destination-Realm from this message.
     *
     * @return The destination realm identifier, or null if not found.
     */
    default String getDestinationRealm() {
        final AVP destinationRealmAVP = findAVP(DiameterConstants.AVP_DESTINATION_REALM);
        if (destinationRealmAVP != null) {
            return destinationRealmAVP.getDataAsString();
        }
        return null;
    }
}
