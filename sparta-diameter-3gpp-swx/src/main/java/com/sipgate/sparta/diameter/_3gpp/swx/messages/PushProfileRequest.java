package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasNon3gppUserDataAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasPprFlagsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Push-Profile-Request (PPR) — 3GPP TS 29.273 §8.1.2.4.1. */
public interface PushProfileRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasSupportedFeaturesAVPs,
                HasNon3gppUserDataAVP, HasPprFlagsAVP {

    final class In extends IncomingRequest<PushProfileAnswer.Out> implements PushProfileRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SwxConstants.CMD_PUSH_PROFILE, false, retransmitted, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<PushProfileAnswer.In> implements PushProfileRequest {
        public Out() {
            super(SwxConstants.CMD_PUSH_PROFILE, false, SwxConstants.APP_ID_SWX);
        }
    }
}
