package com.factorcraft.module.creature.registry;

import com.factorcraft.module.creature.api.CreatureApi;
import com.factorcraft.module.creature.api.CreatureRegistrar;
import com.factorcraft.module.creature.model.CreatureAbilitySpec;
import com.factorcraft.module.creature.model.DropPoolSpec;
import com.factorcraft.module.creature.model.SpawnRuleSpec;
import net.minecraft.server.world.ServerWorld;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 怪物与掉落统一注册中心。
 */
public final class CreatureDropRegistry implements CreatureRegistrar, CreatureApi {
    private static final CreatureDropRegistry INSTANCE = new CreatureDropRegistry();

    private final Map<String, SpawnRuleSpec> spawnRules = new LinkedHashMap<>();
    private final Map<String, DropPoolSpec> dropPools = new LinkedHashMap<>();
    private final Map<String, CreatureAbilitySpec> abilities = new LinkedHashMap<>();

    private CreatureDropRegistry() {}

    public static CreatureDropRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void registerSpawnRule(SpawnRuleSpec spec) {
        putIfAbsent(spawnRules, spec.ruleId(), spec);
    }

    @Override
    public synchronized void registerDropPool(DropPoolSpec spec) {
        putIfAbsent(dropPools, spec.poolId(), spec);
    }

    @Override
    public synchronized void registerAbility(CreatureAbilitySpec spec) {
        putIfAbsent(abilities, spec.abilityId(), spec);
    }

    @Override
    public synchronized List<SpawnRuleSpec> getSpawnRules(ServerWorld world, int tier) {
        String dim = world.getRegistryKey().getValue().toString();
        return spawnRules.values().stream()
                .filter(rule -> tier >= rule.minTier() && tier <= rule.maxTier())
                .filter(rule -> rule.dimensions().isEmpty() || rule.dimensions().contains(dim))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<DropPoolSpec> getDropPools(String entityId, int tier) {
        return dropPools.values().stream()
                .filter(pool -> pool.entityId().equals(entityId))
                .filter(pool -> tier >= pool.minTier() && tier <= pool.maxTier())
                .collect(Collectors.toList());
    }

    public synchronized List<CreatureAbilitySpec> getAbilities(String entityId, int tier) {
        return abilities.values().stream()
                .filter(ability -> ability.entityId().equals(entityId))
                .filter(ability -> tier >= ability.minTier() && tier <= ability.maxTier())
                .collect(Collectors.toList());
    }

    public synchronized Map<String, SpawnRuleSpec> spawnRulesView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(spawnRules));
    }

    public synchronized Map<String, DropPoolSpec> dropPoolsView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(dropPools));
    }

    public synchronized Map<String, CreatureAbilitySpec> abilitiesView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(abilities));
    }

    private static <T> void putIfAbsent(Map<String, T> map, String key, T value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("duplicate id: " + key);
        }
        map.put(key, value);
    }
}
