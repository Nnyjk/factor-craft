package com.factorcraft.module.combat.item;

import com.factorcraft.api.CombatApi;
import com.factorcraft.module.combat.ModToolMaterial;
import com.factorcraft.module.combat.WeaponAttributes;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 共振弓 - Factor 能量远程武器
 * 
 * 特点：中等伤害，远程攻击，蓄力射击，穿透能力
 */
public class ResonanceBowItem extends BowItem implements CombatApi.FactorWeapon {
    
    private final int tier;
    private final float damage;
    private final int drawTime;
    private final float rangeBonus;
    private final int pierceLevel;
    private final int enchantability;
    
    public ResonanceBowItem(int tier) {
        super(new Item.Settings()
            .maxCount(1)
            .maxDamage(ModToolMaterial.getDurability(tier))
        );
        this.tier = tier;
        this.damage = WeaponAttributes.Bow.DAMAGE[tier - 1];
        this.drawTime = WeaponAttributes.Bow.DRAW_TIME[tier - 1];
        this.rangeBonus = WeaponAttributes.Bow.RANGE_BONUS[tier - 1];
        this.pierceLevel = WeaponAttributes.Bow.PIERCE_LEVEL[tier - 1];
        this.enchantability = ModToolMaterial.getEnchantability(tier);
    }
    
    /**
     * 创建 T1-T5 所有等级的共振弓
     */
    public static void registerAll() {
        for (int tier = 1; tier <= 5; tier++) {
            String name = "resonance_bow_t" + tier;
            register(name, new ResonanceBowItem(tier));
        }
    }
    
    private static void register(String name, ResonanceBowItem bow) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), bow);
    }
    
    @Override
    public double getFactorDamageBonus(ItemStack stack) {
        return WeaponAttributes.Bow.FACTOR_BONUS[tier - 1];
    }
    
    @Override
    public int getDimensionPenetration(ItemStack stack) {
        return tier >= 3 ? 1 : 0;
    }
    
    /**
     * 获取武器伤害
     */
    public float getDamage() {
        return damage;
    }
    
    /**
     * 获取蓄力时间 (ticks)
     */
    public int getDrawTime() {
        return drawTime;
    }
    
    /**
     * 获取射程加成
     */
    public float getRangeBonus() {
        return rangeBonus;
    }
    
    /**
     * 获取穿透等级
     */
    public int getPierceLevel() {
        return pierceLevel;
    }
    
    /**
     * 获取附魔能力
     */
    public int getEnchantability() {
        return enchantability;
    }
    
    /**
     * 获取武器名称
     */
    public String getWeaponName() {
        return WeaponAttributes.getWeaponName("bow", tier);
    }
}
