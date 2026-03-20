package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业技能基类
 */
public abstract class ProfessionSkill {
    
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final ProfessionType professionType;
    protected final SkillType skillType;
    protected final int factorCost;
    protected final int cooldownTicks; // 冷却时间（tick）
    
    public ProfessionSkill(String id, String displayName, String description,
                          ProfessionType professionType, SkillType skillType,
                          int factorCost, int cooldownTicks) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.professionType = professionType;
        this.skillType = skillType;
        this.factorCost = factorCost;
        this.cooldownTicks = cooldownTicks;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ProfessionType getProfessionType() {
        return professionType;
    }
    
    public SkillType getSkillType() {
        return skillType;
    }
    
    public int getFactorCost() {
        return factorCost;
    }
    
    public int getCooldownTicks() {
        return cooldownTicks;
    }
    
    /**
     * 检查是否可以使用技能
     */
    public boolean canUse(ServerPlayerEntity player) {
        // 检查职业
        // 检查Factor消耗
        // 检查冷却时间
        return true; // 简化实现
    }
    
    /**
     * 执行技能
     */
    public abstract void execute(ServerPlayerEntity player);
    
    /**
     * 扣除Factor消耗
     */
    protected void consumeFactor(ServerPlayerEntity player) {
        // TODO: 实现Factor消耗
    }
}