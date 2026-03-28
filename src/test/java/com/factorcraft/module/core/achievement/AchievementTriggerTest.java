package com.factorcraft.module.core.achievement;

import com.factorcraft.module.core.achievement.trigger.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 成就触发器系统测试
 */
public class AchievementTriggerTest {
    
    private TriggerRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = TriggerRegistry.getInstance();
        registry.clear();
    }
    
    @Test
    void testTriggerRegistrySingleton() {
        TriggerRegistry instance1 = TriggerRegistry.getInstance();
        TriggerRegistry instance2 = TriggerRegistry.getInstance();
        assertSame(instance1, instance2, "TriggerRegistry should be singleton");
    }
    
    @Test
    void testRegisterTrigger() {
        AchievementTrigger<?> trigger = new FactorProductionTrigger("test_trigger", null, 1, null);
        registry.register(trigger);
        
        assertEquals(1, registry.getTotalTriggers());
        assertTrue(registry.getTrigger("test_trigger").isPresent());
    }
    
    @Test
    void testGetTriggersByType() {
        registry.register(new FactorProductionTrigger("factor_1", null, 1, null));
        registry.register(new FactorProductionTrigger("factor_10", null, 10, null));
        registry.register(new MachineCraftTrigger("machine_1", null, 1));
        
        var factorTriggers = registry.getTriggersByType(TriggerType.FACTOR_PRODUCTION);
        var machineTriggers = registry.getTriggersByType(TriggerType.MACHINE_CRAFT);
        
        assertEquals(2, factorTriggers.size());
        assertEquals(1, machineTriggers.size());
    }
    
    @Test
    void testFactorProductionTriggerMatches() {
        AchievementTrigger<FactorProductionData> trigger = new FactorProductionTrigger("test", "pure", 10, "purifier");
        
        // 完全匹配
        assertTrue(trigger.matches(new FactorProductionData("pure", 10, "purifier")));
        
        // 数量不足
        assertFalse(trigger.matches(new FactorProductionData("pure", 5, "purifier")));
        
        // 类型不匹配
        assertFalse(trigger.matches(new FactorProductionData("concentrated", 10, "purifier")));
        
        // 来源不匹配
        assertFalse(trigger.matches(new FactorProductionData("pure", 10, "extractor")));
    }
    
    @Test
    void testFactorProductionTriggerWithWildcards() {
        AchievementTrigger<FactorProductionData> trigger = new FactorProductionTrigger("test", null, 10, null);
        
        // 任意类型，数量足够
        assertTrue(trigger.matches(new FactorProductionData("pure", 10, "purifier")));
        assertTrue(trigger.matches(new FactorProductionData("concentrated", 15, "extractor")));
        
        // 数量不足
        assertFalse(trigger.matches(new FactorProductionData("pure", 5, "purifier")));
    }
    
    @Test
    void testMachineCraftTriggerMatches() {
        AchievementTrigger<MachineCraftData> trigger = new MachineCraftTrigger("test", "factor_extractor", 2);
        
        // 完全匹配
        assertTrue(trigger.matches(new MachineCraftData("factor_extractor", 2)));
        assertTrue(trigger.matches(new MachineCraftData("factor_extractor", 3)));
        
        // 等级不足
        assertFalse(trigger.matches(new MachineCraftData("factor_extractor", 1)));
        
        // 机器不匹配
        assertFalse(trigger.matches(new MachineCraftData("factor_purifier", 2)));
    }
    
    @Test
    void testQuestCompleteTriggerMatches() {
        AchievementTrigger<QuestCompleteData> trigger = new QuestCompleteTrigger("test", null, "main", true);
        
        // 主线任务
        assertTrue(trigger.matches(new QuestCompleteData("quest_1", "main", true)));
        
        // 支线任务
        assertFalse(trigger.matches(new QuestCompleteData("quest_2", "side", false)));
    }
    
    @Test
    void testBossKillTriggerMatches() {
        AchievementTrigger<BossKillData> trigger = new BossKillTrigger("test", null, "infected", 50);
        
        // 等级足够
        assertTrue(trigger.matches(new BossKillData("boss_1", "infected", 50)));
        assertTrue(trigger.matches(new BossKillData("boss_2", "infected", 100)));
        
        // 等级不足
        assertFalse(trigger.matches(new BossKillData("boss_3", "infected", 25)));
        
        // 类型不匹配
        assertFalse(trigger.matches(new BossKillData("boss_4", "corrupted", 100)));
    }
    
    @Test
    void testExplorationTriggerMatches() {
        AchievementTrigger<ExplorationData> trigger = new ExplorationTrigger("test", "factor_craft:the_depths", null);
        
        // 维度匹配
        assertTrue(trigger.matches(new ExplorationData("factor_craft:the_depths", null, 0, 0)));
        
        // 维度不匹配
        assertFalse(trigger.matches(new ExplorationData("factor_craft:the_void", null, 0, 0)));
    }
    
    @Test
    void testAchievementCategoryMatchesTriggerType() {
        // FACTOR 分类匹配 FACTOR_PRODUCTION 触发器
        assertTrue(AchievementCategory.FACTOR.matchesTriggerType(TriggerType.FACTOR_PRODUCTION));
        assertFalse(AchievementCategory.FACTOR.matchesTriggerType(TriggerType.MACHINE_CRAFT));
        
        // MACHINE 分类匹配 MACHINE_CRAFT 触发器
        assertTrue(AchievementCategory.MACHINE.matchesTriggerType(TriggerType.MACHINE_CRAFT));
        assertFalse(AchievementCategory.MACHINE.matchesTriggerType(TriggerType.BOSS_KILL));
        
        // STORY 分类匹配 QUEST_COMPLETE 触发器
        assertTrue(AchievementCategory.STORY.matchesTriggerType(TriggerType.QUEST_COMPLETE));
        
        // EXPLORATION 分类匹配 EXPLORATION 触发器
        assertTrue(AchievementCategory.EXPLORATION.matchesTriggerType(TriggerType.EXPLORATION));
        
        // COMBAT 分类匹配 BOSS_KILL 触发器
        assertTrue(AchievementCategory.COMBAT.matchesTriggerType(TriggerType.BOSS_KILL));
    }
    
    @Test
    void testModTriggersRegistration() {
        ModTriggers.registerAll();
        
        // 验证注册了足够多的触发器
        assertTrue(registry.getTotalTriggers() > 20, "Should register many pre-defined triggers");
        
        // 验证每种类型都有触发器
        assertFalse(registry.getTriggersByType(TriggerType.FACTOR_PRODUCTION).isEmpty());
        assertFalse(registry.getTriggersByType(TriggerType.MACHINE_CRAFT).isEmpty());
        assertFalse(registry.getTriggersByType(TriggerType.QUEST_COMPLETE).isEmpty());
        assertFalse(registry.getTriggersByType(TriggerType.BOSS_KILL).isEmpty());
        assertFalse(registry.getTriggersByType(TriggerType.EXPLORATION).isEmpty());
    }
}
