package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Route-Record AVP.
 * <p>
 * This interface provides default implementations for handling the Route-Record AVP
 * as defined in RFC 6733. The Route-Record AVP is used to identify the Diameter peers that have processed the message.
 * </p>
 */
public interface HasRouteRecordAVP<T extends HasRouteRecordAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Route-Record AVP.
     *
     * @param routeRecord The route record to set.
     */
    default T setRouteRecord(final String routeRecord) {
        setAVP(AVP.create(DiameterConstants.AVP_ROUTE_RECORD, routeRecord));
        return self();
    }

    /**
     * Gets the Route-Record from this message.
     *
     * @return The route record, or null if not found.
     */
    default String getRouteRecord() {
        final AVP routeRecordAVP = findAVP(DiameterConstants.AVP_ROUTE_RECORD);
        if (routeRecordAVP != null) {
            return routeRecordAVP.getDataAsString();
        }
        return null;
    }
}
