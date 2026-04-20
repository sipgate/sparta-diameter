package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Proxy-Info AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Proxy-Info ]} in proxiable messages.
 * For messages where Proxy-Info is optional and singular, use {@link HasProxyInfoAVP} instead.
 * </p>
 */
public interface HasProxyInfoAVPs extends AVPContainer {

    default void addProxyInfo(final GroupedAVP proxyInfo) {
        addAVP(proxyInfo);
    }

    default List<GroupedAVP> getProxyInfos() {
        final List<GroupedAVP> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0))) {
            result.add((GroupedAVP) avp);
        }
        return result;
    }

    default GroupedAVP getFirstProxyInfo() {
        final List<GroupedAVP> all = getProxyInfos();
        return all.isEmpty() ? null : all.get(0);
    }

    default void addAllProxyInfos(final Collection<GroupedAVP> proxyInfos) {
        for (final GroupedAVP avp : proxyInfos) {
            addProxyInfo(avp);
        }
    }
}
