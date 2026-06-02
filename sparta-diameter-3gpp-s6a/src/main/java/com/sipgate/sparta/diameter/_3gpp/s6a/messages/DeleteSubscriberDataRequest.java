package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasContextIdentifierAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasDsrFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasEdrxRelatedRatAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasExternalIdentifierAVPs;
import com.sipgate.sparta.diameter._3gpp.s6t.mixins.HasScefIdAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSsCodeAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasTraceReferenceAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasTsCodeAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Delete-Subscriber-Data-Request/Answer (Request) (DSR) — 3GPP TS 29.272 §7.2.11-12. */
public interface DeleteSubscriberDataRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasSupportedFeaturesAVPs, HasDsrFlagsAVP, HasScefIdAVP, HasContextIdentifierAVPs, HasTraceReferenceAVP, HasTsCodeAVPs, HasSsCodeAVPs, HasEdrxRelatedRatAVP, HasExternalIdentifierAVPs, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<DeleteSubscriberDataAnswer.Out> implements DeleteSubscriberDataRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_DELETE_SUBSCRIBER_DATA, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<DeleteSubscriberDataAnswer.In> implements DeleteSubscriberDataRequest {
        public Out() {
            super(S6aConstants.CMD_DELETE_SUBSCRIBER_DATA, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
