package com.factorcraft.module.creature;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.creature.registry.CreatureDropRegistry;
import com.factorcraft.module.event.FactorTierChangeEvent;

/**
 * M1b：日切后按新 Tier 刷新怪物规则/掉落池可视状态。
 */
final class CreatureTierSyncService {
    private static final String TRACKED_ENTITY_ID = "minecraft:zombie";

    private final CreatureDropRegistry registry;

    CreatureTierSyncService(CreatureDropRegistry registry) {
        this.registry = registry;
    }

    void onTierChanged(FactorTierChangeEvent event) {
        int tier = event.currentTier();
        String worldKey = event.world().getRegistryKey().getValue().toString();
        long spawnRules = registry.getSpawnRules(event.world(), tier).size();
        long dropPools = registry.dropPoolsView().values().stream()
                .filter(pool -> tier >= pool.minTier() && tier <= pool.maxTier())
                .count();
        long abilities = registry.getAbilities(TRACKED_ENTITY_ID, tier).size();

        FactorCraftMod.LOGGER.info(
                "[M1b] 日切联动完成: world={}, day={}, tier={}, spawnRules={}, dropPools={}, zombieAbilities={}",
                worldKey,
                event.dayIndex(),
                tier,
                spawnRules,
                dropPools,
                abilities
        );
    }
}
