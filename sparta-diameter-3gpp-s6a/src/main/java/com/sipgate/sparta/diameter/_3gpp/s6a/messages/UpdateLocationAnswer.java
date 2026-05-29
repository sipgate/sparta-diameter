package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSubscriptionDataAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUlaFlagsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * Update-Location-Answer (ULA) — 3GPP TS 29.272 §7.2.4.
 */
public interface UpdateLocationAnswer
        extends _3gppAnswer,
                HasDrmpAVP,
                HasUlaFlagsAVP, HasSubscriptionDataAVP,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer
            implements UpdateLocationAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_UPDATE_LOCATION, true,
                  S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer
            implements UpdateLocationAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_UPDATE_LOCATION, true,
                  S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }
}
