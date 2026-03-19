package com.factorcraft.module.loot.handler;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.loot.FactorShardItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.random.Random;

/**
 * 实体掉落处理器 - 处理 Factor 碎片掉落
 */
public final class EntityDropHandler {
    
    private static final double BASE_DROP_CHANCE = 0.05;
    private static final double HIGH_CONCENTRATION_BONUS = 0.15;
    private static final double HIGH_CONCENTRATION_THRESHOLD = 30.0;
    
    private EntityDropHandler() {}
    
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 注册实体掉落处理器");
        
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (world.isClient()) return;
            handleEntityDeath(world, entity, killedEntity);
        });
    }
    
    private static void handleEntityDeath(ServerWorld world, Entity killer, LivingEntity killed) {
        if (!(killer instanceof ServerPlayerEntity player)) return;
        if (!(killed instanceof MobEntity)) return;
        
        BlockPos pos = killed.getBlockPos();
        Random random = world.getRandom();
        
        // 使用世界级 Factor（简化版）
        double concentration = FactorService.getInstance().getFactor(world);
        double dropChance = calculateDropChance(concentration);
        
        if (random.nextDouble() < dropChance) {
            int tier = calculateShardTier(concentration, random);
            ItemStack shard = FactorShardItem.createShard(tier, 1 + random.nextInt(3));
            killed.dropStack(world, shard);
        }
    }
    
    private static double calculateDropChance(double concentration) {
        double chance = BASE_DROP_CHANCE;
        if (concentration >= HIGH_CONCENTRATION_THRESHOLD) {
            chance += HIGH_CONCENTRATION_BONUS;
        }
        return chance;
    }
    
    private static int calculateShardTier(double concentration, Random random) {
        if (concentration >= 80) return 4 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 60) return 3 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 40) return 2 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 20) return 1 + (random.nextDouble() < 0.3 ? 1 : 0);
        return 1;
    }
}