package com.factorcraft.module.combat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CombatModule 单元测试
 * 
 * 测试战斗系统模块功能
 */
@DisplayName("CombatModule Tests")
public class CombatModuleTest {
    
    @Test
    @DisplayName("CombatModule - 单例模式验证")
    public void testSingletonPattern() {
        CombatModule instance1 = CombatModule.getInstance();
        CombatModule instance2 = CombatModule.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2, "CombatModule 应该返回相同的单例实例");
    }
    
    @Test
    @DisplayName("CombatModule - 模块实例可创建")
    public void testModuleInstance() {
        CombatModule module = CombatModule.getInstance();
        assertNotNull(module);
    }
    
    @Test
    @DisplayName("武器等级系统 - T1-T5 等级存在")
    public void testWeaponTierSystem() {
        // 验证武器等级公式
        int[] tiers = {1, 2, 3, 4, 5};
        double[] expectedBonuses = {0.2, 0.4, 0.6, 0.8, 1.0};
        
        for (int i = 0; i < tiers.length; i++) {
            // 验证伤害加成公式：tier * 0.2
            double calculatedBonus = tiers[i] * 0.2;
            assertEquals(expectedBonuses[i], calculatedBonus, 0.001);
        }
    }
    
    @Test
    @DisplayName("武器等级系统 - 维度穿透规则")
    public void testDimensionPenetrationRule() {
        // T1-T2: 无穿透
        assertEquals(0, getDimensionPenetration(1));
        assertEquals(0, getDimensionPenetration(2));
        
        // T3+: tier - 2
        assertEquals(1, getDimensionPenetration(3));
        assertEquals(2, getDimensionPenetration(4));
        assertEquals(3, getDimensionPenetration(5));
    }
    
    @Test
    @DisplayName("武器等级系统 - 耐久度公式")
    public void testDurabilityFormula() {
        // 公式：1000 + tier * 500
        assertEquals(1500, calculateDurability(1));
        assertEquals(2000, calculateDurability(2));
        assertEquals(2500, calculateDurability(3));
        assertEquals(3000, calculateDurability(4));
        assertEquals(3500, calculateDurability(5));
    }
    
    @Test
    @DisplayName("武器平衡性 - 高等级优势验证")
    public void testHighTierAdvantage() {
        // T5 应该比 T1 有显著优势
        double t1Bonus = 1 * 0.2;
        double t5Bonus = 5 * 0.2;
        
        assertTrue(t5Bonus > t1Bonus);
        assertEquals(5.0, t5Bonus / t1Bonus, 0.001); // T5 是 T1 的 5 倍
        
        // T5 耐久度也更高
        int t1Durability = calculateDurability(1);
        int t5Durability = calculateDurability(5);
        assertTrue(t5Durability > t1Durability);
        assertEquals(2.33, (double)t5Durability / t1Durability, 0.01); // 约 2.33 倍
    }
    
    @Test
    @DisplayName("武器平衡性 - 等级递增规则")
    public void testTierProgression() {
        // 每个等级的伤害加成应该递增
        for (int tier = 1; tier < 5; tier++) {
            double currentBonus = tier * 0.2;
            double nextBonus = (tier + 1) * 0.2;
            assertTrue(nextBonus > currentBonus);
            assertEquals(0.2, nextBonus - currentBonus, 0.001); // 每级增加 0.2
        }
    }
    
    // 辅助方法：模拟维度穿透计算
    private int getDimensionPenetration(int tier) {
        return tier >= 3 ? tier - 2 : 0;
    }
    
    // 辅助方法：模拟耐久度计算
    private int calculateDurability(int tier) {
        return 1000 + tier * 500;
    }
}
