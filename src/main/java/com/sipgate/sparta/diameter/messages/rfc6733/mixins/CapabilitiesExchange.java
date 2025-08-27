package com.sipgate.sparta.diameter.messages.rfc6733.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Capabilities Exchange messages (CER/CEA).
 * <p>
 * This interface provides common functionality as mixins for exchanging capabilities between Diameter peers
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3">RFC 6733, Section 5.3</a>.
 * </p>
 */
public interface CapabilitiesExchange extends AVPContainer {

    /**
     * Sets the Vendor-Id AVP.
     *
     * @param vendorId The vendor ID to set.
     */
    default void setVendorId(final long vendorId) {
        setAVP(AVP.create(DiameterConstants.AVP_VENDOR_ID, vendorId));
    }

    /**
     * Sets the Product-Name AVP.
     *
     * @param productName The product name to set.
     */
    default void setProductName(final String productName) {
        setAVP(AVP.create(DiameterConstants.AVP_PRODUCT_NAME, productName));
    }

    /**
     * Adds a Supported-Vendor-Id AVP.
     *
     * @param vendorId The supported vendor ID to add.
     */
    default void addSupportedVendorId(final long vendorId) {
        addAVP(AVP.create(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, vendorId));
    }

    /**
     * Adds an Auth-Application-Id AVP.
     *
     * @param applicationId The authentication application ID to add.
     */
    default void addAuthApplicationId(final long applicationId) {
        addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, applicationId));
    }

    /**
     * Adds an Acct-Application-Id AVP.
     *
     * @param applicationId The accounting application ID to add.
     */
    default void addAcctApplicationId(final long applicationId) {
        addAVP(AVP.create(DiameterConstants.AVP_ACCT_APPLICATION_ID, applicationId));
    }

    /**
     * Sets the Firmware-Revision AVP.
     *
     * @param firmwareRevision The firmware revision to set.
     */
    default void setFirmwareRevision(final long firmwareRevision) {
        setAVP(AVP.create(DiameterConstants.AVP_FIRMWARE_REVISION, firmwareRevision));
    }

    /**
     * Gets the Vendor-Id from this message.
     *
     * @return The vendor ID, or -1 if not present.
     */
    default long getVendorId() {
        final AVP vendorIdAVP = findAVP(DiameterConstants.AVP_VENDOR_ID);
        if (vendorIdAVP != null && vendorIdAVP.getData().length >= 4) {
            return vendorIdAVP.getDataAsUnsignedInt();
        }
        return -1;
    }

    /**
     * Gets the Product-Name from this message.
     *
     * @return The product name, or null if not present.
     */
    default String getProductName() {
        final AVP productNameAVP = findAVP(DiameterConstants.AVP_PRODUCT_NAME);
        if (productNameAVP != null) {
            return productNameAVP.getDataAsString();
        }
        return null;
    }

    /**
     * Gets the Firmware-Revision from this message.
     *
     * @return The firmware revision, or -1 if not present.
     */
    default long getFirmwareRevision() {
        final AVP firmwareRevisionAVP = findAVP(DiameterConstants.AVP_FIRMWARE_REVISION);
        if (firmwareRevisionAVP != null && firmwareRevisionAVP.getData().length >= 4) {
            return firmwareRevisionAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
