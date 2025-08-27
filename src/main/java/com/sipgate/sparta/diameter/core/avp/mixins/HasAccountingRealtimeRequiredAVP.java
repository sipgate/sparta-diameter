package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Accounting-Realtime-Required AVP.
 * <p>
 * This interface provides default implementations for handling the Accounting-Realtime-Required AVP
 * as defined in RFC 6733. The Accounting-Realtime-Required AVP is used to inform the client whether real-time accounting is required.
 * </p>
 */
public interface HasAccountingRealtimeRequiredAVP<T extends HasAccountingRealtimeRequiredAVP<T>> extends AVPContainer {

    /**
     * Sets the Accounting-Realtime-Required AVP.
     *
     * @param accountingRealtimeRequired The accounting realtime required value to set.
     */
    default T setAccountingRealtimeRequired(final int accountingRealtimeRequired) {
        setAVP(AVP.create(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED, accountingRealtimeRequired));
        return self();
    }

    /**
     * Gets the Accounting-Realtime-Required from this message.
     *
     * @return The accounting realtime required value, or -1 if not found.
     */
    default int getAccountingRealtimeRequired() {
        final AVP accountingRealtimeRequiredAVP = findAVP(DiameterConstants.AVP_ACCOUNTING_REALTIME_REQUIRED);
        if (accountingRealtimeRequiredAVP != null) {
            return accountingRealtimeRequiredAVP.getDataAsInt();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
