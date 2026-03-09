package com.factorcraft.module.combat.item;

import com.factorcraft.api.CombatApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 维度锤 - 重型破甲武器 (简化版)
 */
public class DimensionHammerItem extends Item implements CombatApi.FactorWeapon {
    
    private final int tier;
    
    public DimensionHammerItem(int tier) {
        super(new Item.Settings()
            .maxDamage(1000 + tier * 500)
            .maxCount(1)
        );
        this.tier = tier;
    }
    
    /**
     * 创建 T1-T5 所有等级的维度锤
     */
    public static void registerAll() {
        register("dimension_hammer_t1", new DimensionHammerItem(1));
        register("dimension_hammer_t2", new DimensionHammerItem(2));
        register("dimension_hammer_t3", new DimensionHammerItem(3));
        register("dimension_hammer_t4", new DimensionHammerItem(4));
        register("dimension_hammer_t5", new DimensionHammerItem(5));
    }
    
    private static void register(String name, DimensionHammerItem hammer) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), hammer);
    }
    
    @Override
    public double getFactorDamageBonus(ItemStack stack) {
        return 0.1 * tier;
    }
    
    @Override
    public int getDimensionPenetration(ItemStack stack) {
        return tier >= 2 ? tier - 1 : 0;
    }
    
    /**
     * 获取破甲比例
     */
    public float getArmorPenetration() {
        return 0.2f + (tier * 0.1f);
    }
}
