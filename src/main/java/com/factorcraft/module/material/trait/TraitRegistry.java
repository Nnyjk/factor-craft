package com.factorcraft.module.material.trait;

import com.factorcraft.module.material.config.TraitsConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TraitRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraitRegistry.class);
    private static final Map<String, TraitDefinition> TRAITS = new ConcurrentHashMap<>();
    private static final Map<com.factorcraft.module.material.model.TraitCategory, List<TraitDefinition>> BY_CATEGORY = new ConcurrentHashMap<>();
    
    static {
        for (com.factorcraft.module.material.model.TraitCategory category : com.factorcraft.module.material.model.TraitCategory.values()) {
            BY_CATEGORY.put(category, new ArrayList<>());
        }
    }
    
    public static void register(TraitDefinition trait) {
        Objects.requireNonNull(trait, "Trait cannot be null");
        TRAITS.put(trait.id(), trait);
        BY_CATEGORY.get(trait.category()).add(trait);
        LOGGER.debug("Registered trait: {}", trait.id());
    }
    
    public static Optional<TraitDefinition> get(String traitId) {
        return Optional.ofNullable(TRAITS.get(traitId));
    }
    
    public static Collection<TraitDefinition> getAll() {
        return Collections.unmodifiableCollection(TRAITS.values());
    }
    
    public static List<TraitDefinition> getByCategory(com.factorcraft.module.material.model.TraitCategory category) {
        return Collections.unmodifiableList(BY_CATEGORY.getOrDefault(category, Collections.emptyList()));
    }
    
    public static boolean exists(String traitId) {
        return TRAITS.containsKey(traitId);
    }
    
    public static int size() {
        return TRAITS.size();
    }
    
    public static void clear() {
        TRAITS.clear();
        BY_CATEGORY.values().forEach(List::clear);
    }
    
    public static void loadFromConfig(Path configPath) {
        try {
            TraitsConfigParser parser = new TraitsConfigParser();
            List<TraitsConfigParser.TraitDefinition> definitions = parser.parse(configPath);
            
            for (TraitsConfigParser.TraitDefinition def : definitions) {
                TraitDefinition trait = new TraitDefinition(
                    def.getId(), def.getName(), def.getType(), def.getCategory(),
                    def.getDescription(), def.getEffects(), def.getMaxLevel(),
                    def.getLevelScaling(), def.getResonance(), def.getIncompatible(),
                    def.getWeight(), def.getTierRange()
                );
                register(trait);
            }
            LOGGER.info("Loaded {} traits from {}", definitions.size(), configPath);
        } catch (Exception e) {
            LOGGER.error("Failed to load traits from config: {}", configPath, e);
        }
    }
    
    public static List<TraitDefinition> getTraitsForTier(int tier) {
        return TRAITS.values().stream()
            .filter(trait -> trait.tierRange()[0] <= tier && tier <= trait.tierRange()[1])
            .toList();
    }
}