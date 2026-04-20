package com.sipgate.sparta.diameter.base.core;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Generic command for unknown or unsupported command codes.
 * <p>
 * {@code In} is used when an unrecognised command arrives from the wire.
 * {@code Out} is used for relay or forwarding use cases where the caller
 * supplies the full header context explicitly.
 * </p>
 */
public abstract class GenericCommand extends Command {

    protected GenericCommand(final int commandCode, final boolean request, final boolean proxiable,
                             final boolean error, final boolean retransmitted,
                             final int applicationId) {
        super(commandCode, request, proxiable, error, retransmitted, applicationId);
    }

    @Override
    public String getCommandName() {
        return "Unknown[code=" + getCommandCode() + "]";
    }

    public static final class In extends GenericCommand implements IncomingCommand {

        private final HopByHopId hopByHop;
        private final EndToEndId endToEnd;

        public In(final int commandCode, final boolean request, final boolean proxiable,
                  final boolean error, final boolean retransmitted, final int applicationId,
                  final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, request, proxiable, error, retransmitted, applicationId);
            this.hopByHop = hopByHop;
            this.endToEnd = endToEnd;
        }

        @Override public HopByHopId hopByHopId() { return hopByHop; }
        @Override public EndToEndId endToEndId()  { return endToEnd; }
    }

    /**
     * Relay / forwarding frame: carries its own identifiers and can be written
     * directly to the wire. Not an {@link OutgoingAnswer} or {@link OutgoingRequest}
     * in the standard sense; use {@link #writeTo} directly.
     */
    public static final class Out extends GenericCommand {

        private final HopByHopId storedHopByHop;
        private final EndToEndId storedEndToEnd;

        public Out(final int commandCode, final boolean request, final boolean proxiable,
                   final boolean error, final boolean retransmitted, final int applicationId,
                   final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, request, proxiable, error, retransmitted, applicationId);
            this.storedHopByHop = hopByHop;
            this.storedEndToEnd = endToEnd;
        }

        public void writeTo(final DataOutputStream out) throws IOException {
            writeTo(out, storedHopByHop, storedEndToEnd);
        }
    }
}
