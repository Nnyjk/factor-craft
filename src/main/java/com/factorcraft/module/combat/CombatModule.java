package com.factorcraft.module.combat;

import com.factorcraft.module.combat.entity.InfectedEntities;
import com.factorcraft.module.combat.entity.InfectedZombieEntity;
import com.factorcraft.module.combat.entity.InfectedSkeletonEntity;
import com.factorcraft.module.combat.entity.InfectedCreeperEntity;
import com.factorcraft.module.combat.entity.InfectedSlimeEntity;
import com.factorcraft.module.combat.boss.BossEntities;
import com.factorcraft.module.combat.boss.FactorSynthesizerEntity;
import com.factorcraft.module.combat.boss.FactorVoidEntity;
import com.factorcraft.module.combat.boss.FactorBlazingLordEntity;
import com.factorcraft.module.combat.defense.DefenseTowers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

/**
 * 战斗系统模块
 * 包含感染生物、Boss、防御塔等战斗内容
 */
public class CombatModule implements ModInitializer {
    
    public static final String MOD_ID = "combat";
    
    @Override
    public void onInitialize() {
        // 注册感染生物
        InfectedEntities.register();
        
        // 注册 Boss
        BossEntities.register();
        
        // 注册防御塔
        DefenseTowers.register();
        
        // 注册实体属性
        registerEntityAttributes();
    }
    
    private void registerEntityAttributes() {
        // 感染生物
        FabricDefaultAttributeRegistry.register(InfectedEntities.INFECTED_ZOMBIE, 
            InfectedZombieEntity.createInfectedZombieAttributes());
        FabricDefaultAttributeRegistry.register(InfectedEntities.INFECTED_SKELETON, 
            InfectedSkeletonEntity.createInfectedSkeletonAttributes());
        FabricDefaultAttributeRegistry.register(InfectedEntities.INFECTED_CREEPER, 
            InfectedCreeperEntity.createInfectedCreeperAttributes());
        FabricDefaultAttributeRegistry.register(InfectedEntities.INFECTED_SLIME, 
            InfectedSlimeEntity.createInfectedSlimeAttributes());
        
        // Boss
        FabricDefaultAttributeRegistry.register(BossEntities.FACTOR_SYNTHESIZER, 
            FactorSynthesizerEntity.createSynthesizerAttributes());
        FabricDefaultAttributeRegistry.register(BossEntities.FACTOR_VOID, 
            FactorVoidEntity.createVoidAttributes());
        FabricDefaultAttributeRegistry.register(BossEntities.FACTOR_BLAZING_LORD, 
            FactorBlazingLordEntity.createBlazingLordAttributes());
    }
}
