package com.factorcraft.module.creature;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.creature.model.CreatureAbilitySpec;
import com.factorcraft.module.creature.model.DropPoolSpec;
import com.factorcraft.module.creature.model.LootEntrySpec;
import com.factorcraft.module.creature.model.SpawnRuleSpec;
import com.factorcraft.module.creature.registry.CreatureDropRegistry;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.shared.ModuleMilestone;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 怪物生态与凋落物模块。
 */
public final class CreatureDropModule implements FactorCraftModule {
    private static final AtomicBoolean BOOTSTRAPPED = new AtomicBoolean(false);

    private static final String ENTITY_ZOMBIE = "minecraft:zombie";
    private static final String DIMENSION_OVERWORLD = "minecraft:overworld";

    private static final String RULE_OVERWORLD_ZOMBIE_CORE = "overworld_zombie_core";
    private static final String DROP_POOL_ZOMBIE_T2_T4 = "zombie_t2_t4";
    private static final String ABILITY_ZOMBIE_OVERLOAD_BURST = "zombie_overload_burst";

    private static final int TIER_LOW_ENERGY = 1;
    private static final int TIER_OVERLOAD = 4;
    private static final int TIER_STABLE = 2;
    private static final int TIER_HIGH_ENERGY = 3;

    private static final int DEFAULT_ZOMBIE_WEIGHT = 80;
    private static final int DEFAULT_GROUP_SIZE_MIN = 1;
    private static final int DEFAULT_GROUP_SIZE_MAX = 4;

    private static final String ITEM_ROTTEN_FLESH = "minecraft:rotten_flesh";
    private static final String ITEM_IRON_INGOT = "minecraft:iron_ingot";
    private static final double DROP_CHANCE_ROTTEN_FLESH = 0.90;
    private static final double DROP_CHANCE_IRON_INGOT = 0.08;
    private static final String DROP_CONDITION_DEFAULT = "default";
    private static final String DROP_CONDITION_HIGH_TIER_BONUS = "high_tier_bonus";

    private static final String ABILITY_EFFECT_SPEED_BOOST = "speed_boost";
    private static final String ABILITY_EFFECT_SHOCKWAVE = "shockwave";
    private static final int ABILITY_COOLDOWN_TICKS = 200;

    @Override
    public String moduleId() {
        return "creature_drop";
    }

    @Override
    public void initialize() {
        CreatureDropRegistry registry = CreatureDropRegistry.getInstance();
        if (BOOTSTRAPPED.compareAndSet(false, true)) {
            bootstrapDefaults(registry);
            CreatureTierSyncService syncService = new CreatureTierSyncService(registry);
            SimpleFactorEventBus.getInstance().subscribe(FactorTierChangeEvent.class, EventPriority.NORMAL, syncService::onTierChanged);
        }
        FactorCraftMod.LOGGER.info("[{}] 怪物与凋落物模块 M1b 已启用（生态刷新/掉落池/日切联动）", ModuleMilestone.M1B_CREATURE_AND_DROPS);
    }

    private static void bootstrapDefaults(CreatureDropRegistry registry) {
        registry.registerSpawnRule(new SpawnRuleSpec(
                RULE_OVERWORLD_ZOMBIE_CORE,
                ENTITY_ZOMBIE,
                Set.of(DIMENSION_OVERWORLD),
                TIER_LOW_ENERGY,
                TIER_OVERLOAD,
                DEFAULT_ZOMBIE_WEIGHT,
                DEFAULT_GROUP_SIZE_MIN,
                DEFAULT_GROUP_SIZE_MAX
        ));

        registry.registerDropPool(new DropPoolSpec(
                DROP_POOL_ZOMBIE_T2_T4,
                ENTITY_ZOMBIE,
                TIER_STABLE,
                TIER_OVERLOAD,
                List.of(
                        new LootEntrySpec(ITEM_ROTTEN_FLESH, DROP_CHANCE_ROTTEN_FLESH, 1, 3, DROP_CONDITION_DEFAULT),
                        new LootEntrySpec(ITEM_IRON_INGOT, DROP_CHANCE_IRON_INGOT, 1, 1, DROP_CONDITION_HIGH_TIER_BONUS)
                )
        ));

        registry.registerAbility(new CreatureAbilitySpec(
                ABILITY_ZOMBIE_OVERLOAD_BURST,
                ENTITY_ZOMBIE,
                TIER_HIGH_ENERGY,
                TIER_OVERLOAD,
                Set.of(ABILITY_EFFECT_SPEED_BOOST, ABILITY_EFFECT_SHOCKWAVE),
                ABILITY_COOLDOWN_TICKS
        ));
    }
}
