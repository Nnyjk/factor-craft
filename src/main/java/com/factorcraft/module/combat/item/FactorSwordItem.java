package com.factorcraft.module.combat.item;

import com.factorcraft.api.CombatApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 剑 - 基础 Factor 武器 (简化版)
 */
public class FactorSwordItem extends Item implements CombatApi.FactorWeapon {
    
    private final int tier;
    private final double factorDamageBonus;
    
    public FactorSwordItem(int tier, double factorDamageBonus) {
        super(new Item.Settings()
            .maxCount(1)
            .maxDamage(1000 + tier * 500)
        );
        this.tier = tier;
        this.factorDamageBonus = factorDamageBonus;
    }
    
    /**
     * 创建 T1-T5 所有等级的 Factor 剑
     */
    public static void registerAll() {
        register("factor_sword_t1", new FactorSwordItem(1, 0.2));
        register("factor_sword_t2", new FactorSwordItem(2, 0.4));
        register("factor_sword_t3", new FactorSwordItem(3, 0.6));
        register("factor_sword_t4", new FactorSwordItem(4, 0.8));
        register("factor_sword_t5", new FactorSwordItem(5, 1.0));
    }
    
    private static void register(String name, FactorSwordItem sword) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), sword);
    }
    
    @Override
    public double getFactorDamageBonus(ItemStack stack) {
        return factorDamageBonus;
    }
    
    @Override
    public int getDimensionPenetration(ItemStack stack) {
        return tier >= 3 ? tier - 2 : 0;
    }
}
