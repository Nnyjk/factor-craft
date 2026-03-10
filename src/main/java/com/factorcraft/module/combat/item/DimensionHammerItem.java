package com.factorcraft.module.combat.item;

import com.factorcraft.api.CombatApi;
import com.factorcraft.module.combat.ModToolMaterial;
import com.factorcraft.module.combat.WeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 维度锤 - 重型破甲武器
 * 
 * 特点：高伤害，慢速攻击，高破甲，维度穿透
 */
public class DimensionHammerItem extends SwordItem implements CombatApi.FactorWeapon {
    
    private final int tier;
    private final float damage;
    private final float attackSpeed;
    private final float armorPierce;
    private final int enchantability;
    
    public DimensionHammerItem(int tier) {
        super(
            getToolMaterial(tier),
            WeaponAttributes.Hammer.ATTACK_SPEED[tier - 1],
            WeaponAttributes.Hammer.DAMAGE[tier - 1],
            new Item.Settings()
                .maxCount(1)
                .maxDamage(ModToolMaterial.getDurability(tier))
        );
        this.tier = tier;
        this.damage = WeaponAttributes.Hammer.DAMAGE[tier - 1];
        this.attackSpeed = WeaponAttributes.Hammer.ATTACK_SPEED[tier - 1];
        this.armorPierce = WeaponAttributes.Hammer.ARMOR_PIERCE[tier - 1];
        this.enchantability = ModToolMaterial.getEnchantability(tier);
    }
    
    private static ToolMaterial getToolMaterial(int tier) {
        return switch (tier) {
            case 1 -> ModToolMaterial.T1_STONE;
            case 2 -> ModToolMaterial.T2_IRON;
            case 3 -> ModToolMaterial.T3_GOLD;
            case 4 -> ModToolMaterial.T4_NETHERITE;
            case 5 -> ModToolMaterial.T5_FACTOR;
            default -> ModToolMaterial.T1_STONE;
        };
    }
    
    /**
     * 创建 T1-T5 所有等级的维度锤
     */
    public static void registerAll() {
        for (int tier = 1; tier <= 5; tier++) {
            String name = "dimension_hammer_t" + tier;
            register(name, new DimensionHammerItem(tier));
        }
    }
    
    private static void register(String name, DimensionHammerItem hammer) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), hammer);
    }
    
    @Override
    public double getFactorDamageBonus(ItemStack stack) {
        return WeaponAttributes.Hammer.FACTOR_BONUS[tier - 1];
    }
    
    @Override
    public int getDimensionPenetration(ItemStack stack) {
        return WeaponAttributes.Hammer.DIMENSION_PENETRATION[tier - 1];
    }
    
    /**
     * 获取武器伤害
     */
    public float getDamage() {
        return damage;
    }
    
    /**
     * 获取攻击速度
     */
    public float getAttackSpeed() {
        return attackSpeed;
    }
    
    /**
     * 获取破甲比例
     */
    public float getArmorPierce() {
        return armorPierce;
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
        return WeaponAttributes.getWeaponName("hammer", tier);
    }
}
