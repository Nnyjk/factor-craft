package com.factorcraft.module.factor;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 潮汐状态枚举
 * 
 * 表示当前 Factor 浓度相对于基准值的偏离程度
 * 每种状态对应不同的游戏效果
 */
public enum TideStatus {
    /**
     * 枯竭 (0-20%)
     * - 机器效率 -50%
     * - Factor 提取量 -75%
     * - 玩家疲劳 I（缓慢）
     * - 生物生成率 -25%
     */
    DEPLETED(0.0, 0.2, " depleted", Formatting.DARK_GRAY, -0.50, -0.75, -0.25),
    
    /**
     * 低能 (20-40%)
     * - 机器效率 -25%
     * - Factor 提取量 -50%
     * - 无明显玩家效果
     * - 生物生成率 -10%
     */
    LOW_ENERGY(0.2, 0.4, " Low Energy", Formatting.GRAY, -0.25, -0.50, -0.10),
    
    /**
     * 稳定 (40-60%)
     * - 机器效率正常
     * - Factor 提取量正常
     * - 无玩家效果
     * - 生物生成率正常
     */
    STABLE(0.4, 0.6, " Stable", Formatting.WHITE, 0.0, 0.0, 0.0),
    
    /**
     * 高能 (60-80%)
     * - 机器效率 +25%
     * - Factor 提取量 +50%
     * - 玩家生命恢复 I
     * - 生物变异率 +10%
     */
    HIGH_ENERGY(0.6, 0.8, " High Energy", Formatting.AQUA, 0.25, 0.50, 0.10),
    
    /**
     * 过载 (80-100%)
     * - 机器效率 +50%
     * - Factor 提取量 +100%
     * - 玩家力量 I 但缓慢掉血
     * - 生物变异率 +50%
     * - 机器过载风险
     */
    OVERLOAD(0.8, 1.0, " Overload", Formatting.DARK_PURPLE, 0.50, 1.00, 0.50);

    private final double minConcentration;
    private final double maxConcentration;
    private final String displayName;
    private final Formatting color;
    
    /** 机器效率修正系数 (-0.5 to +0.5) */
    private final double machineEfficiencyModifier;
    
    /** Factor 提取量修正系数 (-0.75 to +1.0) */
    private final double extractionModifier;
    
    /** 生物生成率修正系数 (-0.25 to +0.5) */
    private final double spawnRateModifier;

    TideStatus(double minConcentration, double maxConcentration, String displayName, 
               Formatting color, double machineEfficiencyModifier, 
               double extractionModifier, double spawnRateModifier) {
        this.minConcentration = minConcentration;
        this.maxConcentration = maxConcentration;
        this.displayName = displayName;
        this.color = color;
        this.machineEfficiencyModifier = machineEfficiencyModifier;
        this.extractionModifier = extractionModifier;
        this.spawnRateModifier = spawnRateModifier;
    }

    /**
     * 根据 Factor 浓度获取潮汐状态
     * @param concentration Factor 浓度 (0.0-1.0)
     * @return 对应的潮汐状态
     */
    public static TideStatus fromConcentration(double concentration) {
        if (concentration < 0.2) {
            return DEPLETED;
        } else if (concentration < 0.4) {
            return LOW_ENERGY;
        } else if (concentration < 0.6) {
            return STABLE;
        } else if (concentration < 0.8) {
            return HIGH_ENERGY;
        } else {
            return OVERLOAD;
        }
    }

    /**
     * 获取显示名称（带颜色）
     */
    public Text getDisplayName() {
        return Text.literal(this.displayName).styled(style -> style.withColor(this.color));
    }

    /**
     * 获取纯文本名称
     */
    public String getName() {
        return this.displayName.trim();
    }

    /**
     * 获取颜色格式
     */
    public Formatting getColor() {
        return this.color;
    }
    
    /**
     * 机器效率修正系数
     * @return -0.5 到 +0.5 之间的值
     */
    public double getMachineEfficiencyModifier() {
        return this.machineEfficiencyModifier;
    }
    
    /**
     * Factor 提取量修正系数
     * @return -0.75 到 +1.0 之间的值
     */
    public double getExtractionModifier() {
        return this.extractionModifier;
    }
    
    /**
     * 生物生成率修正系数
     * @return -0.25 到 +0.5 之间的值
     */
    public double getSpawnRateModifier() {
        return this.spawnRateModifier;
    }
    
    /**
     * 应用机器效率修正
     * @param baseEfficiency 基础效率
     * @return 修正后的效率
     */
    public double applyMachineEfficiency(double baseEfficiency) {
        return baseEfficiency * (1.0 + this.machineEfficiencyModifier);
    }
    
    /**
     * 应用提取量修正
     * @param baseAmount 基础提取量
     * @return 修正后的提取量
     */
    public double applyExtractionAmount(double baseAmount) {
        return baseAmount * (1.0 + this.extractionModifier);
    }
    
    /**
     * 是否为稳定状态
     */
    public boolean isStable() {
        return this == STABLE;
    }
    
    /**
     * 是否为有益状态（高能或过载）
     */
    public boolean isBeneficial() {
        return this == HIGH_ENERGY || this == OVERLOAD;
    }
    
    /**
     * 是否为有害状态（枯竭或低能）
     */
    public boolean isHarmful() {
        return this == DEPLETED || this == LOW_ENERGY;
    }
    
    /**
     * 是否有过载风险
     */
    public boolean hasOverloadRisk() {
        return this == OVERLOAD;
    }
}
