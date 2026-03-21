package com.factorcraft.factor;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Formatting;

/**
 * Factor 稀有度枚举
 * 
 * 定义 Factor 的稀有度等级，影响颜色、掉落概率等
 */
public enum FactorRarity implements StringIdentifiable {
    
    COMMON("common", Formatting.WHITE, 1.0, 0),
    UNCOMMON("uncommon", Formatting.YELLOW, 0.5, 1),
    RARE("rare", Formatting.AQUA, 0.2, 2),
    EPIC("epic", Formatting.LIGHT_PURPLE, 0.05, 3),
    LEGENDARY("legendary", Formatting.GOLD, 0.01, 4);
    
    private final String name;
    private final Formatting formatting;
    private final double dropChance;
    private final int tier;
    
    FactorRarity(String name, Formatting formatting, double dropChance, int tier) {
        this.name = name;
        this.formatting = formatting;
        this.dropChance = dropChance;
        this.tier = tier;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    /**
     * 获取稀有度对应的格式化颜色
     */
    public Formatting getFormatting() {
        return this.formatting;
    }
    
    /**
     * 获取基础掉落概率
     */
    public double getDropChance() {
        return this.dropChance;
    }
    
    /**
     * 获取稀有度层级（用于比较）
     */
    public int getTier() {
        return this.tier;
    }
    
    /**
     * 根据名称获取稀有度
     */
    public static FactorRarity fromName(String name) {
        for (FactorRarity rarity : values()) {
            if (rarity.name.equals(name)) {
                return rarity;
            }
        }
        return COMMON;
    }
    
    /**
     * 根据随机值选择稀有度
     * @param random 0.0-1.0 的随机值
     */
    public static FactorRarity fromRandom(double random) {
        double cumulative = 0.0;
        // 从高到低检查
        FactorRarity[] rarities = {LEGENDARY, EPIC, RARE, UNCOMMON, COMMON};
        for (FactorRarity rarity : rarities) {
            cumulative += rarity.dropChance;
            if (random <= cumulative) {
                return rarity;
            }
        }
        return COMMON;
    }
}