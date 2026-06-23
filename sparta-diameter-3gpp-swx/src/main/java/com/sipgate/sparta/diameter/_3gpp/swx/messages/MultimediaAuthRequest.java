package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRatTypeAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSipAuthDataItemAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasTerminalInformationAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasVisitedNetworkIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasAaaFailureIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasAnTrustedAVP;
import com.sipgate.sparta.diameter._3gpp.swx.mixins.HasAnidAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Multimedia-Auth-Request (MAR) — 3GPP TS 29.273 §8.1.2.1. */
public interface MultimediaAuthRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasSupportedFeaturesAVPs,
                HasRatTypeAVP, HasAnTrustedAVP, HasAnidAVP, HasVisitedNetworkIdentifierAVP,
                HasTerminalInformationAVP, HasSipAuthDataItemAVP, HasSipNumberAuthItemsAVP,
                HasAaaFailureIndicationAVP {

    final class In extends IncomingRequest<MultimediaAuthAnswer.Out> implements MultimediaAuthRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SwxConstants.CMD_MULTIMEDIA_AUTH, true, retransmitted, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<MultimediaAuthAnswer.In> implements MultimediaAuthRequest {
        public Out() {
            super(SwxConstants.CMD_MULTIMEDIA_AUTH, true, SwxConstants.APP_ID_SWX);
        }
    }
}
