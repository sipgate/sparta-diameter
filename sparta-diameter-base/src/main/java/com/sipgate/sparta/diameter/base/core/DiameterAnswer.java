package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;

public interface DiameterAnswer extends HasErrorMessageAVP, HasFailedAVP, HasProxyInfoAVPs {
}
