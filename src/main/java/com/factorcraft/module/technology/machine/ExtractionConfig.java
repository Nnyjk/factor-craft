package com.factorcraft.module.technology.machine;

/**
 * 提取结构配置
 * 
 * 定义 T1-T5 提取结构的参数
 */
public final class ExtractionConfig {
    
    private ExtractionConfig() {}
    
    // ==================== 基础提取速率 ====================
    // 单位: Factor/tick (在标准条件下)
    
    /** T1 星辰收集器: ×1.0 */
    public static final double BASE_RATE_T1 = 1.0;
    /** T2 星辰阵列: ×2.0 */
    public static final double BASE_RATE_T2 = 2.0;
    /** T3 星云汲取器: ×4.0 */
    public static final double BASE_RATE_T3 = 4.0;
    /** T4 宇宙共鸣器: ×8.0 */
    public static final double BASE_RATE_T4 = 8.0;
    /** T5 虚空漩涡: ×16.0 */
    public static final double BASE_RATE_T5 = 16.0;
    
    /**
     * 获取指定 Tier 的基础提取速率
     */
    public static double getBaseRate(int tier) {
        return switch (tier) {
            case 1 -> BASE_RATE_T1;
            case 2 -> BASE_RATE_T2;
            case 3 -> BASE_RATE_T3;
            case 4 -> BASE_RATE_T4;
            case 5 -> BASE_RATE_T5;
            default -> BASE_RATE_T1;
        };
    }
    
    // ==================== Factor 存储 ====================
    // 单位: Factor
    
    public static final double MAX_STORAGE_T1 = 1_000.0;
    public static final double MAX_STORAGE_T2 = 2_500.0;
    public static final double MAX_STORAGE_T3 = 6_250.0;
    public static final double MAX_STORAGE_T4 = 15_625.0;
    public static final double MAX_STORAGE_T5 = 39_062.0;
    
    /**
     * 获取指定 Tier 的最大存储量
     */
    public static double getMaxStorage(int tier) {
        return switch (tier) {
            case 1 -> MAX_STORAGE_T1;
            case 2 -> MAX_STORAGE_T2;
            case 3 -> MAX_STORAGE_T3;
            case 4 -> MAX_STORAGE_T4;
            case 5 -> MAX_STORAGE_T5;
            default -> MAX_STORAGE_T1;
        };
    }
    
    // ==================== 影响范围 ====================
    // 单位: 区块
    
    /** T1: 1 区块 */
    public static final int RANGE_T1 = 1;
    /** T2: 3×3 区块 */
    public static final int RANGE_T2 = 3;
    /** T3: 5×5 区块 */
    public static final int RANGE_T3 = 5;
    /** T4: 9×9 区块 */
    public static final int RANGE_T4 = 9;
    /** T5: 15×15 区块 */
    public static final int RANGE_T5 = 15;
    
    /**
     * 获取指定 Tier 的影响范围（区块半径）
     */
    public static int getRange(int tier) {
        return switch (tier) {
            case 1 -> RANGE_T1;
            case 2 -> RANGE_T2;
            case 3 -> RANGE_T3;
            case 4 -> RANGE_T4;
            case 5 -> RANGE_T5;
            default -> RANGE_T1;
        };
    }
    
    // ==================== 结构效率 ====================
    
    public static final double EFFICIENCY_T1 = 1.00;
    public static final double EFFICIENCY_T2 = 1.20;
    public static final double EFFICIENCY_T3 = 1.50;
    public static final double EFFICIENCY_T4 = 1.80;
    public static final double EFFICIENCY_T5 = 2.00;
    
    /**
     * 获取指定 Tier 的结构效率
     */
    public static double getEfficiency(int tier) {
        return switch (tier) {
            case 1 -> EFFICIENCY_T1;
            case 2 -> EFFICIENCY_T2;
            case 3 -> EFFICIENCY_T3;
            case 4 -> EFFICIENCY_T4;
            case 5 -> EFFICIENCY_T5;
            default -> EFFICIENCY_T1;
        };
    }
    
    // ==================== 浓度系数 ====================
    // 基于区块浓度（标准参照 100）
    
    /**
     * 计算浓度系数
     * 
     * @param concentration 区块浓度 (10-150)
     * @return 浓度系数 (0.5 - 1.2)
     */
    public static double getConcentrationCoefficient(double concentration) {
        if (concentration > 50) return 1.2;      // 高浓度
        if (concentration > 30) return 1.0;      // 正常
        if (concentration > 10) return 0.8;      // 低浓度
        return 0.5;                              // 枯竭
    }
    
    // ==================== 推荐维度 ====================
    
    /**
     * 获取指定 Tier 的推荐维度
     * 
     * @return 维度 key，null 表示无限制
     */
    public static String getRecommendedDimension(int tier) {
        return switch (tier) {
            case 1, 2 -> "minecraft:overworld";  // T1-T2 推荐主世界
            case 3, 4 -> "minecraft:the_nether"; // T3-T4 推荐下界
            case 5 -> "minecraft:the_end";        // T5 推荐末地
            default -> null;
        };
    }
    
    /**
     * 维度惩罚倍率
     * 在非推荐维度运行时，效率降低
     */
    public static final double DIMENSION_PENALTY = 0.1; // 10% 效率
    
    /**
     * 计算维度效率
     * 
     * @param currentDimension 当前维度 key
     * @param tier 结构等级
     * @return 效率倍率 (0.1 或 1.0)
     */
    public static double getDimensionEfficiency(String currentDimension, int tier) {
        String recommended = getRecommendedDimension(tier);
        if (recommended == null) return 1.0;
        return recommended.equals(currentDimension) ? 1.0 : DIMENSION_PENALTY;
    }
    
    // ==================== 提取间隔 ====================
    // 单位: tick
    
    /** 每次提取操作的间隔 */
    public static final int EXTRACTION_INTERVAL = 20; // 1 秒
    
    // ==================== 最低浓度阈值 ====================
    
    /** 区块浓度低于此值时无法提取 */
    public static final double MIN_CONCENTRATION_THRESHOLD = 5.0;
}