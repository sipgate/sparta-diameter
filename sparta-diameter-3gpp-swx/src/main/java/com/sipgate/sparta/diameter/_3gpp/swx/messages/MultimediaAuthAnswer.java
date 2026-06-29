package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipAuthDataItemAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.Has3gppAaaServerNameAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.load.mixins.HasLoadAVPs;

/** Multimedia-Auth-Answer (MAA) — 3GPP TS 29.273 §8.1.2.1. */
public interface MultimediaAuthAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP, HasLoadAVPs,
                HasSipNumberAuthItemsAVP, HasSipAuthDataItemAVPs, Has3gppAaaServerNameAVP {

    final class In extends IncomingAnswer implements MultimediaAuthAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_MULTIMEDIA_AUTH, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Multimedia-Auth Answer";
        }
    }

    final class Out extends OutgoingAnswer implements MultimediaAuthAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_MULTIMEDIA_AUTH, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Multimedia-Auth Answer";
        }
    }
}
