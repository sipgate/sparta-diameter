package com.sipgate.sparta.diameter._3gpp.common;

import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthSessionStateAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;

public interface _3gppAnswer extends
    HasFailedAVP, HasProxyInfoAVPs,
    HasSessionIdAVP, HasVendorSpecificApplicationIdAVP, HasAuthSessionStateAVP, HasSupportedFeaturesAVPs, HasExperimentalResultAVP
{
}
