package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Auth-Application-Id AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Auth-Application-Id ]} in CER/CEA.
 * For messages where Auth-Application-Id is mandatory and singular
 * (e.g. RAR, ASR, STR), use {@link HasAuthApplicationIdAVP} instead.
 * </p>
 */
public interface HasAuthApplicationIdAVPs<T extends HasAuthApplicationIdAVPs<T>> extends AVPContainer<T> {

    default T addAuthApplicationId(final long authApplicationId) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), authApplicationId));
        return self();
    }

    default List<Long> getAuthApplicationIds() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0))) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default Long getFirstAuthApplicationId() {
        final List<Long> all = getAuthApplicationIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllAuthApplicationIds(final Collection<Long> authApplicationIds) {
        for (final long id : authApplicationIds) {
            addAuthApplicationId(id);
        }
        return self();
    }
}
