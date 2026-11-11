package com.sipgate.sparta.diameter.base.core;

/**
 * Answers to commands no registered factory handles: {@code In} for a received answer
 * (e.g. the downstream answer of a relayed {@link GenericRequest}), {@code Out} for a
 * locally generated answer to a {@link GenericRequest}.
 */
public final class GenericAnswer {

    private GenericAnswer() {
    }

    public static final class In extends IncomingAnswer {

        public In(final int commandCode, final boolean proxiable, final int applicationId,
                  final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, proxiable, applicationId, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Unknown Answer[code=" + getCommandCode() + "]";
        }
    }

    public static final class Out extends OutgoingAnswer {

        Out(final int commandCode, final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(commandCode, proxiable, applicationId, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Unknown Answer[code=" + getCommandCode() + "]";
        }
    }
}
