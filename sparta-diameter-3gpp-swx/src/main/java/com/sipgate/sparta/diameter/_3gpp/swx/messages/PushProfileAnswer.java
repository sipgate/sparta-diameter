package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.swx.SwxAnswer;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasLocalTimeZoneAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasAccessNetworkInfoAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Push-Profile-Answer (PPA) — 3GPP TS 29.273 §8.1.2.4.1. */
public interface PushProfileAnswer
        extends SwxAnswer,
                HasDrmpAVP, HasAccessNetworkInfoAVP, HasLocalTimeZoneAVP,
                HasSupportedFeaturesAVPs {

    final class In extends IncomingAnswer implements PushProfileAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_PUSH_PROFILE, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements PushProfileAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_PUSH_PROFILE, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }
}
