package com.factorcraft.module.factor.inventory;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.factor.Factor;
import com.factorcraft.factor.FactorRarity;
import com.factorcraft.factor.FactorType;
import com.factorcraft.module.loot.FactorShardItem;
import com.factorcraft.module.loot.ResonanceCoreItem;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/**
 * Factor 拾取处理器
 * 
 * 处理玩家拾取 Factor 相关物品的逻辑：
 * - 拾取 FactorShardItem 时自动存入 Factor 背包
 * - 拾取 ResonanceCoreItem 时提取 Factor
 * - 支持时运附魔增加掉落
 */
public class FactorPickupHandler {
    
    private FactorPickupHandler() {}
    
    /**
     * 注册拾取事件处理器
     */
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Inventory] 注册 Factor 拾取处理器");
        // 物品拾取事件通过 Mixin 或其他方式处理
        // 这里只提供静态方法供外部调用
    }
    
    /**
     * 处理物品拾取
     * 由 ItemEntity 或事件处理器调用
     * 
     * @param player 拾取的玩家
     * @param itemEntity 被拾取的物品实体
     * @param stack 物品堆
     * @return 是否消费了物品（true = 物品被完全处理，不应进入玩家背包）
     */
    public static boolean handlePickup(PlayerEntity player, ItemEntity itemEntity, ItemStack stack) {
        if (player.getWorld().isClient()) {
            return false;
        }
        
        // 检查是否是 Factor 相关物品
        if (stack.getItem() instanceof FactorShardItem shardItem) {
            return handleFactorShardPickup(player, shardItem, stack);
        }
        
        if (stack.getItem() instanceof ResonanceCoreItem) {
            return handleResonanceCorePickup(player, stack);
        }
        
        return false;
    }
    
    /**
     * 处理 FactorShard 拾取
     * 
     * @param player 玩家
     * @param shardItem 碎片物品
     * @param stack 物品堆
     * @return 是否消费了物品
     */
    private static boolean handleFactorShardPickup(PlayerEntity player, FactorShardItem shardItem, ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }
        
        int tier = shardItem.getTier();
        int count = stack.getCount();
        
        // 获取玩家的 Factor 背包
        ServerWorld world = serverPlayer.getServerWorld();
        PlayerFactorInventory inventory = PlayerFactorDataStorage.getPlayerFactorData(world, serverPlayer);
        
        if (inventory.isFull()) {
            // 背包已满，不消费，让物品进入普通背包
            return false;
        }
        
        // 将碎片转换为 Factor 并添加到背包
        int added = 0;
        for (int i = 0; i < count && !inventory.isFull(); i++) {
            Factor factor = createFactorFromShard(tier, player.getRandom());
            if (factor != null && inventory.addFactor(factor)) {
                added++;
            }
        }
        
        if (added > 0) {
            // 标记数据已修改
            PlayerFactorDataStorage.get(world).markModified();
            
            // 播放拾取音效
            player.getWorld().playSound(
                null, 
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS,
                0.5f, 
                1.0f + (player.getRandom().nextFloat() - 0.5f) * 0.2f
            );
            
            // 发送提示消息
            serverPlayer.sendMessage(
                Text.translatable("factorcraft.pickup.factor_shard", added, tier),
                true
            );
            
            // 消费已处理的物品
            stack.decrement(added);
            return stack.isEmpty();
        }
        
        return false;
    }
    
    /**
     * 处理 ResonanceCore 拾取
     * 
     * @param player 玩家
     * @param stack 物品堆
     * @return 是否消费了物品
     */
    private static boolean handleResonanceCorePickup(PlayerEntity player, ItemStack stack) {
        // ResonanceCoreItem 提取为 Factor
        // 这里暂时不处理，让物品进入普通背包
        return false;
    }
    
    /**
     * 从碎片创建 Factor
     * 
     * @param tier 碎片等级 (1-5)
     * @param random 随机源
     * @return 创建的 Factor，如果创建失败返回 null
     */
    private static Factor createFactorFromShard(int tier, Random random) {
        // 随机选择 Factor 类型
        FactorType[] types = FactorType.values();
        FactorType type = types[random.nextInt(types.length)];
        
        // 根据 tier 决定稀有度
        FactorRarity rarity = getRarityForTier(tier, random);
        
        // 根据 tier 决定等级范围
        int minLevel = (tier - 1) * 20 + 1;
        int maxLevel = tier * 20;
        int level = minLevel + random.nextInt(maxLevel - minLevel + 1);
        
        // 基础威力随 tier 增加
        double basePower = 10.0 * tier * (1.0 + random.nextDouble() * 0.5);
        
        // 创建唯一 ID
        Identifier factorId = Identifier.of("factorcraft", 
            String.format("shard_%s_%d_%d", type.asString().toLowerCase(), tier, System.nanoTime()));
        
        // 创建 Factor 名称
        String name = String.format("%s 碎片 Factor T%d", type.getDisplayName(), tier);
        
        try {
            return new Factor.Builder(factorId, name)
                .type(type)
                .rarity(rarity)
                .level(level)
                .tier(tier)
                .basePower(basePower)
                .build();
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Inventory] 创建 Factor 失败", e);
            return null;
        }
    }
    
    /**
     * 根据 tier 计算稀有度
     * 
     * @param tier 碎片等级
     * @param random 随机源
     * @return 稀有度
     */
    private static FactorRarity getRarityForTier(int tier, Random random) {
        double roll = random.nextDouble();
        
        // T1: 90% 普通, 10% 稀有
        // T2: 70% 普通, 25% 稀有, 5% 史诗
        // T3: 50% 普通, 35% 稀有, 13% 史诗, 2% 传说
        // T4: 30% 普通, 40% 稀有, 25% 史诗, 5% 传说
        // T5: 10% 普通, 40% 稀有, 35% 史诗, 15% 传说
        
        return switch (tier) {
            case 1 -> roll < 0.90 ? FactorRarity.COMMON : FactorRarity.UNCOMMON;
            case 2 -> roll < 0.70 ? FactorRarity.COMMON : 
                     roll < 0.95 ? FactorRarity.UNCOMMON : FactorRarity.RARE;
            case 3 -> roll < 0.50 ? FactorRarity.COMMON :
                     roll < 0.85 ? FactorRarity.UNCOMMON :
                     roll < 0.98 ? FactorRarity.RARE : FactorRarity.EPIC;
            case 4 -> roll < 0.30 ? FactorRarity.COMMON :
                     roll < 0.70 ? FactorRarity.UNCOMMON :
                     roll < 0.95 ? FactorRarity.RARE : FactorRarity.EPIC;
            case 5 -> roll < 0.10 ? FactorRarity.COMMON :
                     roll < 0.50 ? FactorRarity.UNCOMMON :
                     roll < 0.85 ? FactorRarity.RARE : FactorRarity.LEGENDARY;
            default -> FactorRarity.COMMON;
        };
    }
}