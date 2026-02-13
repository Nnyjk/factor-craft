package com.factorcraft.module.material.service;

import com.factorcraft.module.material.config.MaterialsM2Config;
import com.factorcraft.module.material.model.EnchantCategory;
import com.factorcraft.module.material.model.EnchantSpec;
import com.factorcraft.module.material.model.MaterialSpec;
import com.factorcraft.module.material.model.StatusEffectSpec;
import com.factorcraft.module.material.model.TraitSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * M2 核心服务：在给定 seed + tier + 维度条件下，生成可复现的材料联动结果。
 */
public final class MaterialsM2Service {
    private final MaterialsM2Config config;

    public MaterialsM2Service(MaterialsM2Config config) {
        this.config = config;
    }

    public MaterialRollResult roll(long seed, String dimension, int tierLevel) {
        Random random = new Random(seed ^ dimension.hashCode() ^ (tierLevel * 131L));

        List<MaterialSpec> availableMaterials = config.materials().stream()
                .filter(m -> m.dimensions().contains(dimension))
                .filter(m -> m.level().level() <= (tierLevel + 1))
                .sorted(Comparator.comparing(MaterialSpec::materialId))
                .toList();
        MaterialSpec material = pickOne(availableMaterials, random, "material");

        List<TraitSpec> traitPool = config.traits().stream()
                .filter(t -> tierLevel >= t.minTier() && tierLevel <= t.maxTier())
                .filter(t -> t.dimensions() == null || t.dimensions().isEmpty() || t.dimensions().contains(dimension))
                .sorted(Comparator.comparing(TraitSpec::traitId))
                .toList();

        List<String> traitIds = weightedPickTraitIds(traitPool, random, material.traitSlots());

        List<String> enchantIds = new ArrayList<>();
        for (EnchantCategory category : EnchantCategory.values()) {
            List<EnchantSpec> categoryPool = config.enchants().stream()
                    .filter(e -> e.category() == category)
                    .filter(e -> tierLevel >= e.minTier() && tierLevel <= e.maxTier())
                    .sorted(Comparator.comparing(EnchantSpec::enchantId))
                    .toList();
            if (!categoryPool.isEmpty()) {
                enchantIds.add(pickOne(categoryPool, random, "enchant").enchantId());
            }
        }

        List<StatusRoll> statuses = composeStatuses(tierLevel, traitIds);
        return new MaterialRollResult(seed, dimension, tierLevel, material.materialId(), traitIds, enchantIds, statuses);
    }

    public List<StatusRoll> settleDailyStatusPool(int previousTier, int nextTier) {
        int effectiveTier = Math.max(previousTier, nextTier);
        return config.statuses().stream()
                .filter(s -> effectiveTier >= s.minTier() && effectiveTier <= s.maxTier())
                .limit(3)
                .map(s -> new StatusRoll(s.statusId(), s.type(), s.baseDurationSeconds(), 1, "day_settlement"))
                .toList();
    }

    private List<StatusRoll> composeStatuses(int tierLevel, List<String> traitIds) {
        List<StatusRoll> result = new ArrayList<>();
        for (String traitId : traitIds) {
            TraitSpec trait = config.traits().stream()
                    .filter(t -> t.traitId().equals(traitId))
                    .findFirst()
                    .orElseThrow();
            if (trait.linkedStatusId() == null || trait.linkedStatusId().isBlank()) {
                continue;
            }
            StatusEffectSpec status = config.statuses().stream()
                    .filter(s -> s.statusId().equals(trait.linkedStatusId()))
                    .filter(s -> tierLevel >= s.minTier() && tierLevel <= s.maxTier())
                    .findFirst()
                    .orElse(null);
            if (status == null) {
                continue;
            }
            int duration = (int) Math.max(1, Math.round(status.baseDurationSeconds() * trait.durationMultiplier()));
            int stacks = Math.min(status.maxStacks(), 1 + Math.max(0, tierLevel - status.minTier()) / 2);
            result.add(new StatusRoll(status.statusId(), status.type(), duration, stacks, traitId));
        }
        return result;
    }

    private List<String> weightedPickTraitIds(List<TraitSpec> pool, Random random, int count) {
        List<TraitSpec> candidates = new ArrayList<>(pool);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count && !candidates.isEmpty(); i++) {
            double totalWeight = candidates.stream().mapToDouble(TraitSpec::weight).sum();
            double cursor = random.nextDouble() * totalWeight;
            TraitSpec selected = candidates.getFirst();
            for (TraitSpec spec : candidates) {
                cursor -= spec.weight();
                if (cursor <= 0) {
                    selected = spec;
                    break;
                }
            }
            result.add(selected.traitId());
            candidates.remove(selected);
        }
        return result;
    }

    private static <T> T pickOne(List<T> list, Random random, String label) {
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("empty pool for " + label);
        }
        return list.get(random.nextInt(list.size()));
    }
}
