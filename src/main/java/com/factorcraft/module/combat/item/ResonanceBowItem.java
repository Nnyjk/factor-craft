package com.factorcraft.module.combat.item;

import com.factorcraft.api.CombatApi;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 共振弓 - Factor 能量远程武器 (简化版)
 */
public class ResonanceBowItem extends BowItem implements CombatApi.FactorWeapon {
    
    private final int tier;
    
    public ResonanceBowItem(int tier) {
        super(new net.minecraft.item.Item.Settings()
            .maxDamage(1000 + tier * 500)
            .maxCount(1)
        );
        this.tier = tier;
    }
    
    /**
     * 创建 T1-T5 所有等级的共振弓
     */
    public static void registerAll() {
        register("resonance_bow_t1", new ResonanceBowItem(1));
        register("resonance_bow_t2", new ResonanceBowItem(2));
        register("resonance_bow_t3", new ResonanceBowItem(3));
        register("resonance_bow_t4", new ResonanceBowItem(4));
        register("resonance_bow_t5", new ResonanceBowItem(5));
    }
    
    private static void register(String name, ResonanceBowItem bow) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), bow);
    }
    
    @Override
    public double getFactorDamageBonus(ItemStack stack) {
        return 0.15 * tier;
    }
    
    @Override
    public int getDimensionPenetration(ItemStack stack) {
        return tier >= 3 ? 1 : 0;
    }
}
