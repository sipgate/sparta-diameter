package com.sipgate.sparta.diameter.base.transport;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/**
 * Bundles an {@link OutgoingRequest} with the hop-by-hop and end-to-end
 * identifiers to be stamped into its header at encode time.
 */
public record OutgoingRequestEnvelope(OutgoingRequest<?> request, HopByHopId hopByHop, EndToEndId endToEnd) {}
