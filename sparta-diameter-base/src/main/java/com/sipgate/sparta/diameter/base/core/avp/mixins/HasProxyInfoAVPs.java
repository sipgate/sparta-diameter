package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
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
public interface HasProxyInfoAVPs<T extends HasProxyInfoAVPs<T>> extends AVPContainer<T> {

    default T addProxyInfo(final GroupedAVP proxyInfo) {
        addAVP(proxyInfo);
        return self();
    }

    default List<GroupedAVP> getProxyInfos() {
        final List<GroupedAVP> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_PROXY_INFO)) {
            result.add((GroupedAVP) avp);
        }
        return result;
    }

    default GroupedAVP getFirstProxyInfo() {
        final List<GroupedAVP> all = getProxyInfos();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllProxyInfos(final Collection<GroupedAVP> proxyInfos) {
        for (final GroupedAVP avp : proxyInfos) {
            addProxyInfo(avp);
        }
        return self();
    }
}
