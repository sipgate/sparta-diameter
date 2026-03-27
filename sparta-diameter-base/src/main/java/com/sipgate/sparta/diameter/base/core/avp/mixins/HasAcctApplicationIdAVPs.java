package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Acct-Application-Id AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Acct-Application-Id ]} in CER/CEA.
 * For messages where Acct-Application-Id is optional and singular
 * (e.g. ACR/ACA), use {@link HasAcctApplicationIdAVP} instead.
 * </p>
 */
public interface HasAcctApplicationIdAVPs<T extends HasAcctApplicationIdAVPs<T>> extends AVPContainer<T> {

    default T addAcctApplicationId(final long acctApplicationId) {
        addAVP(AVP.create(DiameterConstants.AVP_ACCT_APPLICATION_ID, acctApplicationId));
        return self();
    }

    default List<Long> getAcctApplicationIds() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_ACCT_APPLICATION_ID)) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default Long getFirstAcctApplicationId() {
        final List<Long> all = getAcctApplicationIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllAcctApplicationIds(final Collection<Long> acctApplicationIds) {
        for (final long id : acctApplicationIds) {
            addAcctApplicationId(id);
        }
        return self();
    }
}
