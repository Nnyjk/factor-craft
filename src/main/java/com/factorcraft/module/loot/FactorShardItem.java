package com.factorcraft.module.loot;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 因子碎片 - 基础掉落物
 */
public class FactorShardItem extends Item {
    
    private final int tier;
    
    public FactorShardItem(int tier) {
        super(new Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", "factor_shard_t" + tier)))
            .maxCount(64));
        this.tier = tier;
    }
    
    /**
     * 注册 T1-T5 因子碎片
     */
    public static void registerAll() {
        for (int tier = 1; tier <= 5; tier++) {
            String name = "factor_shard_t" + tier;
            Identifier id = Identifier.of("factorcraft", name);
            Registry.register(Registries.ITEM, id, new FactorShardItem(tier));
        }
    }
    
    public int getTier() {
        return tier;
    }
}
