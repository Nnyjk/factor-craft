package com.factorcraft.module.creature.mutation;

import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.List;

/**
 * 变异效果定义
 * 
 * 描述生物变异后获得的能力和外观变化
 */
public record MutationEffect(
    /** 变异 ID */
    String id,
    
    /** 变异名称 */
    String name,
    
    /** 变异描述 */
    String description,
    
    /** 应用的状态效果列表 */
    List<StatusEffectInstance> effects,
    
    /** 伤害修正系数 (1.0 = 无修正) */
    double damageModifier,
    
    /** 生命值修正系数 (1.0 = 无修正) */
    double healthModifier,
    
    /** 移动速度修正系数 (1.0 = 无修正) */
    double speedModifier,
    
    /** 渲染颜色 (ARGB) */
    int color,
    
    /** 粒子效果 ID */
    String particleType,
    
    /** 是否为稀有变异（永久） */
    boolean isRare,
    
    /** 基础触发概率 (0.0-1.0) */
    double baseChance
) {
    
    /**
     * 创建变异效果（简化版）
     */
    public static MutationEffect create(
        String id,
        String name,
        String description,
        double damageModifier,
        double healthModifier,
        double speedModifier,
        int color,
        boolean isRare,
        double baseChance
    ) {
        return new MutationEffect(
            id, name, description,
            List.of(),
            damageModifier, healthModifier, speedModifier,
            color, "", isRare, baseChance
        );
    }
}
