package com.factorcraft.module.loot;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 因子碎片 - 基础掉落物
 */
public class FactorShardItem extends Item {
    
    private final int tier;
    
    public FactorShardItem(int tier) {
        super(new Settings().maxCount(64));
        this.tier = tier;
    }
    
    /**
     * 注册 T1-T5 因子碎片
     */
    public static void registerAll() {
        register("factor_shard_t1", new FactorShardItem(1));
        register("factor_shard_t2", new FactorShardItem(2));
        register("factor_shard_t3", new FactorShardItem(3));
        register("factor_shard_t4", new FactorShardItem(4));
        register("factor_shard_t5", new FactorShardItem(5));
    }
    
    private static void register(String name, FactorShardItem item) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), item);
    }
    
    public int getTier() {
        return tier;
    }
}
