package com.factorcraft.module.profession.talent;

import java.util.*;

/**
 * 天赋效果处理器
 * 
 * 负责管理和计算玩家的天赋效果
 * 效果值会根据天赋等级动态计算
 */
public class TalentEffectHandler {
    
    private final PlayerTalentData playerData;
    private final Map<TalentEffectType, Float> cachedEffects;
    private boolean needsRecache;
    
    public TalentEffectHandler(PlayerTalentData playerData) {
        this.playerData = playerData;
        this.cachedEffects = new EnumMap<>(TalentEffectType.class);
        this.needsRecache = true;
    }
    
    /**
     * 获取指定效果类型的总值
     * 
     * @param effectType 效果类型
     * @return 效果值（从所有已学习天赋中累加）
     */
    public float getEffectValue(TalentEffectType effectType) {
        if (needsRecache) {
            recacheEffects();
        }
        return cachedEffects.getOrDefault(effectType, 0f);
    }
    
    /**
     * 获取所有效果的映射
     */
    public Map<TalentEffectType, Float> getAllEffectValues() {
        if (needsRecache) {
            recacheEffects();
        }
        return Collections.unmodifiableMap(cachedEffects);
    }
    
    /**
     * 检查是否有指定效果
     */
    public boolean hasEffect(TalentEffectType effectType) {
        if (needsRecache) {
            recacheEffects();
        }
        return cachedEffects.containsKey(effectType) && cachedEffects.get(effectType) > 0;
    }
    
    /**
     * 获取百分比效果的百分比值
     * 
     * @param effectType 效果类型
     * @return 百分比值（如 0.15 表示 15%）
     */
    public float getPercentEffect(TalentEffectType effectType) {
        float value = getEffectValue(effectType);
        // 如果效果已经标记为百分比，直接返回
        // 否则转换为百分比（假设原始值为 0-100）
        if (value > 1) {
            return value / 100f;
        }
        return value;
    }
    
    /**
     * 标记需要重新计算效果
     * 当玩家天赋等级变化时调用
     */
    public void markDirty() {
        this.needsRecache = true;
    }
    
    /**
     * 重新计算所有效果值
     * 从玩家的天赋数据中累加所有效果
     */
    private void recacheEffects() {
        cachedEffects.clear();
        
        // 遍历玩家已学习的天赋
        for (String talentId : playerData.getUnlockedTalentIds()) {
            TalentNode talent = TalentNodes.getTalentById(talentId);
            if (talent == null) continue;
            
            int level = playerData.getTalentLevel(talentId);
            if (level <= 0) continue;
            
            // 累加所有效果
            for (TalentEffect effect : talent.getEffects()) {
                float effectValue = effect.getValueForLevel(level);
                cachedEffects.merge(effect.getType(), effectValue, Float::sum);
            }
        }
        
        needsRecache = false;
    }
    
    // ==================== 便捷方法 ====================
    
    /**
     * 应用 Factor 消耗减免
     * 
     * @param baseCost 基础消耗
     * @return 减免后的消耗
     */
    public int applyFactorCostReduction(int baseCost) {
        float reduction = getEffectValue(TalentEffectType.FACTOR_COST_REDUCTION);
        if (reduction <= 0) return baseCost;
        
        // 转换为百分比
        float reductionPercent = reduction > 1 ? reduction / 100f : reduction;
        int reduced = Math.round(baseCost * (1 - reductionPercent));
        return Math.max(1, reduced); // 最少消耗 1
    }
    
    /**
     * 应用机器速度加成
     * 
     * @param baseSpeed 基础速度（tick）
     * @return 加成后的速度（减少 tick 数）
     */
    public int applyMachineSpeedBonus(int baseSpeed) {
        float bonus = getEffectValue(TalentEffectType.MACHINE_SPEED);
        if (bonus <= 0) return baseSpeed;
        
        float bonusPercent = bonus > 1 ? bonus / 100f : bonus;
        int reduced = Math.round(baseSpeed * (1 - bonusPercent));
        return Math.max(1, reduced); // 最少 1 tick
    }
    
    /**
     * 应用攻击伤害加成
     * 
     * @param baseDamage 基础伤害
     * @return 加成后的伤害
     */
    public float applyAttackDamageBonus(float baseDamage) {
        float bonus = getEffectValue(TalentEffectType.ATTACK_DAMAGE);
        if (bonus <= 0) return baseDamage;
        
        float bonusPercent = bonus > 1 ? bonus / 100f : bonus;
        return baseDamage * (1 + bonusPercent);
    }
    
    /**
     * 计算技能冷却时间
     * 
     * @param baseCooldown 基础冷却时间（tick）
     * @return 减免后的冷却时间
     */
    public int calculateCooldown(int baseCooldown) {
        float reduction = getEffectValue(TalentEffectType.COOLDOWN_REDUCTION);
        if (reduction <= 0) return baseCooldown;
        
        float reductionPercent = reduction > 1 ? reduction / 100f : reduction;
        int reduced = Math.round(baseCooldown * (1 - reductionPercent));
        return Math.max(1, reduced);
    }
    
    /**
     * 获取 Factor 存储上限加成
     * 
     * @return 额外的存储上限
     */
    public int getFactorStorageBonus() {
        float bonus = getEffectValue(TalentEffectType.FACTOR_STORAGE);
        return Math.round(bonus);
    }
    
    /**
     * 获取感知范围
     * 
     * @param baseRange 基础范围
     * @return 加成后的范围
     */
    public int getSenseRange(int baseRange) {
        float bonus = getEffectValue(TalentEffectType.FACTOR_SENSE_RANGE);
        return baseRange + Math.round(bonus);
    }
    
    /**
     * 应用技能伤害加成
     */
    public float applySkillDamageBonus(float baseDamage) {
        float bonus = getEffectValue(TalentEffectType.SKILL_DAMAGE);
        if (bonus <= 0) return baseDamage;
        float bonusPercent = bonus > 1 ? bonus / 100f : bonus;
        return baseDamage * (1 + bonusPercent);
    }
    
    /**
     * 应用暴击率加成
     */
    public float applyCriticalChanceBonus(float baseChance) {
        float bonus = getEffectValue(TalentEffectType.CRITICAL_CHANCE);
        if (bonus <= 0) return baseChance;
        return baseChance + (bonus > 1 ? bonus / 100f : bonus);
    }
    
    /**
     * 应用生命恢复加成
     */
    public float applyHealthRegenBonus(float baseRegen) {
        float bonus = getEffectValue(TalentEffectType.HEALTH_REGEN);
        if (bonus <= 0) return baseRegen;
        float bonusPercent = bonus > 1 ? bonus / 100f : bonus;
        return baseRegen * (1 + bonusPercent);
    }
    
    /**
     * 应用生长速度加成
     */
    public float applyGrowthSpeedBonus(float baseSpeed) {
        float bonus = getEffectValue(TalentEffectType.GROWTH_SPEED);
        if (bonus <= 0) return baseSpeed;
        float bonusPercent = bonus > 1 ? bonus / 100f : bonus;
        return baseSpeed * (1 + bonusPercent);
    }
    
    /**
     * 应用变异概率加成
     */
    public float applyMutationChanceBonus(float baseChance) {
        float bonus = getEffectValue(TalentEffectType.MUTATION_CHANCE);
        if (bonus <= 0) return baseChance;
        return baseChance + (bonus > 1 ? bonus / 100f : bonus);
    }
}