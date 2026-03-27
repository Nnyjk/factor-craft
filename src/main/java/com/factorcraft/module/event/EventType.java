package com.factorcraft.module.event;

import java.util.UUID;

/**
 * 事件类型枚举
 * 
 * 定义所有随机事件的类型标识
 */
public enum EventType {
    /**
     * Factor 风暴 - 指定区域 Factor 浓度临时提升
     */
    FACTOR_STORM("factor_storm", "Factor 风暴", 0.3),
    
    /**
     * 商人访问 - 特殊 NPC 商人随机出现
     */
    MERCHANT_VISIT("merchant_visit", "神秘商人", 0.2),
    
    /**
     * 矿脉爆发 - 随机位置生成临时矿点
     */
    ORE_BURST("ore_burst", "矿脉爆发", 0.25),
    
    /**
     * 机器过载 - 玩家机器临时产出翻倍但消耗增加
     */
    MACHINE_OVERLOAD("machine_overload", "机器过载", 0.15),
    
    /**
     * 生物狂潮 - 区域内生物生成率大幅提升
     */
    CREATURE_FRENZY("creature_frenzy", "生物狂潮", 0.2),
    
    /**
     * 能量涌流 - 玩家 Factor 恢复速度提升
     */
    ENERGY_SURGE("energy_surge", "能量涌流", 0.25),
    
    /**
     * 时空扭曲 - 传送门效率提升
     */
    SPACE_TIME_WARP("space_time_warp", "时空扭曲", 0.1);
    
    private final String id;
    private final String displayName;
    private final double baseWeight;
    
    EventType(String id, String displayName, double baseWeight) {
        this.id = id;
        this.displayName = displayName;
        this.baseWeight = baseWeight;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getBaseWeight() {
        return baseWeight;
    }
    
    /**
     * 根据 ID 查找事件类型
     */
    public static EventType fromId(String id) {
        for (EventType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
