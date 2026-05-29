package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasIdaFlagsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * Insert-Subscriber-Data-Answer (IDA) — 3GPP TS 29.272 §7.2.10.
 */
public interface InsertSubscriberDataAnswer
        extends _3gppAnswer,
                HasDrmpAVP,
                HasIdaFlagsAVP,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer
            implements InsertSubscriberDataAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true,
                  S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer
            implements InsertSubscriberDataAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true,
                  S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }
}
