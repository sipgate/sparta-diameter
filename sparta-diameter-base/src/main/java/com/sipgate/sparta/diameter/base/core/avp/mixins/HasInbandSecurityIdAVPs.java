package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Inband-Security-Id AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Inband-Security-Id ]} in CER/CEA.
 * </p>
 */
public interface HasInbandSecurityIdAVPs<T extends HasInbandSecurityIdAVPs<T>> extends AVPContainer<T> {

    default T addInbandSecurityId(final long inbandSecurityId) {
        addAVP(AVP.create(DiameterConstants.AVP_INBAND_SECURITY_ID, inbandSecurityId));
        return self();
    }

    default List<Long> getInbandSecurityIds() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_INBAND_SECURITY_ID)) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default Long getFirstInbandSecurityId() {
        final List<Long> all = getInbandSecurityIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllInbandSecurityIds(final Collection<Long> inbandSecurityIds) {
        for (final long id : inbandSecurityIds) {
            addInbandSecurityId(id);
        }
        return self();
    }
}
