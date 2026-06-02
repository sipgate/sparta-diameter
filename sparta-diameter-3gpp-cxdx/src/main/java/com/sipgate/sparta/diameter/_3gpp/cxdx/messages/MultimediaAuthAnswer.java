package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipAuthDataItemAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Multimedia-Auth-Answer (MAA) — 3GPP TS 29.229 §6.1.8. */
public interface MultimediaAuthAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP,
                HasPublicIdentityAVP, HasSipNumberAuthItemsAVP,
                HasSipAuthDataItemAVPs, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements MultimediaAuthAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements MultimediaAuthAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }
}
