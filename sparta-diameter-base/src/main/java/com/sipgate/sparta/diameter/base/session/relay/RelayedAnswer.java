package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.AVP;

import java.util.List;

/**
 * A generic outgoing answer for the relay answer path (RFC 6733 §6.2.2).
 *
 * <p>Relay agents must forward a downstream answer upstream unchanged except for the
 * hop-by-hop identifier, which is restored to the value the original request carried.
 * There is no per-application {@code Out} type for an arbitrary forwarded answer, so
 * this class copies the downstream answer's header flags and AVPs verbatim and swaps
 * in the restored identifiers. Origin-Host and Origin-Realm belong to the answering
 * endpoint and pass through untouched — the relay must not stamp its own identity
 * (RFC 6733 §6.2.2, and §8.16's rule that a modified Origin-Host would invalidate
 * the Origin-State-Id).</p>
 */
public final class RelayedAnswer extends OutgoingAnswer {

    private RelayedAnswer(final int commandCode, final boolean proxiable, final boolean error,
                          final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, error, applicationId, hopByHop, endToEnd);
    }

    /**
     * Builds the upstream copy of a downstream answer with the original hop-by-hop
     * identifier restored. The end-to-end identifier, command code, application id,
     * flags (including the E-bit), and all AVPs are copied unchanged.
     *
     * @param downstream        the answer received from the downstream peer
     * @param originalHopByHop   the hop-by-hop identifier of the original inbound request
     * @return the answer to send upstream
     */
    public static RelayedAnswer of(final IncomingAnswer downstream, final HopByHopId originalHopByHop) {
        final RelayedAnswer restored = new RelayedAnswer(
                downstream.getCommandCode(),
                downstream.isProxiable(),
                downstream.isError(),
                downstream.getApplicationId(),
                originalHopByHop,
                downstream.endToEndId());
        final List<AVP> avps = downstream.getAVPs();
        for (final AVP avp : avps) {
            restored.addAVP(avp);
        }
        return restored;
    }

    @Override
    public String getCommandName() {
        return "Relayed Answer";
    }
}
