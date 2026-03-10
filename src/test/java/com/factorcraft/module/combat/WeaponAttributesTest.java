package com.factorcraft.module.combat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WeaponAttributes 单元测试
 * 
 * 测试武器平衡性配置
 */
@DisplayName("WeaponAttributes Tests")
public class WeaponAttributesTest {
    
    @Test
    @DisplayName("剑类属性 - 伤害递增验证")
    public void testSwordDamageProgression() {
        float[] damage = WeaponAttributes.Sword.DAMAGE;
        
        // 验证伤害递增
        for (int i = 0; i < damage.length - 1; i++) {
            assertTrue(damage[i + 1] > damage[i], 
                "T" + (i + 2) + "剑伤害应该高于 T" + (i + 1));
        }
        
        // 验证具体数值
        assertEquals(6.0f, damage[0]);  // T1
        assertEquals(10.0f, damage[4]); // T5
    }
    
    @Test
    @DisplayName("剑类属性 - 攻击速度验证")
    public void testSwordAttackSpeed() {
        float[] speed = WeaponAttributes.Sword.ATTACK_SPEED;
        
        // 验证攻击速度在合理范围 (-4.0 到 -2.0)
        for (float s : speed) {
            assertTrue(s >= -4.0f && s <= -2.0f, 
                "攻击速度应该在合理范围：" + s);
        }
        
        // 验证高等级攻击速度更快 (数值更大)
        assertTrue(speed[4] > speed[0], "T5 剑应该比 T1 剑攻击速度快");
    }
    
    @Test
    @DisplayName("剑类属性 - 耐久度递增验证")
    public void testSwordDurabilityProgression() {
        int[] durability = WeaponAttributes.Sword.DURABILITY;
        
        // 验证耐久度递增
        for (int i = 0; i < durability.length - 1; i++) {
            assertTrue(durability[i + 1] > durability[i], 
                "T" + (i + 2) + "剑耐久度应该高于 T" + (i + 1));
        }
        
        // 验证 T5 是 T1 的 2 倍以上
        assertTrue(durability[4] >= durability[0] * 2, 
            "T5 剑耐久度应该是 T1 的 2 倍以上");
    }
    
    @Test
    @DisplayName("剑类属性 - Factor 加成递增")
    public void testSwordFactorBonus() {
        double[] bonus = WeaponAttributes.Sword.FACTOR_BONUS;
        
        // 验证 Factor 加成递增
        for (int i = 0; i < bonus.length - 1; i++) {
            assertTrue(bonus[i + 1] > bonus[i], 
                "T" + (i + 2) + "剑 Factor 加成应该高于 T" + (i + 1));
        }
        
        // 验证 T5 是 T1 的 5 倍
        assertEquals(bonus[0] * 5, bonus[4], 0.001);
    }
    
    @Test
    @DisplayName("锤类属性 - 高伤害验证")
    public void testHammerHighDamage() {
        float[] hammerDamage = WeaponAttributes.Hammer.DAMAGE;
        float[] swordDamage = WeaponAttributes.Sword.DAMAGE;
        
        // 验证锤的伤害高于同等级剑
        for (int i = 0; i < hammerDamage.length; i++) {
            assertTrue(hammerDamage[i] > swordDamage[i], 
                "T" + (i + 1) + "锤伤害应该高于 T" + (i + 1) + "剑");
        }
        
        // 验证锤 T1 伤害 >=8
        assertTrue(hammerDamage[0] >= 8.0f);
    }
    
    @Test
    @DisplayName("锤类属性 - 高破甲验证")
    public void testHammerArmorPierce() {
        float[] hammerAP = WeaponAttributes.Hammer.ARMOR_PIERCE;
        float[] swordAP = WeaponAttributes.Sword.ARMOR_PIERCE;
        
        // 验证锤的破甲高于剑
        for (int i = 0; i < hammerAP.length; i++) {
            assertTrue(hammerAP[i] > swordAP[i], 
                "T" + (i + 1) + "锤破甲应该高于 T" + (i + 1) + "剑");
        }
        
        // 验证 T5 锤破甲 >= 50%
        assertTrue(hammerAP[4] >= 0.5f);
    }
    
