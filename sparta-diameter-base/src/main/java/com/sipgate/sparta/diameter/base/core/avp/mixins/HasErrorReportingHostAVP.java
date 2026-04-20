package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Error-Reporting-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Error-Reporting-Host AVP
 * as defined in RFC 6733. The Error-Reporting-Host AVP contains the identity of the Diameter host that sent the Result-Code AVP to a value other than 2001 (Success).
 * </p>
 */
public interface HasErrorReportingHostAVP extends AVPContainer {

    /**
     * Sets the Error-Reporting-Host AVP.
     *
     * @param errorReportingHost The error reporting host identifier to set.
     */
    default void setErrorReportingHost(final String errorReportingHost) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ERROR_REPORTING_HOST, 0), errorReportingHost));
    }

    /**
     * Gets the Error-Reporting-Host from this message.
     *
     * @return The error reporting host identifier, or null if not found.
     */
    default String getErrorReportingHost() {
        final AVP errorReportingHostAVP = findAVP(new AVPKey(DiameterConstants.AVP_ERROR_REPORTING_HOST, 0));
        if (errorReportingHostAVP != null) {
            return errorReportingHostAVP.getDataAsString();
        }
        return null;
    }
}
