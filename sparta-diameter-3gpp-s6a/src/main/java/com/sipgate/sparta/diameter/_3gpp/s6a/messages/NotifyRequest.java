package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * Notify-Request (NOR) — 3GPP TS 29.272 §7.2.17.
 */
public interface NotifyRequest
        extends _3gppRequest,
                HasDrmpAVP,
                HasDestinationHostAVP, HasDestinationRealmAVP,
                HasUserNameAVP, HasSupportedFeaturesAVPs,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<NotifyAnswer.Out>
            implements NotifyRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_NOTIFY, true, retransmitted,
                  S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<NotifyAnswer.In>
            implements NotifyRequest {

        public Out() {
            super(S6aConstants.CMD_NOTIFY, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
