package com.factorcraft.module.combat;

/**
 * 武器属性配置
 * 
 * 定义所有 T1-T5 武器的平衡参数
 */
public class WeaponAttributes {
    
    /**
     * 剑类武器属性
     */
    public static class Sword {
        // 基础伤害 (无附魔)
        public static final float[] DAMAGE = {6.0f, 7.0f, 8.0f, 9.0f, 10.0f};
        // 攻击速度 (攻击间隔，越小越快)
        public static final float[] ATTACK_SPEED = {-2.4f, -2.4f, -2.3f, -2.3f, -2.2f};
        // 耐久度
        public static final int[] DURABILITY = {1500, 2000, 2500, 3000, 3500};
        // Factor 伤害加成
        public static final double[] FACTOR_BONUS = {0.2, 0.4, 0.6, 0.8, 1.0};
        // 破甲比例
        public static final float[] ARMOR_PIERCE = {0.05f, 0.1f, 0.15f, 0.2f, 0.25f};
        // 附魔能力
        public static final int[] ENCHANTABILITY = {10, 12, 14, 16, 18};
    }
    
    /**
     * 锤类武器属性
     */
    public static class Hammer {
        // 基础伤害 (高于剑，但速度慢)
        public static final float[] DAMAGE = {8.0f, 10.0f, 12.0f, 14.0f, 16.0f};
        // 攻击速度 (慢)
        public static final float[] ATTACK_SPEED = {-3.2f, -3.2f, -3.0f, -3.0f, -2.8f};
        // 耐久度
        public static final int[] DURABILITY = {1800, 2400, 3000, 3600, 4200};
        // Factor 伤害加成
        public static final double[] FACTOR_BONUS = {0.1, 0.2, 0.3, 0.4, 0.5};
        // 破甲比例 (锤的特色)
        public static final float[] ARMOR_PIERCE = {0.2f, 0.3f, 0.4f, 0.5f, 0.6f};
        // 维度穿透 (从 T2 开始)
        public static final int[] DIMENSION_PENETRATION = {0, 1, 2, 3, 4};
        // 附魔能力
        public static final int[] ENCHANTABILITY = {8, 10, 12, 14, 16};
    }
    
    /**
     * 弓类武器属性
     */
    public static class Bow {
        // 基础伤害
        public static final float[] DAMAGE = {5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
        // 蓄力时间 (ticks)
        public static final int[] DRAW_TIME = {20, 19, 18, 17, 16};
        // 耐久度
        public static final int[] DURABILITY = {1200, 1600, 2000, 2400, 2800};
        // Factor 伤害加成
        public static final double[] FACTOR_BONUS = {0.15, 0.3, 0.45, 0.6, 0.75};
        // 射程加成 (百分比)
        public static final float[] RANGE_BONUS = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f};
        // 穿透箭矢 (T3+)
        public static final int[] PIERCE_LEVEL = {0, 0, 1, 2, 3};
        // 附魔能力
        public static final int[] ENCHANTABILITY = {12, 14, 16, 18, 20};
    }
    
    /**
     * 获取武器名称
     */
    public static String getWeaponName(String type, int tier) {
        String[] tierNames = {"一", "二", "三", "四", "五"};
        String typeNames = switch (type.toLowerCase()) {
            case "sword" -> "阶 Factor 剑";
            case "hammer" -> "阶维度锤";
            case "bow" -> "阶共振弓";
            default -> "武器";
        };
        return tierNames[tier - 1] + typeNames;
    }
    
    /**
     * 验证武器平衡性
     */
    public static boolean validateBalance() {
        // 验证伤害递增
        for (int i = 0; i < 4; i++) {
            if (Sword.DAMAGE[i] >= Sword.DAMAGE[i + 1]) return false;
            if (Hammer.DAMAGE[i] >= Hammer.DAMAGE[i + 1]) return false;
            if (Bow.DAMAGE[i] >= Bow.DAMAGE[i + 1]) return false;
        }
        
        // 验证耐久度递增
        for (int i = 0; i < 4; i++) {
            if (Sword.DURABILITY[i] >= Sword.DURABILITY[i + 1]) return false;
            if (Hammer.DURABILITY[i] >= Hammer.DURABILITY[i + 1]) return false;
            if (Bow.DURABILITY[i] >= Bow.DURABILITY[i + 1]) return false;
        }
        
        return true;
    }
}
