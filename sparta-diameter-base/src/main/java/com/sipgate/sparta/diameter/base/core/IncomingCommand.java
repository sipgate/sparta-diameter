package com.sipgate.sparta.diameter.base.core;

public interface IncomingCommand {
    HopByHopId hopByHopId();
    EndToEndId endToEndId();
    int getCommandCode();
    int getApplicationId();
}
