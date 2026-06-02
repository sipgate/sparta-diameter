package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasIdrFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasResetIdAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSubscriptionDataAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Insert-Subscriber-Data-Request/Answer (Request) (IDR) — 3GPP TS 29.272 §7.2.9-10. */
public interface InsertSubscriberDataRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasSupportedFeaturesAVPs, HasSubscriptionDataAVP, HasIdrFlagsAVP, HasResetIdAVPs, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<InsertSubscriberDataAnswer.Out> implements InsertSubscriberDataRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<InsertSubscriberDataAnswer.In> implements InsertSubscriberDataRequest {
        public Out() {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
