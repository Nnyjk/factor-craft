package com.factorcraft.factor;

import net.minecraft.util.StringIdentifiable;

/**
 * Factor 类型枚举
 * 
 * 定义 Factor 的基本属性类型，用于分类和标签系统
 */
public enum FactorType implements StringIdentifiable {
    
    // 基础元素类型
    ELEMENTAL("elemental", "元素", 0),
    FIRE("fire", "火焰", 1),
    WATER("water", "水", 2),
    EARTH("earth", "土", 3),
    AIR("air", "风", 4),
    
    // 能量类型
    ENERGY("energy", "能量", 10),
    LIFE("life", "生命", 11),
    DEATH("death", "死亡", 12),
    CHAOS("chaos", "混沌", 13),
    ORDER("order", "秩序", 14),
    
    // 特殊类型
    VOID("void", "虚空", 20),
    TIME("time", "时间", 21),
    SPACE("space", "空间", 22),
    
    // 原始类型
    PRIMAL("primal", "原始", 30);
    
    private final String name;
    private final String displayName;
    private final int id;
    
    FactorType(String name, String displayName, int id) {
        this.name = name;
        this.displayName = displayName;
        this.id = id;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * 获取类型 ID（用于序列化）
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * 根据名称获取类型
     */
    public static FactorType fromName(String name) {
        for (FactorType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return ELEMENTAL;
    }
    
    /**
     * 根据 ID 获取类型
     */
    public static FactorType fromId(int id) {
        for (FactorType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return ELEMENTAL;
    }
    
    /**
     * 判断是否为元素类型
     */
    public boolean isElemental() {
        return this.id >= 0 && this.id <= 4;
    }
    
    /**
     * 判断是否为能量类型
     */
    public boolean isEnergy() {
        return this.id >= 10 && this.id <= 14;
    }
    
    /**
     * 判断是否为特殊类型
     */
    public boolean isSpecial() {
        return this.id >= 20 && this.id <= 22;
    }
    
    /**
     * 判断是否为原始类型
     */
    public boolean isPrimal() {
        return this.id == 30;
    }
}