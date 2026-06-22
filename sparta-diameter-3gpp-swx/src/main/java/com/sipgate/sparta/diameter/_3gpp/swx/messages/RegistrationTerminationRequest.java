package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasDeregistrationReasonAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Request (RTR) — 3GPP TS 29.273 §8.1.2.2.3. */
public interface RegistrationTerminationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasSupportedFeaturesAVPs, HasDeregistrationReasonAVP {

    final class In extends IncomingRequest<RegistrationTerminationAnswer.Out> implements RegistrationTerminationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SwxConstants.CMD_REGISTRATION_TERMINATION, true, retransmitted, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<RegistrationTerminationAnswer.In> implements RegistrationTerminationRequest {
        public Out() {
            super(SwxConstants.CMD_REGISTRATION_TERMINATION, true, SwxConstants.APP_ID_SWX);
        }
    }
}
