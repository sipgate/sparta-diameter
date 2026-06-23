package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.Has3gppAaaServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasNon3gppUserDataAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.load.mixins.HasLoadAVPs;

/** Server-Assignment-Answer (SAA) — 3GPP TS 29.273 §8.1.2.4.2. */
public interface ServerAssignmentAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP, HasLoadAVPs,
                HasNon3gppUserDataAVP, Has3gppAaaServerNameAVP {

    final class In extends IncomingAnswer implements ServerAssignmentAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_SERVER_ASSIGNMENT, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements ServerAssignmentAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_SERVER_ASSIGNMENT, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }
}
