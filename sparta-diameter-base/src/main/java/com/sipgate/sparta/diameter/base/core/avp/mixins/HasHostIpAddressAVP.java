package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.net.InetAddress;

/**
 * Interface for Diameter messages that include Host-IP-Address AVP.
 * <p>
 * This interface provides default implementations for handling the Host-IP-Address AVP
 * as defined in RFC 6733. The Host-IP-Address AVP informs a peer of the sender's IP address.
 * </p>
 */
public interface HasHostIpAddressAVP<T extends HasHostIpAddressAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Host-IP-Address AVP.
     *
     * @param hostIpAddress The host IP address to set.
     */
    default T setHostIpAddress(final InetAddress hostIpAddress) {
        setAVP(AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, hostIpAddress));
        return self();
    }

    /**
     * Gets the Host-IP-Address from this message.
     *
     * @return The host IP address, or null if not found.
     */
    default InetAddress getHostIpAddress() {
        final AVP hostIpAddressAVP = findAVP(DiameterConstants.AVP_HOST_IP_ADDRESS);
        if (hostIpAddressAVP != null) {
            return hostIpAddressAVP.getDataAsIPAddress();
        }
        return null;
    }
}
