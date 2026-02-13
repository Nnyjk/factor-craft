package com.factorcraft.module.material.validation;

import com.factorcraft.module.material.config.MaterialsM2Config;
import com.factorcraft.module.material.model.EnchantSpec;
import com.factorcraft.module.material.model.MaterialSpec;
import com.factorcraft.module.material.model.StatusEffectSpec;
import com.factorcraft.module.material.model.TraitSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * M2 配置模型校验。
 */
public final class MaterialsM2SpecValidator {
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_/.-]+$");

    private MaterialsM2SpecValidator() {}

    public static void validate(MaterialsM2Config config) {
        require(config != null, "config must not be null");
        validateMaterials(config.materials());
        validateTraits(config.traits());
        validateEnchants(config.enchants());
        validateStatuses(config.statuses());
    }

    private static void validateMaterials(List<MaterialSpec> materials) {
        require(materials != null && !materials.isEmpty(), "materials must not be empty");
        Set<String> ids = new HashSet<>();
        for (MaterialSpec spec : materials) {
            require(spec != null, "material must not be null");
            validateId(spec.materialId(), "materialId");
            require(ids.add(spec.materialId()), "duplicate materialId: " + spec.materialId());
            require(spec.level() != null, "material level missing");
            require(spec.harvestTier() >= 0, "harvestTier must be >= 0");
            require(spec.toughness() > 0, "toughness must be > 0");
            require(spec.factorConductivity() > 0 && spec.factorConductivity() <= 1.0, "factorConductivity must be (0,1]");
            require(spec.traitSlots() >= spec.level().minTraitSlots(), "traitSlots below level minimum");
            require(spec.dimensions() != null && !spec.dimensions().isEmpty(), "dimensions must not be empty");
        }
    }

    private static void validateTraits(List<TraitSpec> traits) {
        require(traits != null && !traits.isEmpty(), "traits must not be empty");
        Set<String> ids = new HashSet<>();
        for (TraitSpec spec : traits) {
            require(spec != null, "trait must not be null");
            validateId(spec.traitId(), "traitId");
            require(ids.add(spec.traitId()), "duplicate traitId: " + spec.traitId());
            require(spec.category() != null, "trait category missing");
            require(spec.minTier() >= 0 && spec.maxTier() <= 4 && spec.minTier() <= spec.maxTier(), "trait tier window invalid");
            require(spec.weight() > 0, "trait weight must be > 0");
            require(spec.durationMultiplier() > 0, "durationMultiplier must be > 0");
            if (spec.linkedStatusId() != null && !spec.linkedStatusId().isBlank()) {
                validateId(spec.linkedStatusId(), "linkedStatusId");
            }
        }
    }

    private static void validateEnchants(List<EnchantSpec> enchants) {
        require(enchants != null && !enchants.isEmpty(), "enchants must not be empty");
        Set<String> ids = new HashSet<>();
        for (EnchantSpec spec : enchants) {
            require(spec != null, "enchant must not be null");
            validateId(spec.enchantId(), "enchantId");
            require(ids.add(spec.enchantId()), "duplicate enchantId: " + spec.enchantId());
            require(spec.category() != null, "enchant category missing");
            require(spec.minTier() >= 0 && spec.maxTier() <= 4 && spec.minTier() <= spec.maxTier(), "enchant tier window invalid");
            require(spec.potency() > 0, "potency must be > 0");
        }
    }

    private static void validateStatuses(List<StatusEffectSpec> statuses) {
        require(statuses != null && !statuses.isEmpty(), "statuses must not be empty");
        Set<String> ids = new HashSet<>();
        for (StatusEffectSpec spec : statuses) {
            require(spec != null, "status must not be null");
            validateId(spec.statusId(), "statusId");
            require(ids.add(spec.statusId()), "duplicate statusId: " + spec.statusId());
            require(spec.type() != null, "status type missing");
            require(spec.baseDurationSeconds() > 0, "baseDurationSeconds must be > 0");
            require(spec.maxStacks() > 0, "maxStacks must be > 0");
            require(spec.stackRule() != null, "stackRule missing");
            require(spec.minTier() >= 0 && spec.maxTier() <= 4 && spec.minTier() <= spec.maxTier(), "status tier window invalid");
        }
    }

    private static void validateId(String value, String field) {
        require(value != null && !value.isBlank(), field + " must not be empty");
        require(ID_PATTERN.matcher(value).matches(), field + " must be namespace:path");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
