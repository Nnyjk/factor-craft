package com.factorcraft.module.vfx;

import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

/**
 * Factor 元素类型枚举
 * 
 * 定义 Factor 的 5 种基本元素类型，每种类型有独特的视觉效果
 */
public enum FactorElementType {
    /**
     * 火焰 Factor
     * - 橙红色粒子、火星效果
     * - 高能量、破坏性强
     */
    FIRE(
        "fire",
        "Fire Factor",
        Formatting.GOLD,
        0xFF6600,
        ParticleTypes.FLAME,
        ParticleTypes.LAVA
    ),
    
    /**
     * 水流 Factor
     * - 蓝色粒子、水滴效果
     * - 流动性强、治愈特性
     */
    WATER(
        "water",
        "Water Factor",
        Formatting.AQUA,
        0x3399FF,
        ParticleTypes.DRIPPING_WATER,
        ParticleTypes.BUBBLE_POP
    ),
    
    /**
     * 自然 Factor
     * - 绿色粒子、叶子效果
     * - 生命力、生长加速
     */
    NATURE(
        "nature",
        "Nature Factor",
        Formatting.GREEN,
        0x33CC33,
        ParticleTypes.HAPPY_VILLAGER,
        ParticleTypes.COMPOSTER
    ),
    
    /**
     * 虚空 Factor
     * - 紫色粒子、星尘效果
     * - 神秘力量、传送特性
     */
    VOID(
        "void",
        "Void Factor",
        Formatting.DARK_PURPLE,
        0x9933FF,
        ParticleTypes.PORTAL,
        ParticleTypes.REVERSE_PORTAL
    ),
    
    /**
     * 秩序 Factor
     * - 白色粒子、几何图案
     * - 稳定性、效率提升
     */
    ORDER(
        "order",
        "Order Factor",
        Formatting.WHITE,
        0xFFFFFF,
        ParticleTypes.END_ROD,
        ParticleTypes.SNOWFLAKE
    );
    
    private final String id;
    private final String displayName;
    private final Formatting chatColor;
    private final int rgbColor;
    private final ParticleType<?> primaryParticle;
    private final ParticleType<?> secondaryParticle;
    
    FactorElementType(String id, String displayName, Formatting chatColor,
                      int rgbColor, ParticleType<?> primaryParticle,
                      ParticleType<?> secondaryParticle) {
        this.id = id;
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.rgbColor = rgbColor;
        this.primaryParticle = primaryParticle;
        this.secondaryParticle = secondaryParticle;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Formatting getChatColor() {
        return chatColor;
    }
    
    public int getRgbColor() {
        return rgbColor;
    }
    
    /**
     * 获取红色分量 (0-255)
     */
    public int getRed() {
        return (rgbColor >> 16) & 0xFF;
    }
    
    /**
     * 获取绿色分量 (0-255)
     */
    public int getGreen() {
        return (rgbColor >> 8) & 0xFF;
    }
    
    /**
     * 获取蓝色分量 (0-255)
     */
    public int getBlue() {
        return rgbColor & 0xFF;
    }
    
    /**
     * 获取浮动颜色值 (用于粒子效果)
     */
    public float[] getColorComponents() {
        return new float[] {
            getRed() / 255.0f,
            getGreen() / 255.0f,
            getBlue() / 255.0f
        };
    }
    
    public ParticleType<?> getPrimaryParticle() {
        return primaryParticle;
    }
    
    public ParticleType<?> getSecondaryParticle() {
        return secondaryParticle;
    }
    
    /**
     * 根据浓度获取粒子数量
     */
    public int getParticleCount(double concentration, Random random) {
        // 基础数量 + 浓度加成
        int base = 1 + random.nextInt(3);
        double factor = Math.min(1.0, concentration);
        return (int)(base * (1 + factor * 3));
    }
    
    /**
     * 根据浓度获取粒子速度
     */
    public double getParticleSpeed(double concentration) {
        return 0.02 + concentration * 0.05;
    }
    
    /**
     * 从 ID 获取类型
     */
    public static FactorElementType fromId(String id) {
        for (FactorElementType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return ORDER; // 默认返回秩序
    }
    
    /**
     * 随机选择一个类型
     */
    public static FactorElementType random(Random random) {
        return values()[random.nextInt(values().length)];
    }
    
    /**
     * 根据世界浓度选择类型
     */
    public static FactorElementType fromConcentration(double concentration, Random random) {
        // 高浓度时更容易出现稀有类型
        if (concentration > 0.8) {
            // 过载：虚空或火焰
            return random.nextBoolean() ? VOID : FIRE;
        } else if (concentration > 0.6) {
            // 高能：随机
            return random(random);
        } else if (concentration > 0.4) {
            // 稳定：秩序或自然
            return random.nextBoolean() ? ORDER : NATURE;
        } else {
            // 低能：水流或自然
            return random.nextBoolean() ? WATER : NATURE;
        }
    }
}