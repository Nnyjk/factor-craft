package com.factorcraft.module.material.config;

import com.factorcraft.module.material.model.EnchantSpec;
import com.factorcraft.module.material.model.MaterialSpec;
import com.factorcraft.module.material.model.StatusEffectSpec;
import com.factorcraft.module.material.model.TraitSpec;

import java.util.List;

/**
 * M2 数据化配置聚合。
 */
public record MaterialsM2Config(
        List<MaterialSpec> materials,
        List<TraitSpec> traits,
        List<EnchantSpec> enchants,
        List<StatusEffectSpec> statuses
) {}
