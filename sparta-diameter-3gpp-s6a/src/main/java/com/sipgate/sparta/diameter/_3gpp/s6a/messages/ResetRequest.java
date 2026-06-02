package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasResetIdAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSubscriptionDataAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSubscriptionDataDeletionAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUserIdAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Reset-Request/Answer (Request) (RSR) — 3GPP TS 29.272 §7.2.15-16. */
public interface ResetRequest
        extends _3gppRequest,
                HasDrmpAVP, HasSupportedFeaturesAVPs, HasUserIdAVPs, HasResetIdAVPs, HasSubscriptionDataAVP, HasSubscriptionDataDeletionAVP, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<ResetAnswer.Out> implements ResetRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_RESET, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<ResetAnswer.In> implements ResetRequest {
        public Out() {
            super(S6aConstants.CMD_RESET, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
