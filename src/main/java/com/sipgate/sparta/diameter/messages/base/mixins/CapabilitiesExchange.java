package com.sipgate.sparta.diameter.messages.base.mixins;

import com.sipgate.sparta.diameter.core.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.mixins.DiameterMessage;

import java.net.InetAddress;

/**
 * Interface for Capabilities Exchange messages (CER/CEA).
 * <p>
 * This interface provides common functionality as mixins for exchanging capabilities between Diameter peers
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3">RFC 6733, Section 5.3</a>.
 * </p>
 */
public interface CapabilitiesExchange extends DiameterMessage {

    /**
     * Adds a Host-IP-Address AVP with proper encoding for IPv4 and IPv6 addresses.
     *
     * @param address The IP address to add.
     * @throws IllegalArgumentException if the provided address is invalid.
     */
    default void addHostIPAddress(final InetAddress address) {
        final byte[] addressBytes = address.getAddress();
        final byte[] data;

        if (addressBytes.length == 4) {
            // IPv4 - Address Type 1
            data = new byte[6];
            data[0] = 0x00;
            data[1] = 0x01;
            System.arraycopy(addressBytes, 0, data, 2, 4);
        } else if (addressBytes.length == 16) {
            // IPv6 - Address Type 2
            data = new byte[18];
            data[0] = 0x00;
            data[1] = 0x02;
            System.arraycopy(addressBytes, 0, data, 2, 16);
        } else {
            throw new IllegalArgumentException("Invalid IP address");
        }

        addAVP(new AVP(DiameterConstants.HOST_IP_ADDRESS, true, data));
    }

    /**
     * Sets the Vendor-Id AVP.
     *
     * @param vendorId The vendor ID to set.
     */
    default void setVendorId(final int vendorId) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.VENDOR_ID, true, vendorId));
    }

    /**
     * Sets the Product-Name AVP.
     *
     * @param productName The product name to set.
     */
    default void setProductName(final String productName) {
        setAVP(AVP.createStringAVP(DiameterConstants.PRODUCT_NAME, false, productName));
    }

    /**
     * Adds a Supported-Vendor-Id AVP.
     *
     * @param vendorId The supported vendor ID to add.
     */
    default void addSupportedVendorId(final int vendorId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.SUPPORTED_VENDOR_ID, true, vendorId));
    }

    /**
     * Adds an Auth-Application-Id AVP.
     *
     * @param applicationId The authentication application ID to add.
     */
    default void addAuthApplicationId(final int applicationId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.AUTH_APPLICATION_ID, true, applicationId));
    }

    /**
     * Adds an Acct-Application-Id AVP.
     *
     * @param applicationId The accounting application ID to add.
     */
    default void addAcctApplicationId(final int applicationId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.ACCT_APPLICATION_ID, true, applicationId));
    }

    /**
     * Sets the Firmware-Revision AVP.
     *
     * @param firmwareRevision The firmware revision to set.
     */
    default void setFirmwareRevision(final int firmwareRevision) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.FIRMWARE_REVISION, false, firmwareRevision));
    }

    /**
     * Gets the Vendor-Id from this message.
     *
     * @return The vendor ID, or -1 if not present.
     */
    default int getVendorId() {
        final AVP vendorIdAVP = findAVP(DiameterConstants.VENDOR_ID);
        if (vendorIdAVP != null && vendorIdAVP.getData().length >= 4) {
            return vendorIdAVP.getDataAsInt();
        }
        return -1;
    }

    /**
     * Gets the Product-Name from this message.
     *
     * @return The product name, or null if not present.
     */
    default String getProductName() {
        final AVP productNameAVP = findAVP(DiameterConstants.PRODUCT_NAME);
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
    default int getFirmwareRevision() {
        final AVP firmwareRevisionAVP = findAVP(DiameterConstants.FIRMWARE_REVISION);
        if (firmwareRevisionAVP != null && firmwareRevisionAVP.getData().length >= 4) {
            return firmwareRevisionAVP.getDataAsInt();
        }
        return -1;
    }
}
