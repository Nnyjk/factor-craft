package com.factorcraft.module.integration.registry;

import com.factorcraft.module.integration.api.NonCoreRegistrar;
import com.factorcraft.module.integration.model.ArmorSpec;
import com.factorcraft.module.integration.model.FurnitureSpec;
import com.factorcraft.module.integration.model.ToolSpec;
import com.factorcraft.module.integration.model.WeaponSpec;
import com.factorcraft.module.integration.validation.NonCoreSpecValidator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统一注册中心：为扩展包和第三方包提供接入点。
 */
public final class NonCoreIntegrationRegistry implements NonCoreRegistrar {
    private static final NonCoreIntegrationRegistry INSTANCE = new NonCoreIntegrationRegistry();

    private final Map<String, ToolSpec> tools = new LinkedHashMap<>();
    private final Map<String, WeaponSpec> weapons = new LinkedHashMap<>();
    private final Map<String, ArmorSpec> armors = new LinkedHashMap<>();
    private final Map<String, FurnitureSpec> furnitures = new LinkedHashMap<>();

    private NonCoreIntegrationRegistry() {}

    public static NonCoreIntegrationRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void registerTool(ToolSpec spec) {
        NonCoreSpecValidator.validate(spec);
        putIfAbsent(tools, spec.contentId(), spec);
    }

    @Override
    public synchronized void registerWeapon(WeaponSpec spec) {
        NonCoreSpecValidator.validate(spec);
        putIfAbsent(weapons, spec.contentId(), spec);
    }

    @Override
    public synchronized void registerArmor(ArmorSpec spec) {
        NonCoreSpecValidator.validate(spec);
        putIfAbsent(armors, spec.contentId(), spec);
    }

    @Override
    public synchronized void registerFurniture(FurnitureSpec spec) {
        NonCoreSpecValidator.validate(spec);
        putIfAbsent(furnitures, spec.contentId(), spec);
    }

    public Map<String, ToolSpec> toolsView() {
        return Collections.unmodifiableMap(tools);
    }

    public Map<String, WeaponSpec> weaponsView() {
        return Collections.unmodifiableMap(weapons);
    }

    public Map<String, ArmorSpec> armorsView() {
        return Collections.unmodifiableMap(armors);
    }

    public Map<String, FurnitureSpec> furnituresView() {
        return Collections.unmodifiableMap(furnitures);
    }

    private static <T> void putIfAbsent(Map<String, T> map, String key, T value) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("duplicate contentId: " + key);
        }
        map.put(key, value);
    }
}
