package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasErrorReportingHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasResultCodeAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;

/**
 * Error Answer message as defined in RFC 6733 §7.2.
 *
 * <p>E-bit answers do not conform to the normal CCF for their command. This sealed interface
 * covers both the inbound ({@link In}) and outbound ({@link Out}) representations. The type
 * itself signals the error condition — callers do not need to inspect the E-bit separately.
 */
public sealed interface ErrorAnswer
        extends HasResultCodeAVP, HasSessionIdAVP, HasOriginStateIdAVP,
                HasErrorMessageAVP, HasErrorReportingHostAVP,
                HasFailedAVP, HasExperimentalResultAVP, HasProxyInfoAVP
        permits ErrorAnswer.In, ErrorAnswer.Out {

    /**
     * A received E-bit answer. Produced by {@link DiameterMessageFactory} when the E-bit is
     * set on an inbound answer; correlation with the pending request is done by hop-by-hop ID.
     */
    final class In extends IncomingAnswer implements ErrorAnswer {

        public In(final int commandCode, final int applicationId,
                  final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, false, true, applicationId, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Error Answer";
        }
    }

    /**
     * An outgoing E-bit answer. Constructed by a request handler that needs to signal a
     * protocol error to the peer.
     */
    final class Out extends OutgoingAnswer implements ErrorAnswer {
        Out(final int commandCode, final boolean proxiable,
            final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, proxiable, true, applicationId, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Error Answer";
        }
    }
}
