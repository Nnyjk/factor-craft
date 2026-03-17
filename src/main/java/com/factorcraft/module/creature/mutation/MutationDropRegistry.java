package com.factorcraft.module.creature.mutation;

import com.factorcraft.FactorCraftMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 变异生物掉落注册表
 * 
 * 管理变异生物的特殊掉落物品
 */
public class MutationDropRegistry {
    
    /** 掉落表：变异 ID -> 掉落物品列表 */
    private static final Map<Identifier, List<MutationDropEntry>> DROPS = new HashMap<>();
    
    /**
     * 注册变异掉落
     */
    public static void register(String mutationId, Item item, double chance, int minCount, int maxCount) {
        Identifier key = Identifier.of(FactorCraftMod.MOD_ID, mutationId);
        DROPS.computeIfAbsent(key, k -> new ArrayList<>())
            .add(new MutationDropEntry(item, chance, minCount, maxCount));
        
        FactorCraftMod.LOGGER.debug("Registered drop for mutation {}: {} ({}x{}, chance={})", 
            mutationId, Registries.ITEM.getId(item), minCount, maxCount, chance);
    }
    
    /**
     * 获取变异掉落列表
     */
    public static List<MutationDropEntry> getDrops(Identifier mutationId) {
        return DROPS.getOrDefault(mutationId, Collections.emptyList());
    }
    
    /**
     * 获取所有掉落
     */
    public static Map<Identifier, List<MutationDropEntry>> getAllDrops() {
        return Collections.unmodifiableMap(DROPS);
    }
    
    /**
     * 初始化内置变异掉落
     */
    public static void init() {
        // 常见变异掉落
        register("swiftness", net.minecraft.item.Items.SUGAR, 0.5, 1, 3);
        register("swiftness", net.minecraft.item.Items.FEATHER, 0.3, 1, 2);
        
        register("strength", net.minecraft.item.Items.BLAZE_POWDER, 0.5, 1, 2);
        register("strength", net.minecraft.item.Items.GHAST_TEAR, 0.2, 1, 1);
        
        register("toughness", net.minecraft.item.Items.ARMADILLO_SCUTE, 0.5, 1, 2);
        register("toughness", net.minecraft.item.Items.TURTLE_HELMET, 0.1, 1, 1);
        
        // 稀有变异掉落
        register("fire_infused", net.minecraft.item.Items.BLAZE_ROD, 0.75, 1, 3);
        register("fire_infused", net.minecraft.item.Items.FIRE_CHARGE, 0.5, 1, 2);
        
        register("void_touched", net.minecraft.item.Items.ENDER_PEARL, 0.75, 1, 2);
        register("void_touched", net.minecraft.item.Items.SHULKER_SHELL, 0.3, 1, 1);
        
        register("nature_blessed", net.minecraft.item.Items.GLOW_BERRIES, 0.75, 2, 5);
        register("nature_blessed", net.minecraft.item.Items.SWEET_BERRIES, 0.5, 2, 4);
        
        register("overcharged", net.minecraft.item.Items.REDSTONE, 1.0, 2, 6);
        register("overcharged", net.minecraft.item.Items.GLOWSTONE_DUST, 0.5, 1, 3);
        
        FactorCraftMod.LOGGER.info("Registered mutation drops for {} mutations", DROPS.size());
    }
    
    /**
     * 掉落条目
     */
    public record MutationDropEntry(
        Item item,
        double chance,
        int minCount,
        int maxCount
    ) {
        /**
         * 生成掉落堆
         */
        @Nullable
        public ItemStack generateDrop(net.minecraft.util.math.random.Random random) {
            if (random.nextDouble() > chance) {
                return null;
            }
            
            int count = minCount + random.nextInt(maxCount - minCount + 1);
            return new ItemStack(item, count);
        }
    }
}
