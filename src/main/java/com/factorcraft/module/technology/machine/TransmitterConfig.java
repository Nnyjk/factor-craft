package com.factorcraft.module.technology.machine;

import java.util.Map;

/**
 * 传递器配置
 * 
 * 定义 T1-T4 传递器的跨维度传输参数
 */
public final class TransmitterConfig {
    
    private TransmitterConfig() {}
    
    // ==================== 基础效率 ====================
    // 单位: 百分比
    
    public static final double EFFICIENCY_T1 = 0.80;  // 80%
    public static final double EFFICIENCY_T2 = 0.85;  // 85%
    public static final double EFFICIENCY_T3 = 0.90;  // 90%
    public static final double EFFICIENCY_T4 = 0.95;  // 95%
    
    public static double getEfficiency(int tier) {
        return switch (tier) {
            case 1 -> EFFICIENCY_T1;
            case 2 -> EFFICIENCY_T2;
            case 3 -> EFFICIENCY_T3;
            case 4 -> EFFICIENCY_T4;
            default -> EFFICIENCY_T1;
        };
    }
    
    // ==================== 距离损耗 ====================
    // 单位: 每百格损耗百分比
    
    public static final double DISTANCE_LOSS_T1 = 0.010;   // 1%/百格
    public static final double DISTANCE_LOSS_T2 = 0.008;   // 0.8%/百格
    public static final double DISTANCE_LOSS_T3 = 0.005;   // 0.5%/百格
    public static final double DISTANCE_LOSS_T4 = 0.003;   // 0.3%/百格
    
    public static double getDistanceLoss(int tier) {
        return switch (tier) {
            case 1 -> DISTANCE_LOSS_T1;
            case 2 -> DISTANCE_LOSS_T2;
            case 3 -> DISTANCE_LOSS_T3;
            case 4 -> DISTANCE_LOSS_T4;
            default -> DISTANCE_LOSS_T1;
        };
    }
    
    // ==================== 最大传输量 ====================
    // 单位: Factor/次
    
    public static final double MAX_TRANSFER_T1 = 1_000.0;
    public static final double MAX_TRANSFER_T2 = 5_000.0;
    public static final double MAX_TRANSFER_T3 = 25_000.0;
    public static final double MAX_TRANSFER_T4 = 100_000.0;
    
    public static double getMaxTransfer(int tier) {
        return switch (tier) {
            case 1 -> MAX_TRANSFER_T1;
            case 2 -> MAX_TRANSFER_T2;
            case 3 -> MAX_TRANSFER_T3;
            case 4 -> MAX_TRANSFER_T4;
            default -> MAX_TRANSFER_T1;
        };
    }
    
    // ==================== 冷却时间 ====================
    // 单位: ticks
    
    public static final int COOLDOWN_T1 = 200;   // 10 秒
    public static final int COOLDOWN_T2 = 150;   // 7.5 秒
    public static final int COOLDOWN_T3 = 100;   // 5 秒
    public static final int COOLDOWN_T4 = 60;    // 3 秒
    
    public static int getCooldown(int tier) {
        return switch (tier) {
            case 1 -> COOLDOWN_T1;
            case 2 -> COOLDOWN_T2;
            case 3 -> COOLDOWN_T3;
            case 4 -> COOLDOWN_T4;
            default -> COOLDOWN_T1;
        };
    }
    
    // ==================== 缓冲区 ====================
    // 单位: Factor
    
    public static final double BUFFER_T1 = 2_000.0;
    public static final double BUFFER_T2 = 10_000.0;
    public static final double BUFFER_T3 = 50_000.0;
    public static final double BUFFER_T4 = 200_000.0;
    
    public static double getBuffer(int tier) {
        return switch (tier) {
            case 1 -> BUFFER_T1;
            case 2 -> BUFFER_T2;
            case 3 -> BUFFER_T3;
            case 4 -> BUFFER_T4;
            default -> BUFFER_T1;
        };
    }
    
    // ==================== 维度传输倍率 ====================
    
    /**
     * 维度传输倍率表
     * 
     * 基于维度基准值计算:
     * 倍率 = 发送维度基准 / 接收维度基准
     * 
     * 维度基准:
     * - 主世界: 0.5
     * - 下界: 1.5
     * - 末地: 3.0
     */
    public static final Map<String, Map<String, Double>> DIMENSION_MULTIPLIERS = Map.of(
        "minecraft:overworld", Map.of(
            "minecraft:the_nether", 0.33,  // 主世界 → 下界
            "minecraft:the_end", 0.17      // 主世界 → 末地
        ),
        "minecraft:the_nether", Map.of(
            "minecraft:overworld", 3.0,    // 下界 → 主世界
            "minecraft:the_end", 0.5       // 下界 → 末地
        ),
        "minecraft:the_end", Map.of(
            "minecraft:overworld", 6.0,    // 末地 → 主世界
            "minecraft:the_nether", 2.0    // 末地 → 下界
        )
    );
    
