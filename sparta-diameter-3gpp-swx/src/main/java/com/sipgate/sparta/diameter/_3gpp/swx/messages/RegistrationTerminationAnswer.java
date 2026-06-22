package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.swx.SwxAnswer;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Answer (RTA) — 3GPP TS 29.273 §8.1.2.2.3. */
public interface RegistrationTerminationAnswer
        extends SwxAnswer,
                HasDrmpAVP, HasSupportedFeaturesAVPs {

    final class In extends IncomingAnswer implements RegistrationTerminationAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_REGISTRATION_TERMINATION, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements RegistrationTerminationAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SwxConstants.CMD_REGISTRATION_TERMINATION, true, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }
}
