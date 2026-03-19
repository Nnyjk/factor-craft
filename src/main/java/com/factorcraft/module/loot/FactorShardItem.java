package com.factorcraft.module.loot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 因子碎片 - 基础掉落物 (T1-T5)
 */
public class FactorShardItem extends Item {
    
    private final int tier;
    private static final Map<Integer, FactorShardItem> SHARDS = new HashMap<>();
    
    public FactorShardItem(int tier) {
        super(new Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", "factor_shard_t" + tier)))
            .maxCount(64));
        this.tier = tier;
    }
    
    public int getTier() {
        return tier;
    }
    
    /**
     * 创建指定等级和数量的碎片
     */
    public static ItemStack createShard(int tier, int count) {
        FactorShardItem shard = SHARDS.get(tier);
        if (shard == null) {
            tier = Math.max(1, Math.min(5, tier)); // clamp to 1-5
            shard = SHARDS.get(tier);
        }
        return new ItemStack(shard, count);
    }
    
    /**
     * 获取指定等级的碎片物品
     */
    public static Item getShardItem(int tier) {
        tier = Math.max(1, Math.min(5, tier));
        return SHARDS.get(tier);
    }
    
    /**
     * 注册 T1-T5 因子碎片
     */
    public static void registerAll() {
        for (int tier = 1; tier <= 5; tier++) {
            String name = "factor_shard_t" + tier;
            Identifier id = Identifier.of("factorcraft", name);
            FactorShardItem shard = new FactorShardItem(tier);
            Registry.register(Registries.ITEM, id, shard);
            SHARDS.put(tier, shard);
        }
    }
}