    @Test
    @DisplayName("锤类属性 - 维度穿透验证")
    public void testHammerDimensionPenetration() {
        int[] penetration = WeaponAttributes.Hammer.DIMENSION_PENETRATION;
        
        // 验证 T1 无穿透
        assertEquals(0, penetration[0]);
        
        // 验证 T2+ 有穿透
        for (int i = 1; i < penetration.length; i++) {
            assertTrue(penetration[i] > 0, 
                "T" + (i + 1) + "锤应该有维度穿透");
        }
        
        // 验证穿透递增
        for (int i = 0; i < penetration.length - 1; i++) {
            assertTrue(penetration[i + 1] >= penetration[i], 
                "维度穿透应该递增或持平");
        }
    }
    
    @Test
    @DisplayName("弓类属性 - 蓄力时间递减")
    public void testBowDrawTime() {
        int[] drawTime = WeaponAttributes.Bow.DRAW_TIME;
        
        // 验证蓄力时间递减 (越快越好)
        for (int i = 0; i < drawTime.length - 1; i++) {
            assertTrue(drawTime[i + 1] < drawTime[i], 
                "T" + (i + 2) + "弓蓄力时间应该短于 T" + (i + 1));
        }
        
        // 验证 T5 蓄力 <= 16 ticks
        assertTrue(drawTime[4] <= 16);
    }
    
    @Test
    @DisplayName("弓类属性 - 射程加成递增")
    public void testBowRangeBonus() {
        float[] range = WeaponAttributes.Bow.RANGE_BONUS;
        
        // 验证射程加成递增
        for (int i = 0; i < range.length - 1; i++) {
            assertTrue(range[i + 1] > range[i], 
                "T" + (i + 2) + "弓射程加成应该高于 T" + (i + 1));
        }
        
        // 验证 T1 无加成
        assertEquals(0.0f, range[0]);
        
        // 验证 T5 加成 >= 30%
        assertTrue(range[4] >= 0.3f);
    }
    
    @Test
    @DisplayName("弓类属性 - 穿透等级验证")
    public void testBowPierceLevel() {
        int[] pierce = WeaponAttributes.Bow.PIERCE_LEVEL;
        
        // 验证 T1-T2 无穿透
        assertEquals(0, pierce[0]);
        assertEquals(0, pierce[1]);
        
        // 验证 T3+ 有穿透
        assertTrue(pierce[2] >= 1);
        assertTrue(pierce[4] >= pierce[2]);
    }
    
    @Test
    @DisplayName("武器名称 - 中文命名验证")
    public void testWeaponNames() {
        // 验证剑命名
        assertEquals("一阶 Factor 剑", WeaponAttributes.getWeaponName("sword", 1));
        assertEquals("五阶 Factor 剑", WeaponAttributes.getWeaponName("sword", 5));
        
        // 验证锤命名
        assertEquals("一阶维度锤", WeaponAttributes.getWeaponName("hammer", 1));
        assertEquals("五阶维度锤", WeaponAttributes.getWeaponName("hammer", 5));
        
        // 验证弓命名
        assertEquals("一阶共振弓", WeaponAttributes.getWeaponName("bow", 1));
        assertEquals("五阶共振弓", WeaponAttributes.getWeaponName("bow", 5));
    }
    
    @Test
    @DisplayName("平衡性验证 - 整体递增规则")
    public void testOverallBalance() {
        assertTrue(WeaponAttributes.validateBalance(), 
            "武器平衡性验证应该通过");
    }
    
    @Test
    @DisplayName("附魔能力 - 递增验证")
    public void testEnchantabilityProgression() {
        // 剑
        int[] swordEnc = WeaponAttributes.Sword.ENCHANTABILITY;
        for (int i = 0; i < swordEnc.length - 1; i++) {
            assertTrue(swordEnc[i + 1] > swordEnc[i]);
        }
        
        // 锤
        int[] hammerEnc = WeaponAttributes.Hammer.ENCHANTABILITY;
        for (int i = 0; i < hammerEnc.length - 1; i++) {
            assertTrue(hammerEnc[i + 1] > hammerEnc[i]);
        }
        
        // 弓
        int[] bowEnc = WeaponAttributes.Bow.ENCHANTABILITY;
        for (int i = 0; i < bowEnc.length - 1; i++) {
            assertTrue(bowEnc[i + 1] > bowEnc[i]);
        }
    }
}
