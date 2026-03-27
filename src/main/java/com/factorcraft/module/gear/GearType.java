package com.factorcraft.module.gear;

/**
 * 装备类型枚举
 * 
 * 定义装备的三大分类
 */
public enum GearType {
    /** 工具类 */
    TOOL("tool"),
    
    /** 武器类 */
    WEAPON("weapon"),
    
    /** 护甲类 */
    ARMOR("armor");
    
    private final String name;
    
    GearType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
