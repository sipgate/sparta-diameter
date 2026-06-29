package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.load.mixins.HasLoadAVPs;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

/** Notify-Request/Answer (Answer) (NOA) — 3GPP TS 29.272 §7.2.17-18. */
public interface NotifyAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP, HasLoadAVPs, HasSupportedFeaturesAVPs, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements NotifyAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_NOTIFY, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Notify Answer";
        }
    }

    final class Out extends OutgoingAnswer implements NotifyAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_NOTIFY, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Notify Answer";
        }
    }
}
