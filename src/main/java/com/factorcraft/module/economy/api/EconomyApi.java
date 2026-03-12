package com.factorcraft.module.economy.api;

import com.factorcraft.module.economy.config.EconomyConfig;

public interface EconomyApi {
    boolean isEnabled();
    EconomyConfig getConfig();
}