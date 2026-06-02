package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipAuthDataItemAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Multimedia-Auth-Request (MAR) — 3GPP TS 29.229 §6.1.7. */
public interface MultimediaAuthRequest
        extends _3gppRequest, HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVP, HasSipAuthDataItemAVP,
                HasSipNumberAuthItemsAVP, HasServerNameAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<MultimediaAuthAnswer.Out> implements MultimediaAuthRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<MultimediaAuthAnswer.In> implements MultimediaAuthRequest {
        public Out() {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX);
        }
    }
}
