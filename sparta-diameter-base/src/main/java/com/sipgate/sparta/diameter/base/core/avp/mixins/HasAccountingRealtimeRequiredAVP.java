package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Realtime-Required AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Realtime-Required AVP
 * as defined in RFC 6733. The Accounting-Realtime-Required AVP is used to inform the client whether real-time accounting is required.
 * </p>
 */
public interface HasAccountingRealtimeRequiredAVP<T extends HasAccountingRealtimeRequiredAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Accounting-Realtime-Required AVP.
     *
     * @param accountingRealtimeRequired The accounting realtime required value to set.
     */
    default T setAccountingRealtimeRequired(final int accountingRealtimeRequired) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED, 0), accountingRealtimeRequired));
        return self();
    }

    /**
     * Gets the Accounting-Realtime-Required from this message.
     *
     * @return The accounting realtime required value, or -1 if not found.
     */
    default int getAccountingRealtimeRequired() {
        final AVP accountingRealtimeRequiredAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED, 0));
        if (accountingRealtimeRequiredAVP != null) {
            return accountingRealtimeRequiredAVP.getDataAsInt();
        }
        return -1;
    }
}
