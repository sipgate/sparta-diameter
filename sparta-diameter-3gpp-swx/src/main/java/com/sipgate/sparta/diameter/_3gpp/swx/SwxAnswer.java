package com.sipgate.sparta.diameter._3gpp.swx;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthSessionStateAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;

/**
 * Base interface for SWx answer messages — mirrors {@code _3gppAnswer} but without
 * coupling to {@code common.mixins.HasSupportedFeaturesAVPs}. SWx answers declare
 * their own {@code swx.mixins.HasSupportedFeaturesAVPs} so the duplicate, per-protocol
 * accessor is the single source for SWx (no {@code common} AVP-mixin coupling).
 * <p>
 * Unlike {@code _3gppAnswer}, this base intentionally omits the singular
 * {@code HasProxyInfoAVP}: SWx answer CCFs only ever carry plural Proxy-Info
 * ({@code *[ Proxy-Info ]}), so the plural accessor alone is sufficient.
 */
public interface SwxAnswer extends
    HasFailedAVP, HasProxyInfoAVPs,
    HasSessionIdAVP, HasVendorSpecificApplicationIdAVP, HasAuthSessionStateAVP, HasExperimentalResultAVP {
}
