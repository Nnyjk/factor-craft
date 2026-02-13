package com.factorcraft.module.material.config;

import com.factorcraft.module.material.validation.MaterialsM2SpecValidator;
import com.factorcraft.module.shared.ModuleLoggers;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * 从 DynamicBundle.materialsM2 动态内容解析 M2 配置。
 */
public final class MaterialsM2ConfigParser {
    private static final org.slf4j.Logger LOG = ModuleLoggers.forModule("materials_traits_enchants_buffs");
    private static final Gson GSON = new Gson();

    private MaterialsM2ConfigParser() {}

    public static MaterialsM2Config parseOrDefault(Map<String, Object> dynamicSpec, MaterialsM2Config fallback) {
        if (dynamicSpec == null || dynamicSpec.isEmpty() || !hasCoreSections(dynamicSpec)) {
            return fallback;
        }

        try {
            MaterialsM2Config parsed = GSON.fromJson(GSON.toJson(dynamicSpec), MaterialsM2Config.class);
            MaterialsM2SpecValidator.validate(parsed);
            return parsed;
        } catch (Exception ex) {
            LOG.warn("M2 动态配置解析失败，回退默认池: {}", ex.getMessage());
            return fallback;
        }
    }

    private static boolean hasCoreSections(Map<String, Object> dynamicSpec) {
        return isNonEmptyList(dynamicSpec.get("materials"))
                && isNonEmptyList(dynamicSpec.get("traits"))
                && isNonEmptyList(dynamicSpec.get("enchants"))
                && isNonEmptyList(dynamicSpec.get("statuses"));
    }

    private static boolean isNonEmptyList(Object value) {
        return value instanceof List<?> list && !list.isEmpty();
    }
}
