package com.factorcraft.module.profession;

import com.factorcraft.module.profession.balance.ProfessionBalanceConfig;
import com.factorcraft.module.profession.balance.ProfessionBalanceConfig.ProfessionAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 职业系统数值平衡测试
 * 
 * 验证各职业的数值是否符合平衡要求：
 * - 发育速度差异不超过 15%
 * - 各职业有独特的优势和劣势
 * - 天赋效果在合理范围内
 */
@DisplayName("职业数值平衡测试")
class ProfessionBalanceTest {
    
    private ProfessionBalanceConfig config;
    
    @BeforeEach
    void setUp() {
        config = ProfessionBalanceConfig.getInstance();
    }
    
    @Test
    @DisplayName("职业平衡验证应通过")
    void testBalanceValidation() {
        assertTrue(config.validateBalance(), 
            "职业平衡差异应不超过 15%");
    }
    
    @Test
    @DisplayName("Factor工程师应有发育速度优势")
    void testEngineerDevelopmentSpeed() {
        ProfessionAttributes engineer = config.getProfessionAttributes("engineer");
        
        // Factor工程师应该有发育速度加成
        assertTrue(engineer.getDevelopmentSpeed() >= 1.0,
            "Factor工程师的发育速度应 >= 1.0");
        
        // 发育速度加成不超过 15%
        assertTrue(engineer.getDevelopmentSpeed() <= 1.15,
            "Factor工程师的发育速度加成应不超过 15%");
    }
    
    @Test
    @DisplayName("能量培育师应有资源收集优势")
    void testCultivatorResourceGathering() {
        ProfessionAttributes cultivator = config.getProfessionAttributes("cultivator");
        
        // 能量培育师应该有资源收集加成
        assertTrue(cultivator.getResourceGathering() >= 1.0,
            "能量培育师的资源收集应 >= 1.0");
        
        // 资源收集加成不超过 15%
        assertTrue(cultivator.getResourceGathering() <= 1.15,
            "能量培育师的资源收集加成应不超过 15%");
    }
    
    @Test
    @DisplayName("潮汐探索者应有技能威力优势")
    void testExplorerSkillPower() {
        ProfessionAttributes explorer = config.getProfessionAttributes("explorer");
        
        // 潮汐探索者应该有技能威力加成
        assertTrue(explorer.getSkillPower() >= 1.0,
            "潮汐探索者的技能威力应 >= 1.0");
        
        // 技能威力加成不超过 15%
        assertTrue(explorer.getSkillPower() <= 1.15,
            "潮汐探索者的技能威力加成应不超过 15%");
    }
    
    @Test
    @DisplayName("因子掌控者应保持均衡")
    void testMasterBalance() {
        ProfessionAttributes master = config.getProfessionAttributes("master");
        
        // 因子掌控者作为全能职业，各项属性应接近基准
        assertEquals(1.0, master.getDevelopmentSpeed(), 0.05,
            "因子掌控者的发育速度应接近基准");
        assertEquals(1.0, master.getFactorEfficiency(), 0.05,
            "因子掌控者的Factor效率应接近基准");
        assertEquals(1.0, master.getSkillPower(), 0.05,
            "因子掌控者的技能威力应接近基准");
    }
    
    @Test
    @DisplayName("发育速度差异应在允许范围内")
    void testDevelopmentSpeedRange() {
        double maxDevSpeed = 0;
        double minDevSpeed = Double.MAX_VALUE;
        
        for (String professionId : new String[]{"engineer", "cultivator", "explorer", "master"}) {
            ProfessionAttributes attrs = config.getProfessionAttributes(professionId);
            if (attrs.getDevelopmentSpeed() > maxDevSpeed) {
                maxDevSpeed = attrs.getDevelopmentSpeed();
            }
            if (attrs.getDevelopmentSpeed() < minDevSpeed) {
                minDevSpeed = attrs.getDevelopmentSpeed();
            }
        }
        
        double difference = (maxDevSpeed - minDevSpeed) / minDevSpeed * 100;
        
        assertTrue(difference <= 15.0,
            String.format("发育速度差异 %.2f%% 应不超过 15%%", difference));
    }
    
    @Test
    @DisplayName("技能冷却倍率应在合理范围")
    void testSkillCooldownMultiplier() {
        double cooldown = config.getSkillBalance().getCooldownMultiplier();
        
        assertTrue(cooldown >= 0.8 && cooldown <= 1.2,
            "技能冷却倍率应在 0.8-1.2 范围内");
    }
    
    @Test
    @DisplayName("Factor消耗倍率应在合理范围")
    void testFactorCostMultiplier() {
        double cost = config.getSkillBalance().getFactorCostMultiplier();
        
        assertTrue(cost >= 0.8 && cost <= 1.2,
            "Factor消耗倍率应在 0.8-1.2 范围内");
    }
    
    @Test
    @DisplayName("天赋效果上限应合理")
    void testTalentEffectCap() {
        double cap = config.getTalentBalance().getTalentEffectCap();
        
        // 单一天赋效果上限应在 30%-70% 之间
        assertTrue(cap >= 0.3 && cap <= 0.7,
            "天赋效果上限应在 30%-70% 之间");
    }
    
    @Test
    @DisplayName("应能生成平衡报告")
    void testBalanceReport() {
        String report = config.getBalanceReport();
        
        assertNotNull(report, "平衡报告不应为空");
        assertTrue(report.contains("engineer"), "报告应包含 engineer");
        assertTrue(report.contains("cultivator"), "报告应包含 cultivator");
        assertTrue(report.contains("explorer"), "报告应包含 explorer");
        assertTrue(report.contains("master"), "报告应包含 master");
        assertTrue(report.contains("平衡验证"), "报告应包含平衡验证结果");
    }
    
    @Test
    @DisplayName("各职业应有不同的特点")
    void testProfessionDiversity() {
        ProfessionAttributes engineer = config.getProfessionAttributes("engineer");
        ProfessionAttributes cultivator = config.getProfessionAttributes("cultivator");
        ProfessionAttributes explorer = config.getProfessionAttributes("explorer");
        
        // 验证各职业有不同的优势方向
        // Factor工程师的发育速度应该最高
        assertTrue(engineer.getDevelopmentSpeed() >= cultivator.getDevelopmentSpeed(),
            "Factor工程师的发育速度应不低于能量培育师");
        assertTrue(engineer.getDevelopmentSpeed() >= explorer.getDevelopmentSpeed(),
            "Factor工程师的发育速度应不低于潮汐探索者");
        
        // 能量培育师的资源收集应该最高
        assertTrue(cultivator.getResourceGathering() >= engineer.getResourceGathering(),
            "能量培育师的资源收集应不低于Factor工程师");
        assertTrue(cultivator.getResourceGathering() >= explorer.getResourceGathering(),
            "能量培育师的资源收集应不低于潮汐探索者");
        
        // 潮汐探索者的技能威力应该最高
        assertTrue(explorer.getSkillPower() >= engineer.getSkillPower(),
            "潮汐探索者的技能威力应不低于Factor工程师");
        assertTrue(explorer.getSkillPower() >= cultivator.getSkillPower(),
            "潮汐探索者的技能威力应不低于能量培育师");
    }
}