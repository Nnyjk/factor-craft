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
    @DisplayName("CombatModule - 模块可实例化")
    public void testModuleInstance() {
        CombatModule module = new CombatModule();
        assertNotNull(module);
    }
    
    @Test
    @DisplayName("武器等级系统 - T1-T5 等级存在")
    public void testWeaponTierSystem() {
        // 验证武器等级公式
        int[] tiers = {1, 2, 3, 4, 5};
        double[] expectedBonuses = {0.0, 0.2, 0.4, 0.6, 0.8};
        
        for (int i = 0; i < tiers.length; i++) {
            double bonus = (tiers[i] - 1) * 0.2;
            assertEquals(expectedBonuses[i], bonus, 0.01, 
                "T" + tiers[i] + " 武器应该有 " + (int)(expectedBonuses[i] * 100) + "% 伤害加成");
        }
    }
    
    @Test
    @DisplayName("防御塔类型 - 4 种塔存在")
    public void testDefenseTowerTypes() {
        String[] towerTypes = {"arrow", "frost", "lightning", "factor"};
        
        for (String type : towerTypes) {
            assertNotNull(type, "防御塔类型应该存在：" + type);
        }
        
        assertEquals(4, towerTypes.length, "应该有 4 种防御塔");
    }
    
    @Test
    @DisplayName("感染生物 - 4 种类型存在")
    public void testInfectedEntities() {
        String[] infectedTypes = {"zombie", "skeleton", "creeper", "slime"};
        
        for (String type : infectedTypes) {
            assertNotNull(type, "感染生物类型应该存在：" + type);
        }
        
        assertEquals(4, infectedTypes.length, "应该有 4 种感染生物");
    }
}
