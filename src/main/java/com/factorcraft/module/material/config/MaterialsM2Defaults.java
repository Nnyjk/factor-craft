package com.factorcraft.module.material.config;

import com.factorcraft.module.material.model.EnchantCategory;
import com.factorcraft.module.material.model.EnchantSpec;
import com.factorcraft.module.material.model.MaterialLevel;
import com.factorcraft.module.material.model.MaterialSpec;
import com.factorcraft.module.material.model.StatusEffectSpec;
import com.factorcraft.module.material.model.StatusStackRule;
import com.factorcraft.module.material.model.StatusType;
import com.factorcraft.module.material.model.TraitCategory;
import com.factorcraft.module.material.model.TraitSpec;

import java.util.List;
import java.util.Set;

/**
 * 对齐 docs/02 的 M2 首版默认池。
 */
public final class MaterialsM2Defaults {
    private MaterialsM2Defaults() {}

    public static MaterialsM2Config create() {
        return new MaterialsM2Config(
                List.of(
                        new MaterialSpec("factorcraft:copper_core", MaterialLevel.LV1_CRUDE, 1, 0.8, 0.45, 1, Set.of("minecraft:overworld")),
                        new MaterialSpec("factorcraft:nether_steel", MaterialLevel.LV2_INDUSTRIAL, 2, 1.2, 0.62, 2, Set.of("minecraft:overworld", "minecraft:the_nether")),
                        new MaterialSpec("factorcraft:cobalt_matrix", MaterialLevel.LV3_DIMENSIONAL, 3, 1.8, 0.78, 3, Set.of("minecraft:the_nether", "minecraft:the_end")),
                        new MaterialSpec("factorcraft:ancient_alloy", MaterialLevel.LV4_ANCIENT, 4, 2.4, 0.9, 4, Set.of("minecraft:overworld", "minecraft:the_end")),
                        new MaterialSpec("factorcraft:arbiter_shard", MaterialLevel.LV5_ARBITER, 5, 3.1, 1.0, 5, Set.of("minecraft:the_end"))
                ),
                List.of(
                        new TraitSpec("factorcraft:resonant", TraitCategory.GENERAL, 2, 4, 1.0, Set.of(), "factorcraft:tidal_focus", 1.15),
                        new TraitSpec("factorcraft:lagged_core", TraitCategory.GENERAL, 0, 3, 0.8, Set.of(), "factorcraft:steady_blessing", 1.2),
                        new TraitSpec("factorcraft:conductor", TraitCategory.GENERAL, 1, 4, 0.9, Set.of(), "factorcraft:mana_suppression", 0.8),
                        new TraitSpec("factorcraft:anchored", TraitCategory.GENERAL, 1, 4, 1.1, Set.of(), "factorcraft:steady_blessing", 1.3),
                        new TraitSpec("factorcraft:nether_surge", TraitCategory.DIMENSION, 2, 4, 1.0, Set.of("minecraft:the_nether"), "factorcraft:tidal_focus", 1.1),
                        new TraitSpec("factorcraft:end_silence", TraitCategory.DIMENSION, 2, 4, 1.0, Set.of("minecraft:the_end"), "factorcraft:void_vertigo", 0.85),
                        new TraitSpec("factorcraft:overworld_bloom", TraitCategory.DIMENSION, 0, 3, 1.0, Set.of("minecraft:overworld"), "factorcraft:steady_blessing", 1.1),
                        new TraitSpec("factorcraft:arbiter_mark", TraitCategory.ENDGAME, 3, 4, 1.0, Set.of(), "factorcraft:arbiter_wing", 1.0),
                        new TraitSpec("factorcraft:chaos_gift", TraitCategory.ENDGAME, 3, 4, 0.7, Set.of(), "factorcraft:rift_burn", 1.25)
                ),
                List.of(
                        new EnchantSpec("factorcraft:tide_rend", EnchantCategory.WEAPON, 2, 4, 1.0),
                        new EnchantSpec("factorcraft:rift_pierce", EnchantCategory.WEAPON, 3, 4, 1.2),
                        new EnchantSpec("factorcraft:stabilized_riposte", EnchantCategory.WEAPON, 1, 3, 0.8),
                        new EnchantSpec("factorcraft:harmonic_quarry", EnchantCategory.TOOL, 1, 3, 0.9),
                        new EnchantSpec("factorcraft:overload_sampling", EnchantCategory.TOOL, 3, 4, 1.1),
                        new EnchantSpec("factorcraft:static_calibrate", EnchantCategory.TOOL, 0, 4, 0.7),
                        new EnchantSpec("factorcraft:phase_guard", EnchantCategory.ARMOR, 1, 4, 1.0),
                        new EnchantSpec("factorcraft:entropy_weave", EnchantCategory.ARMOR, 2, 4, 1.1),
                        new EnchantSpec("factorcraft:dawn_shelter", EnchantCategory.ARMOR, 0, 2, 0.8)
                ),
                List.of(
                        new StatusEffectSpec("factorcraft:tidal_focus", StatusType.BUFF, 120, 3, StatusStackRule.STACK_INTENSITY, 2, 4),
                        new StatusEffectSpec("factorcraft:steady_blessing", StatusType.BUFF, 180, 2, StatusStackRule.REFRESH_DURATION, 0, 3),
                        new StatusEffectSpec("factorcraft:dimension_sync", StatusType.BUFF, 90, 2, StatusStackRule.REFRESH_DURATION, 1, 4),
                        new StatusEffectSpec("factorcraft:arbiter_wing", StatusType.BUFF, 60, 1, StatusStackRule.OVERRIDE_BY_STRONGER, 3, 4),
                        new StatusEffectSpec("factorcraft:mana_suppression", StatusType.DEBUFF, 80, 3, StatusStackRule.STACK_INTENSITY, 0, 4),
                        new StatusEffectSpec("factorcraft:rift_burn", StatusType.DEBUFF, 75, 4, StatusStackRule.STACK_INTENSITY, 2, 4),
                        new StatusEffectSpec("factorcraft:structure_fatigue", StatusType.DEBUFF, 120, 2, StatusStackRule.REFRESH_DURATION, 3, 4),
                        new StatusEffectSpec("factorcraft:void_vertigo", StatusType.DEBUFF, 45, 1, StatusStackRule.OVERRIDE_BY_STRONGER, 2, 4)
                )
        );
    }
}
