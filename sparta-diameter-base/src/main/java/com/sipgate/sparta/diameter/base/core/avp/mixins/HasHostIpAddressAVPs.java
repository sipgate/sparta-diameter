package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying one or more Host-IP-Address AVPs.
 * <p>
 * RFC 6733 mandates {@code 1*} Host-IP-Address in CER/CEA, meaning at least one
 * occurrence is required. This interface models the repeatable (0..n) access
 * pattern; callers are responsible for ensuring at least one value is present.
 * </p>
 */
public interface HasHostIpAddressAVPs extends AVPContainer {

    default void addHostIpAddress(final InetAddress hostIpAddress) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_HOST_IP_ADDRESS, 0), hostIpAddress));
    }

    default List<InetAddress> getHostIpAddresses() {
        final List<InetAddress> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_HOST_IP_ADDRESS, 0))) {
            result.add(avp.getDataAsIPAddress());
        }
        return result;
    }

    default InetAddress getFirstHostIpAddress() {
        final List<InetAddress> all = getHostIpAddresses();
        return all.isEmpty() ? null : all.get(0);
    }

    default void addAllHostIpAddresses(final Collection<InetAddress> hostIpAddresses) {
        for (final InetAddress addr : hostIpAddresses) {
            addHostIpAddress(addr);
        }
    }
}
