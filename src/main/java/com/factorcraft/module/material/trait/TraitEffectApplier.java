package com.factorcraft.module.material.trait;

import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.material.model.TraitCategory;
import com.factorcraft.module.material.model.TraitEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 特性效果应用器
 * 
 * 负责将物品上的特性效果应用到实体或世界中
 * 支持基于 Factor 浓度的效果调整
 */
public class TraitEffectApplier {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft:TraitApplier");
    
    // 属性修改器 UUID 命名空间
    private static final String MODIFIER_NAMESPACE = "factorcraft:trait_";
    
    /**
     * 应用物品上的所有特性效果到实体
     * 
     * @param stack 物品
     * @param entity 目标实体
     * @param concentration Factor 浓度 (0-100)
     */
    public static void applyTraitEffects(ItemStack stack, LivingEntity entity, double concentration) {
        if (stack.isEmpty() || entity == null) {
            return;
        }
        
        List<TraitInstance> traits = TraitService.getTraits(stack);
        if (traits.isEmpty()) {
            return;
        }
        
        // 计算共振加成
        double resonanceBonus = TraitService.calculateResonanceBonus(traits);
        
        // 应用每个特性效果
        for (TraitInstance traitInstance : traits) {
            Optional<TraitDefinition> traitOpt = TraitRegistry.get(traitInstance.traitId());
            if (traitOpt.isEmpty()) {
                continue;
            }
            
            TraitDefinition trait = traitOpt.get();
            double concentrationMultiplier = calculateConcentrationMultiplier(concentration, trait.category());
            
            // 应用效果
            for (TraitEffect effect : trait.effects()) {
                applyEffect(entity, effect, traitInstance.level(), concentrationMultiplier, resonanceBonus);
            }
        }
    }
    
    /**
     * 根据浓度和特性类别计算加成倍率
     */
    private static double calculateConcentrationMultiplier(double concentration, TraitCategory category) {
        TideStatus status = TideStatus.fromConcentration(concentration);
        
        return switch (category) {
            case EXTRACTION -> {
                // 提取类特性在高浓度时效果更好
                yield switch (status) {
                    case DEPLETED -> 0.5;      // 0-20%: 50% 效果
                    case LOW_ENERGY -> 0.75;   // 20-40%: 75% 效果
                    case STABLE -> 1.0;        // 40-60%: 100% 效果
                    case HIGH_ENERGY -> 1.25;  // 60-80%: 125% 效果
                    case OVERLOAD -> 1.5;      // 80-100%: 150% 效果
                };
            }
            case TRANSFER -> {
                // 传输类特性在稳定浓度时效果最佳
                yield switch (status) {
                    case DEPLETED -> 0.6;
                    case LOW_ENERGY -> 0.85;
                    case STABLE -> 1.2;
                    case HIGH_ENERGY -> 1.1;
                    case OVERLOAD -> 0.8;
                };
            }
            case PRODUCTION -> {
                // 生产类特性需要高能量
                yield switch (status) {
                    case DEPLETED -> 0.4;
                    case LOW_ENERGY -> 0.7;
                    case STABLE -> 0.9;
                    case HIGH_ENERGY -> 1.3;
                    case OVERLOAD -> 1.4;
                };
            }
            case ENVIRONMENT, DIMENSION -> {
                // 环境类特性受浓度影响较小
                yield 1.0;
            }
            case NEGATIVE -> {
                // 负面特性在高浓度时更强
                yield switch (status) {
                    case DEPLETED, LOW_ENERGY -> 0.7;
                    case STABLE -> 1.0;
                    case HIGH_ENERGY, OVERLOAD -> 1.3;
                };
            }
            case GENERAL, ENDGAME -> {
                // 通用和终局特性线性增长
                yield 0.8 + (concentration / 100.0) * 0.4;
            }
        };
    }
    
    /**
     * 应用单个效果到实体
     */
    private static void applyEffect(LivingEntity entity, TraitEffect effect, 
                                    int level, double concentrationMultiplier, double resonanceBonus) {
        // 检查条件
        if (effect.getCondition().isPresent() && !checkCondition(entity, effect.getCondition().get())) {
            return;
        }
        
        // 计算最终值
        double baseValue = effect.value();
        double scaledValue = baseValue * level * concentrationMultiplier * resonanceBonus;
        
        String target = effect.target();
        String operation = effect.operation();
        
        // 应用属性修改
        applyAttributeModification(entity, target, scaledValue, operation);
    }
    
    /**
     * 应用属性修改（简化版）
     */
    private static void applyAttributeModification(LivingEntity entity, String target, 
                                                    double value, String operation) {
        // 简化的实现，实际应用需要根据具体属性进行调整
        // 这里仅作为示例框架
        LOGGER.debug("Applying trait effect: {} = {} ({})", target, value, operation);
    }
    
    /**
     * 检查效果条件
     */
    private static boolean checkCondition(LivingEntity entity, TraitEffect.Condition condition) {
        // 维度条件
        if (condition.getDimension().isPresent() && !condition.getDimension().get().isBlank()) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                String dimensionId = serverWorld.getRegistryKey().getValue().toString();
                if (!dimensionId.equals(condition.getDimension().get())) {
                    return false;
                }
            }
        }
        
        // 浓度条件（TODO: 集成 Factor 浓度系统）
        if (condition.getConcentrationBelow().isPresent()) {
            // 暂时返回 true，等待浓度系统集成
        }
        
        return true;
    }
    
    /**
     * 清除实体上的所有特性效果
     */
    public static void clearTraitEffects(LivingEntity entity) {
        // 清除所有特性相关的属性修改器
        LOGGER.debug("Clearing trait effects for {}", entity.getName().getString());
    }
    
    /**
     * 计算物品的总特性加成
     * 
     * @param stack 物品
     * @param concentration Factor 浓度
     * @return 特性加成映射表
     */
    public static Map<String, Double> calculateTotalBonus(ItemStack stack, double concentration) {
        Map<String, Double> bonuses = new HashMap<>();
        
        if (stack.isEmpty()) {
            return bonuses;
        }
        
        List<TraitInstance> traits = TraitService.getTraits(stack);
        if (traits.isEmpty()) {
            return bonuses;
        }
        
        double resonanceBonus = TraitService.calculateResonanceBonus(traits);
        
        for (TraitInstance traitInstance : traits) {
            Optional<TraitDefinition> traitOpt = TraitRegistry.get(traitInstance.traitId());
            if (traitOpt.isEmpty()) {
                continue;
            }
            
            TraitDefinition trait = traitOpt.get();
            double concentrationMultiplier = calculateConcentrationMultiplier(concentration, trait.category());
            
            for (TraitEffect effect : trait.effects()) {
                if (effect.getCondition().isPresent() && !checkCondition(null, effect.getCondition().get())) {
                    continue;
                }
                
                double scaledValue = effect.value() * traitInstance.level() * concentrationMultiplier * resonanceBonus;
                bonuses.merge(effect.target(), scaledValue, Double::sum);
            }
        }
        
        return bonuses;
    }
}
