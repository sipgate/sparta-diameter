package com.sipgate.sparta.diameter.base.core;

public interface DiameterPackageFactory {

    /**
     * Returns null if this factory does not handle the given command code / application ID.
     */
    IncomingCommand createForParsing(int commandCode, int applicationId, boolean isRequest,
                                     HopByHopId hopByHop, EndToEndId endToEnd,
                                     boolean retransmitted);

    /**
     * Returns null if this factory does not handle the given command code / application ID.
     */
    OutgoingAnswer<?> createAnswer(int commandCode, int applicationId,
                                   HopByHopId hopByHop, EndToEndId endToEnd);
}
