package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Class AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Class ]} in STR/STA.
 * Method names use the {@code ClassAVP} suffix to avoid collision with
 * {@link Object#getClass()}.
 * </p>
 */
public interface HasClassAVPs extends AVPContainer {

    default void addClass(final byte[] classValue) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_CLASS, 0), classValue));
    }

    default List<byte[]> getClasses() {
        final List<byte[]> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_CLASS, 0))) {
            result.add(avp.getData());
        }
        return result;
    }

    default byte[] getFirstClass() {
        final List<byte[]> all = getClasses();
        return all.isEmpty() ? null : all.get(0);
    }

    default void addAllClasses(final Collection<byte[]> classValues) {
        for (final byte[] value : classValues) {
            addClass(value);
        }
    }
}
