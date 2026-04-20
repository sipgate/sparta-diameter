package com.sipgate.sparta.diameter.base.core;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base class for all outgoing Diameter request messages.
 */
public abstract class OutgoingRequest<A extends Answer>
        extends Request<A> {

    protected OutgoingRequest(final int commandCode, final boolean proxiable,
                               final int applicationId) {
        super(commandCode, proxiable, false, applicationId);
    }

    public void writeTo(final DataOutputStream out,
                        final HopByHopId hopByHop, final EndToEndId endToEnd) throws IOException {
        super.writeTo(out, hopByHop, endToEnd);
    }
}
