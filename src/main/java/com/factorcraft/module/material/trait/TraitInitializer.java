package com.factorcraft.module.material.trait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

public class TraitInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft:Trait");
    
    public static void initialize() {
        LOGGER.info("[FactorCraft:Trait] Initializing trait system...");
        LOGGER.info("[FactorCraft:Trait] Trait system initialized");
    }
    
    public static void loadTraits(Path configPath) {
        TraitRegistry.loadFromConfig(configPath);
        LOGGER.info("[FactorCraft:Trait] Loaded {} traits", TraitRegistry.size());
    }
}