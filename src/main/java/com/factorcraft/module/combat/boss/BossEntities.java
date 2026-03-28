package com.factorcraft.module.combat.boss;

import com.factorcraft.module.combat.CombatModule;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Boss 实体注册表
 */
public class BossEntities {
    
    public static final EntityType<FactorSynthesizerEntity> FACTOR_SYNTHESIZER;
    public static final EntityType<FactorVoidEntity> FACTOR_VOID;
    public static final EntityType<FactorBlazingLordEntity> FACTOR_BLAZING_LORD;
    
    static {
        RegistryKey<EntityType<?>> synthesizerKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "factor_synthesizer"));
        FACTOR_SYNTHESIZER = Registry.register(Registries.ENTITY_TYPE, synthesizerKey, 
            EntityType.Builder.create(FactorSynthesizerEntity::new, SpawnGroup.MONSTER)
                .dimensions(1.2F, 3.0F)
                .eyeHeight(2.5F)
                .maxTrackingRange(16)
                .build(synthesizerKey));
        
        RegistryKey<EntityType<?>> voidKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "factor_void"));
        FACTOR_VOID = Registry.register(Registries.ENTITY_TYPE, voidKey, 
            EntityType.Builder.create(FactorVoidEntity::new, SpawnGroup.MONSTER)
                .dimensions(1.0F, 2.8F)
                .eyeHeight(2.3F)
                .maxTrackingRange(16)
                .build(voidKey));
        
        RegistryKey<EntityType<?>> blazingLordKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "factor_blazing_lord"));
        FACTOR_BLAZING_LORD = Registry.register(Registries.ENTITY_TYPE, blazingLordKey, 
            EntityType.Builder.create(FactorBlazingLordEntity::new, SpawnGroup.MONSTER)
                .dimensions(1.3F, 3.2F)
                .eyeHeight(2.7F)
                .maxTrackingRange(16)
                .build(blazingLordKey));
    }
    
    public static void register() {
        // 静态初始化时自动注册
    }
}
