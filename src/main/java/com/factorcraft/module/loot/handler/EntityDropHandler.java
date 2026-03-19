package com.factorcraft.module.loot.handler;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.loot.FactorShardItem;
import com.factorcraft.module.loot.MobDropsConfig;
import com.factorcraft.module.loot.MobDropsConfig.DropConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Map;

/**
 * 实体掉落处理器 - 处理 Factor 相关物品掉落
 * 
 * 功能：
 * 1. 配置化的掉落率和数量
 * 2. Boss 特殊掉落
 * 3. Factor 浓度加成
 */
public final class EntityDropHandler {
    
    private EntityDropHandler() {}
    
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 注册实体掉落处理器");
        
        // 初始化配置
        MobDropsConfig.getInstance();
        
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (world.isClient()) return;
            handleEntityDeath(world, entity, killedEntity);
        });
    }
    
    private static void handleEntityDeath(ServerWorld world, Entity killer, LivingEntity killed) {
        // 检查配置是否启用
        if (!MobDropsConfig.isEnabled()) return;
        
        // 只处理玩家击杀的生物
        if (!(killer instanceof ServerPlayerEntity player)) return;
        
        // 只处理 MobEntity
        if (!(killed instanceof MobEntity)) return;
        
        // 获取生物 ID
        String mobId = getMobId(killed);
        if (mobId == null) return;
        
        // 获取掉落配置
        Map<String, DropConfig> drops = MobDropsConfig.getMobDrops(mobId);
        if (drops.isEmpty()) {
            // 没有配置的生物使用默认掉落
            handleDefaultDrop(world, killed);
            return;
        }
        
        // 计算 Factor 浓度加成
        double concentration = FactorService.getInstance().getFactor(world);
        double concentrationBonus = MobDropsConfig.getConcentrationBonus(concentration);
        double globalMultiplier = MobDropsConfig.getGlobalMultiplier();
        Random random = world.getRandom();
        
        // 处理每个掉落项
        for (Map.Entry<String, DropConfig> entry : drops.entrySet()) {
            String dropId = entry.getKey();
            DropConfig config = entry.getValue();
            
            // 计算最终掉落概率
            double finalChance = config.chance() * globalMultiplier * (1 + concentrationBonus);
            
            if (random.nextDouble() < finalChance) {
                // 随机数量
                int count = config.min() + random.nextInt(config.max() - config.min() + 1);
                
                // 创建物品
                ItemStack dropStack = createDropItem(dropId, config, count, random);
                if (dropStack != null) {
                    killed.dropStack(world, dropStack);
                    FactorCraftMod.LOGGER.debug("[MobDrops] {} 掉落 {} x{}", mobId, dropId, dropStack.getCount());
                }
            }
        }
    }
    
    /**
     * 处理默认掉落（未配置的生物）
     */
    private static void handleDefaultDrop(ServerWorld world, LivingEntity killed) {
        Random random = world.getRandom();
        double concentration = FactorService.getInstance().getFactor(world);
        double concentrationBonus = MobDropsConfig.getConcentrationBonus(concentration);
        
        // 默认 5% 掉落 Factor 碎片
        double baseChance = 0.05 * (1 + concentrationBonus);
        if (random.nextDouble() < baseChance) {
            int tier = calculateDefaultTier(concentration, random);
            ItemStack shard = FactorShardItem.createShard(tier, 1 + random.nextInt(2));
            killed.dropStack(world, shard);
        }
    }
    
    /**
     * 获取生物 ID
     */
    private static String getMobId(LivingEntity entity) {
        Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
        return id != null ? id.toString() : null;
    }
    
    /**
     * 创建掉落物品
     */
    private static ItemStack createDropItem(String dropId, DropConfig config, int count, Random random) {
        // Factor 碎片系列
        if (dropId.startsWith("factor_shard")) {
            int tier = config.tier();
            if (config.tierRange() > 0) {
                tier = Math.max(1, Math.min(5, tier + random.nextInt(config.tierRange() + 1)));
            }
            return FactorShardItem.createShard(tier, count);
        }
        
        // Factor 精华
        if (dropId.equals("factor_essence")) {
            return createFactorEssence(count);
        }
        
        // 其他特殊掉落
        return createSpecialDrop(dropId, count);
    }
    
    /**
     * 创建 Factor 精华
     */
    private static ItemStack createFactorEssence(int count) {
        // 尝试获取已注册的物品
        var item = Registries.ITEM.get(Identifier.of("factorcraft", "factor_essence"));
        if (item != null) {
            return new ItemStack(item, count);
        }
        // 回退到 Factor 碎片 T2
        return FactorShardItem.createShard(2, count);
    }
    
    /**
     * 创建特殊掉落
     */
    private static ItemStack createSpecialDrop(String dropId, int count) {
        // 尝试解析物品 ID
        Identifier itemId = Identifier.tryParse(dropId);
        if (itemId == null) {
            // 尝试添加 factorcraft: 前缀
            itemId = Identifier.tryParse("factorcraft:" + dropId);
        }
        if (itemId == null) {
            // 尝试添加 minecraft: 前缀
            itemId = Identifier.tryParse("minecraft:" + dropId);
        }
        if (itemId == null) {
            return null;
        }
        
        var item = Registries.ITEM.get(itemId);
        if (item == null) {
            FactorCraftMod.LOGGER.debug("[MobDrops] 未知掉落物品: {}", dropId);
            return null;
        }
        
        return new ItemStack(item, count);
    }
    
    /**
     * 计算默认碎片等级
     */
    private static int calculateDefaultTier(double concentration, Random random) {
        if (concentration >= 80) return 4 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 60) return 3 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 40) return 2 + (random.nextDouble() < 0.3 ? 1 : 0);
        if (concentration >= 20) return 1 + (random.nextDouble() < 0.3 ? 1 : 0);
        return 1;
    }
    
    /**
     * 重载配置
     */
    public static void reloadConfig() {
        MobDropsConfig.reload();
        FactorCraftMod.LOGGER.info("[EntityDropHandler] 配置已重载");
    }
}