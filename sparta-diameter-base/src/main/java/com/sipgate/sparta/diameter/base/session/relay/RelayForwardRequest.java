package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;

import java.util.List;
import java.util.Objects;

/**
 * An outgoing request frame for
 * {@link com.sipgate.sparta.diameter.base.session.DiameterSession#relay}: a copy
 * of a received request carried over unchanged (all AVPs, original
 * Origin-Host/Realm and header flags) prepared for one more relay hop
 * (RFC 6733 §6.1.9).
 *
 * <p>Relay agents must forward proxiable requests with every AVP unchanged
 * except for one appended {@code Route-Record} identifying the relay. The
 * concrete {@code Out} classes of the known commands cannot express that (they
 * build typed answers/mutations, and {@code GenericCommand.Out} is neither an
 * {@link OutgoingRequest} nor a {@link HasRouteRecordAVPs}), so this type
 * carries the copied AVPs and the received command's header, implementing the
 * Route-Record mixin so the application can {@link #addRouteRecord(String)}
 * before relaying.</p>
 *
 * <p>Identifiers are NOT carried over: {@code relay} mints a fresh hop-by-hop
 * id on the egress connection and preserves the end-to-end id from the
 * {@code received} argument itself (RFC 6733 §3). This frame only supplies
 * command code, flags and AVPs to the wire encoder.</p>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class RelayForwardRequest extends OutgoingRequest implements HasRouteRecordAVPs {

    /**
     * Copies every AVP of the received request into the forward frame; header
     * flags (command code, P-bit, retransmit bit, application id) carry over.
     *
     * @param received the request this agent received and now forwards
     */
    public RelayForwardRequest(final IncomingRequest<?> received) {
        super(Objects.requireNonNull(received, "received must not be null").getCommandCode(),
                received.isProxiable(), received.getApplicationId());
        final List<AVP> avps = received.getAVPs();
        for (int i = 0; i < avps.size(); i++) {
            addAVP(avps.get(i));
        }
    }

    @Override
    public String getCommandName() {
        return "Relay Forward (cmd " + getCommandCode() + ")";
    }
}
