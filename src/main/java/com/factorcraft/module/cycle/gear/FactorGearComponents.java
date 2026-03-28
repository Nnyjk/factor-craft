package com.factorcraft.module.cycle.gear;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 装备 Data Components
 * 
 * 定义量子工具和 Factor 盔甲使用的自定义 Data Component 类型
 */
public class FactorGearComponents {
    
    /**
     * 范围模式 - 用于量子工具 (3 或 5)
     */
    public static final ComponentType<Integer> RANGE_MODE = ComponentType.<Integer>builder()
            .codec(Codec.INT)
            .build();
    
    /**
     * Factor 充能 - 存储 DenseFactor 充能量 (mB)
     */
    public static final ComponentType<Integer> FACTOR_CHARGE = ComponentType.<Integer>builder()
            .codec(Codec.INT)
            .build();
    
    /**
     * 套装效果激活状态 - 用于 Factor 盔甲
     */
    public static final ComponentType<Boolean> ARMOR_SET_ACTIVE = ComponentType.<Boolean>builder()
            .codec(Codec.BOOL)
            .build();
    
    /**
     * 注册所有 Data Components
     */
    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, 
                       Identifier.of("factorcraft", "range_mode"), RANGE_MODE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, 
                       Identifier.of("factorcraft", "factor_charge"), FACTOR_CHARGE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, 
                       Identifier.of("factorcraft", "armor_set_active"), ARMOR_SET_ACTIVE);
    }
}
