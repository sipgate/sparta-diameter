package com.sipgate.sparta.diameter.ietf.cc;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.nio.ByteBuffer;

/**
 * Factories for credit-control AVPs that {@code AVP.create} cannot encode correctly.
 */
public final class CcAvps {

    private CcAvps() {}

    /**
     * Creates a Value-Digits AVP (Integer64, RFC 4006 §8.10) with its mandatory 8-byte encoding.
     * <p>
     * Do not use {@code AVP.create(key, long)} for Value-Digits: it writes a 4-byte Unsigned32,
     * which is malformed on the wire.
     * </p>
     */
    public static AVP valueDigits(final long value) {
        final byte[] encoded = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
        return AVP.createRaw(new AVPKey(CcConstants.AVP_VALUE_DIGITS, 0), false, true, false, encoded);
    }
}
