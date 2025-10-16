package com.xingmot.gtmadvancedhatch.integration.jade.caps;

import java.util.UUID;

public interface IAdaptiveNetCap {

    default boolean isAutoRebind() {
        return false;
    };

    default boolean isSlaveTerminal() {
        return false;
    };

    UUID getUUID();

    String getName();

    long getFrequency();
}
