package com.sipgate.sparta.diameter.base.core;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class OutgoingAnswer extends Answer {



    protected OutgoingAnswer(final int commandCode, final boolean proxiable,
                              final int applicationId,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        this(commandCode, proxiable, false, applicationId, hopByHop, endToEnd);
    }

    /**
     * Constructor for error answers where the E-bit must be set.
     */
    protected OutgoingAnswer(final int commandCode, final boolean proxiable, final boolean error,
                              final int applicationId,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, error, applicationId, hopByHop, endToEnd);
    }

    public void writeTo(final DataOutputStream out) throws IOException {
        writeTo(out, hopByHopId(), endToEndId());
    }
}
