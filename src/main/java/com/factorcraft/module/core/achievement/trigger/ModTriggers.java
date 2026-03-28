package com.factorcraft.module.core.achievement.trigger;

import com.factorcraft.FactorCraftMod;

/**
 * 预定义触发器注册
 * 注册所有基础触发器实例
 */
public class ModTriggers {
    
    /**
     * 注册所有预定义触发器
     */
    public static void registerAll() {
        TriggerRegistry registry = TriggerRegistry.getInstance();
        
        // ========== Factor 生产触发器 ==========
        registry.registerAll(
            // 基础 Factor 生产
            new FactorProductionTrigger("produce_factor_crystal", null, 1, null),
            new FactorProductionTrigger("produce_factor_crystal_10", null, 10, null),
            new FactorProductionTrigger("produce_factor_crystal_100", null, 100, null),
            new FactorProductionTrigger("produce_factor_crystal_1000", null, 1000, null),
            
            // 高纯度 Factor
            new FactorProductionTrigger("produce_pure_factor", "pure", 1, null),
            new FactorProductionTrigger("produce_pure_factor_10", "pure", 10, null),
            
            // 浓缩 Factor
            new FactorProductionTrigger("produce_concentrated_factor", "concentrated", 1, null),
            new FactorProductionTrigger("produce_concentrated_factor_10", "concentrated", 10, null)
        );
        
        // ========== 机器制作触发器 ==========
        registry.registerAll(
            // 基础机器
            new MachineCraftTrigger("craft_basic_machine", null, 1),
            new MachineCraftTrigger("craft_mk2_machine", null, 2),
            new MachineCraftTrigger("craft_mk3_machine", null, 3),
            
            // 特定机器
            new MachineCraftTrigger("craft_factor_extractor", "factor_extractor", 1),
            new MachineCraftTrigger("craft_factor_purifier", "factor_purifier", 1),
            new MachineCraftTrigger("craft_factor_compressor", "factor_compressor", 1),
            new MachineCraftTrigger("craft_factor_reactor", "factor_reactor", 1),
            new MachineCraftTrigger("craft_factor_synthesizer", "factor_synthesizer", 1)
        );
        
        // ========== 任务完成触发器 ==========
        registry.registerAll(
            // 主线任务
            new QuestCompleteTrigger("complete_main_quest", null, "main", true),
            new QuestCompleteTrigger("complete_first_quest", null, null, null),
            new QuestCompleteTrigger("complete_10_quests", null, null, null),
            
            // 支线任务
            new QuestCompleteTrigger("complete_side_quest", null, "side", false)
        );
        
        // ========== Boss 击杀触发器 ==========
        registry.registerAll(
            // 基础 Boss
            new BossKillTrigger("kill_boss", null, null, null),
            new BossKillTrigger("kill_infected_boss", "infected", null, null),
            new BossKillTrigger("kill_corrupted_boss", "corrupted", null, null),
            
            // 高难度 Boss
            new BossKillTrigger("kill_boss_level_50", null, null, 50),
            new BossKillTrigger("kill_boss_level_100", null, null, 100)
        );
        
        // ========== 探索触发器 ==========
        registry.registerAll(
            // 维度探索
            new ExplorationTrigger("explore_dimension", "factor_craft:the_depths", null),
            new ExplorationTrigger("explore_dimension_void", "factor_craft:the_void", null),
            new ExplorationTrigger("explore_dimension_crystal", "factor_craft:crystal_dimension", null),
            
            // 结构发现
            new ExplorationTrigger("discover_structure", null, "factor_craft:abandoned_lab"),
            new ExplorationTrigger("discover_structure_factory", null, "factor_craft:abandoned_factory"),
            new ExplorationTrigger("discover_structure_reactor", null, "factor_craft:reactor_ruins")
        );
        
        FactorCraftMod.LOGGER.info("Registered {} pre-defined achievement triggers", registry.getTotalTriggers());
    }
}
