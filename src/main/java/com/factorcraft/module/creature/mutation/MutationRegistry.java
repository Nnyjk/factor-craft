package com.factorcraft.module.creature.mutation;

import com.factorcraft.FactorCraftMod;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * 变异效果注册表
 * 
 * 管理所有可用的生物变异效果
 */
public class MutationRegistry {
    
    private static final Map<Identifier, MutationEffect> MUTATIONS = new LinkedHashMap<>();
    
    /**
     * 注册变异效果
     */
    public static void register(String modId, String id, MutationEffect effect) {
        Identifier key = Identifier.of(modId, id);
        if (MUTATIONS.containsKey(key)) {
            FactorCraftMod.LOGGER.warn("Mutation {} already registered, replacing", key);
        }
        MUTATIONS.put(key, effect);
        FactorCraftMod.LOGGER.debug("Registered mutation: {}", key);
    }
    
    /**
     * 获取变异效果
     */
    public static Optional<MutationEffect> get(Identifier id) {
        return Optional.ofNullable(MUTATIONS.get(id));
    }
    
    /**
     * 获取所有变异效果
     */
    public static Collection<MutationEffect> getAll() {
        return Collections.unmodifiableCollection(MUTATIONS.values());
    }
    
    /**
     * 根据稀有度获取变异效果
     */
    public static List<MutationEffect> getByRarity(boolean rare) {
        return MUTATIONS.values().stream()
            .filter(m -> m.isRare() == rare)
            .toList();
    }
    
    /**
     * 初始化内置变异效果
     */
    public static void init() {
        // 常见变异（临时）
        register(FactorCraftMod.MOD_ID, "swiftness", MutationEffect.create(
            "swiftness",
            "Swift Mutation",
            "移动速度大幅提升",
            1.0, 1.0, 1.5,  // 速度 +50%
            0xFF87CEEB,    // 天蓝色
            false,         // 常见变异
            0.30           // 30% 基础概率
        ));
        
        register(FactorCraftMod.MOD_ID, "strength", MutationEffect.create(
            "strength",
            "Strong Mutation",
            "攻击力提升",
            1.5, 1.0, 1.0,  // 伤害 +50%
            0xFF8B0000,    // 深红色
            false,
            0.30
        ));
        
        register(FactorCraftMod.MOD_ID, "toughness", MutationEffect.create(
            "toughness",
            "Tough Mutation",
            "生命值提升",
            1.0, 1.5, 0.9,  // 生命 +50%, 速度 -10%
            0xFF228B22,    // 森林绿
            false,
            0.30
        ));
        
        // 稀有变异（永久）
        register(FactorCraftMod.MOD_ID, "fire_infused", MutationEffect.create(
            "fire_infused",
            "Fire Infused",
            "火焰 infused，攻击附带火焰伤害",
            1.3, 1.2, 1.1,
            0xFFFF4500,    // 橙红色
            true,          // 稀有变异
            0.10           // 10% 基础概率
        ));
        
        register(FactorCraftMod.MOD_ID, "void_touched", MutationEffect.create(
            "void_touched",
            "Void Touched",
            "虚空接触，随机瞬移",
            1.2, 0.8, 1.3,
            0xFF4B0082,    // 靛青色
            true,
            0.05
        ));
        
        register(FactorCraftMod.MOD_ID, "nature_blessed", MutationEffect.create(
            "nature_blessed",
            "Nature Blessed",
            "自然祝福，生命恢复加快",
            1.1, 1.3, 1.1,
            0xFF90EE90,    // 浅绿色
            true,
            0.08
        ));
        
        register(FactorCraftMod.MOD_ID, "overcharged", MutationEffect.create(
            "overcharged",
            "Overcharged",
            "过载状态，全属性提升但不稳定",
            1.4, 1.2, 1.2,
            0xFF9400D3,    // 深紫色
            true,
            0.03           // 3% 极低概率
        ));
        
        FactorCraftMod.LOGGER.info("Registered {} mutation effects", MUTATIONS.size());
    }
}
