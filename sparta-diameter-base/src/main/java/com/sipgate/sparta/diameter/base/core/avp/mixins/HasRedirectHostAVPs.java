package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Redirect-Host AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Redirect-Host ]} in answer messages that support redirection.
 * </p>
 */
public interface HasRedirectHostAVPs extends AVPContainer {

    default void addRedirectHost(final String redirectHost) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_REDIRECT_HOST, 0), redirectHost));
    }

    default List<String> getRedirectHosts() {
        final List<String> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_REDIRECT_HOST, 0))) {
            result.add(avp.getDataAsString());
        }
        return result;
    }

    default String getFirstRedirectHost() {
        final List<String> all = getRedirectHosts();
        return all.isEmpty() ? null : all.get(0);
    }

    default void addAllRedirectHosts(final Collection<String> redirectHosts) {
        for (final String host : redirectHosts) {
            addRedirectHost(host);
        }
    }
}
