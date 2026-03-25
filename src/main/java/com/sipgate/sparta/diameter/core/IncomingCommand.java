package com.sipgate.sparta.diameter.core;

public interface IncomingCommand {
    HopByHopId hopByHopId();
    EndToEndId endToEndId();
}
