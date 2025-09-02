package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Destination-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Destination-Host AVP
 * as defined in RFC 6733. The Destination-Host AVP identifies the endpoint where the Diameter message should be routed.
 * </p>
 */
public interface HasDestinationHostAVP<T extends HasDestinationHostAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Destination-Host AVP.
     *
     * @param destinationHost The destination host identifier to set.
     */
    default T setDestinationHost(final String destinationHost) {
        setAVP(AVP.create(DiameterConstants.AVP_DESTINATION_HOST, destinationHost));
        return self();
    }

    /**
     * Gets the Destination-Host from this message.
     *
     * @return The destination host identifier, or null if not found.
     */
    default String getDestinationHost() {
        final AVP destinationHostAVP = findAVP(DiameterConstants.AVP_DESTINATION_HOST);
        if (destinationHostAVP != null) {
            return destinationHostAVP.getDataAsString();
        }
        return null;
    }
}
