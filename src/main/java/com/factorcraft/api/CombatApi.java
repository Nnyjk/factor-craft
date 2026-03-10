package com.factorcraft.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * 战斗系统公共 API
 * 
 * 提供给第三方 Mod 扩展武器和怪物
 * 
 * @since 0.1.0
 */
public interface CombatApi {
    
    /**
     * 注册武器类型
     * 
     * @param type 武器类型
     */
    void registerWeaponType(WeaponType type);
    
    /**
     * 注册武器物品
     * 
     * @param weapon 武器物品
     */
    void registerWeapon(Item weapon);
    
    /**
     * 获取所有已注册的武器
     * 
     * @return 武器集合
     */
    java.util.Collection<Item> getWeapons();
    
    /**
     * 武器类型
     */
    record WeaponType(
        String id,
        String name,
        float baseDamage,
        float attackSpeed,
        WeaponCategory category
    ) {}
    
    /**
     * 武器分类
     */
    enum WeaponCategory {
        SWORD,      // 剑
        HAMMER,     // 锤
        BOW,        // 弓
        BLADE,      // 刃
        STAFF       // 法杖
    }
    
    /**
     * 武器接口 - 所有 Factor 武器应实现此接口
     */
    interface FactorWeapon {
        /**
         * 获取 Factor 伤害加成
         * 
         * @param stack 武器物品栈
         * @return Factor 伤害加成 (0-100%)
         */
        double getFactorDamageBonus(ItemStack stack);
        
        /**
         * 获取维度穿透等级
         * 
         * @param stack 武器物品栈
         * @return 穿透等级 (0-5)
         */
        int getDimensionPenetration(ItemStack stack);
    }
}
