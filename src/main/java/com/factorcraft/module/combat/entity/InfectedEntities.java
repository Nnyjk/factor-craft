package com.factorcraft.module.combat.entity;

import com.factorcraft.module.combat.CombatModule;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 感染生物注册表
 */
public class InfectedEntities {
    
    public static final EntityType<InfectedZombieEntity> INFECTED_ZOMBIE;
    public static final EntityType<InfectedSkeletonEntity> INFECTED_SKELETON;
    public static final EntityType<InfectedCreeperEntity> INFECTED_CREEPER;
    public static final EntityType<InfectedSlimeEntity> INFECTED_SLIME;
    
    static {
        RegistryKey<EntityType<?>> zombieKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "infected_zombie"));
        INFECTED_ZOMBIE = Registry.register(Registries.ENTITY_TYPE, zombieKey, 
            EntityType.Builder.create(InfectedZombieEntity::new, SpawnGroup.MONSTER)
                .dimensions(0.6F, 1.95F)
                .eyeHeight(1.74F)
                .maxTrackingRange(8)
                .build(zombieKey));
        
        RegistryKey<EntityType<?>> skeletonKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "infected_skeleton"));
        INFECTED_SKELETON = Registry.register(Registries.ENTITY_TYPE, skeletonKey, 
            EntityType.Builder.create(InfectedSkeletonEntity::new, SpawnGroup.MONSTER)
                .dimensions(0.6F, 1.99F)
                .eyeHeight(1.54F)
                .maxTrackingRange(8)
                .build(skeletonKey));
        
        RegistryKey<EntityType<?>> creeperKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "infected_creeper"));
        INFECTED_CREEPER = Registry.register(Registries.ENTITY_TYPE, creeperKey, 
            EntityType.Builder.create(InfectedCreeperEntity::new, SpawnGroup.MONSTER)
                .dimensions(0.6F, 1.7F)
                .eyeHeight(1.7F)
                .maxTrackingRange(8)
                .build(creeperKey));
        
        RegistryKey<EntityType<?>> slimeKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, 
            Identifier.of(CombatModule.MOD_ID, "infected_slime"));
        INFECTED_SLIME = Registry.register(Registries.ENTITY_TYPE, slimeKey, 
            EntityType.Builder.create(InfectedSlimeEntity::new, SpawnGroup.MONSTER)
                .dimensions(0.51000005F, 0.51000005F)
                .eyeHeight(0.255F)
                .maxTrackingRange(8)
                .build(slimeKey));
    }
    
    public static void register() {
        // 静态初始化时自动注册
    }
}
