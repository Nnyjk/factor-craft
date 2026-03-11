package com.factorcraft.module.material.trait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

public class TraitInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraitInitializer.class);
    
    public static void initialize() {
        LOGGER.info("Initializing trait system...");
        LOGGER.info("Trait system initialized");
    }
    
    public static void loadTraits(Path configPath) {
        TraitRegistry.loadFromConfig(configPath);
        LOGGER.info("Loaded {} traits", TraitRegistry.size());
    }
}