    /**
     * 获取维度传输倍率
     * 
     * @param fromDimension 发送维度
     * @param toDimension 接收维度
     * @return 传输倍率（同维度返回 1.0）
     */
    public static double getDimensionMultiplier(String fromDimension, String toDimension) {
        if (fromDimension.equals(toDimension)) {
            return 1.0;
        }
        
        var toMap = DIMENSION_MULTIPLIERS.get(fromDimension);
        if (toMap == null) {
            return 1.0;
        }
        
        return toMap.getOrDefault(toDimension, 1.0);
    }
    
    // ==================== 维度基准值 ====================
    
    public static final double OVERWORLD_BASE = 0.5;
    public static final double NETHER_BASE = 1.5;
    public static final double END_BASE = 3.0;
    
    /**
     * 获取维度基准值
     */
    public static double getDimensionBase(String dimension) {
        return switch (dimension) {
            case "minecraft:overworld" -> OVERWORLD_BASE;
            case "minecraft:the_nether" -> NETHER_BASE;
            case "minecraft:the_end" -> END_BASE;
            default -> 1.0;
        };
    }
    
    // ==================== 传输计算 ====================
    
    /**
     * 计算实际传输量
     * 
     * 公式: 接收 = 发送 × 维度倍率 × 传递器效率 × (1 - 距离损耗)
     * 
     * @param amount 发送量
     * @param fromDimension 发送维度
     * @param toDimension 接收维度
     * @param tier 传递器等级
     * @param distance 距离（方块数）
     * @return 实际接收量
     */
    public static double calculateTransfer(double amount, String fromDimension, 
                                           String toDimension, int tier, double distance) {
        // 维度倍率
        double dimMult = getDimensionMultiplier(fromDimension, toDimension);
        
        // 传递器效率
        double efficiency = getEfficiency(tier);
        
        // 距离损耗
        double distanceLoss = getDistanceLoss(tier);
        double distanceFactor = Math.max(0, 1 - (distance / 100) * distanceLoss);
        
        return amount * dimMult * efficiency * distanceFactor;
    }
    
    /**
     * 计算同维度传输
     * 
     * @param amount 发送量
     * @param tier 传递器等级
     * @param distance 距离
     * @return 实际接收量
     */
    public static double calculateSameDimensionTransfer(double amount, int tier, double distance) {
        double efficiency = getEfficiency(tier);
        double distanceLoss = getDistanceLoss(tier);
        double distanceFactor = Math.max(0, 1 - (distance / 100) * distanceLoss);
        
        return amount * efficiency * distanceFactor;
    }
    
    // ==================== 传递器材料需求 ====================
    
    /**
     * 传递器材料需求
     */
    public static final Map<Integer, String> TIER_MATERIALS = Map.of(
        1, "factorcraft:shadow_steel_ingot",      // T2 材料
        2, "factorcraft:stardust_ingot",          // T3 材料
        3, "factorcraft:ancient_alloy",           // T4 材料
        4, "factorcraft:void_crystal"             // T5 材料
    );
    
    /**
     * 获取传递器所需材料
     */
    public static String getRequiredMaterial(int tier) {
        return TIER_MATERIALS.getOrDefault(tier, "factorcraft:dust_copper_ingot");
    }
    
    // ==================== 传递器名称 ====================
    
    public static final String[] TRANSMITTER_NAMES = {
        "",  // 无 T0
        "基础传递器",    // T1
        "维度传递器",    // T2
        "远古传递器",    // T3
        "仲裁传递器"     // T4
    };
    
    public static String getTransmitterName(int tier) {
        if (tier >= 1 && tier <= 4) {
            return TRANSMITTER_NAMES[tier];
        }
        return "未知传递器";
    }
    
    // ==================== 链接验证 ====================
    
    /**
     * 验证传输是否可行
     */
    public static boolean canTransfer(int tier, String fromDimension, String toDimension) {
        // T1 只能同维度传输
        if (tier == 1 && !fromDimension.equals(toDimension)) {
            return false;
        }
        
        // T2+ 可以跨维度传输
        return true;
    }
    
    /**
     * 获取传输描述
     */
    public static String getTransferDescription(int tier, String fromDimension, String toDimension) {
        if (!canTransfer(tier, fromDimension, toDimension)) {
            return "T1 传递器仅支持同维度传输";
        }
        
        double multiplier = getDimensionMultiplier(fromDimension, toDimension);
        double efficiency = getEfficiency(tier);
        
        if (fromDimension.equals(toDimension)) {
            return String.format("效率: %.0f%%", efficiency * 100);
        } else {
            return String.format("倍率: %.2fx, 效率: %.0f%%", multiplier, efficiency * 100);
        }
    }
}