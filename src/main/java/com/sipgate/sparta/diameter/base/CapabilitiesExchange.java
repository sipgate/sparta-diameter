package com.sipgate.sparta.diameter.base;

import java.net.InetAddress;

/**
 * Interface for Capabilities Exchange messages (CER/CEA).
 * Contains common functionality for exchanging capabilities between Diameter peers.
 * This interface provides default implementations that can be used as mixins.
 */
public interface CapabilitiesExchange {

    // These methods need to be implemented by the concrete classes
    void addAVP(AVP avp);
    void setAVP(AVP avp);
    AVP findAVP(int code);

    /**
     * Adds a Host-IP-Address AVP with proper encoding for IPv4 and IPv6 addresses.
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
     */
    default void setVendorId(final int vendorId) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.VENDOR_ID, true, vendorId));
    }

    /**
     * Sets the Product-Name AVP.
     */
    default void setProductName(final String productName) {
        setAVP(AVP.createStringAVP(DiameterConstants.PRODUCT_NAME, false, productName));
    }

    /**
     * Adds a Supported-Vendor-Id AVP.
     */
    default void addSupportedVendorId(final int vendorId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.SUPPORTED_VENDOR_ID, true, vendorId));
    }

    /**
     * Adds an Auth-Application-Id AVP.
     */
    default void addAuthApplicationId(final int applicationId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.AUTH_APPLICATION_ID, true, applicationId));
    }

    /**
     * Adds an Acct-Application-Id AVP.
     */
    default void addAcctApplicationId(final int applicationId) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.ACCT_APPLICATION_ID, true, applicationId));
    }

    /**
     * Sets the Firmware-Revision AVP.
     */
    default void setFirmwareRevision(final int firmwareRevision) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.FIRMWARE_REVISION, false, firmwareRevision));
    }

    /**
     * Gets the Vendor-Id from this message.
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
     */
    default int getFirmwareRevision() {
        final AVP firmwareRevisionAVP = findAVP(DiameterConstants.FIRMWARE_REVISION);
        if (firmwareRevisionAVP != null && firmwareRevisionAVP.getData().length >= 4) {
            return firmwareRevisionAVP.getDataAsInt();
        }
        return -1;
    }
}
