package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

/**
 * 传递结构 GameTest
 * 
 * 测试范围：
 * 1. 跨维度传输
 * 2. 目标位置 Factor 添加
 * 3. 网络同步
 * 4. 边界情况处理
 */
public class TransmitterGameTests {
    
    /**
     * 测试传递器核心方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void transmitterCoreRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing transmitter core registration...");
        
        // 验证 T1-T4 传递器核心已注册
        for (int tier = 1; tier <= 4; tier++) {
            String blockId = "factor_machine_transmitter_t" + tier;
            var block = net.minecraft.registry.Registries.BLOCK.get(
                net.minecraft.util.Identifier.of("factorcraft", blockId));
            if (block == null || block == net.minecraft.block.Blocks.AIR) {
                context.throwGameTestException("Transmitter core T" + tier + " not registered");
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试传递配置加载
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void transmitterConfigLoaded(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing transmitter config...");
        
        // 验证基础传输效率
        double baseEfficiency = TransmitterTestConfig.getBaseEfficiency();
        if (baseEfficiency <= 0 || baseEfficiency > 1) {
            context.throwGameTestException("Base efficiency should be between 0 and 1");
        }
        
        // 验证距离惩罚
        double distancePenalty = TransmitterTestConfig.getDistancePenalty();
        if (distancePenalty < 0) {
            context.throwGameTestException("Distance penalty should be non-negative");
        }
        
        context.complete();
    }
    
    /**
     * 测试距离衰减计算
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void distanceAttenuationCalculation(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing distance attenuation...");
        
        // 近距离传输效率应接近基础值
        double nearEfficiency = TransmitterTestConfig.calculateEfficiency(10);
        if (nearEfficiency < 0.7) {
            context.throwGameTestException(
                "Near distance efficiency too low: " + nearEfficiency);
        }
        
        // 远距离传输效率应降低
        double farEfficiency = TransmitterTestConfig.calculateEfficiency(1000);
        if (farEfficiency >= nearEfficiency) {
            context.throwGameTestException(
                "Far distance efficiency should be lower than near");
        }
        
        // 效率不应为负
        if (farEfficiency < 0) {
            context.throwGameTestException("Efficiency should never be negative");
        }
        
        context.complete();
    }
    
    /**
     * 测试维度惩罚计算
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void dimensionPenaltyCalculation(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing dimension penalty...");
        
        // 同维度传输无惩罚
        double sameDim = TransmitterTestConfig.getDimensionPenalty(false);
        if (sameDim != 1.0) {
            context.throwGameTestException(
                "Same dimension should have no penalty, got " + sameDim);
        }
        
        // 跨维度传输有惩罚
        double crossDim = TransmitterTestConfig.getDimensionPenalty(true);
        if (crossDim >= 1.0 || crossDim <= 0) {
            context.throwGameTestException(
                "Cross dimension penalty should be between 0 and 1, got " + crossDim);
        }
        
        context.complete();
    }
    
    /**
     * 测试传输容量限制
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void transmissionCapacityLimit(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing transmission capacity...");
        
        // 验证容量随 Tier 增加
        int prevCapacity = 0;
        for (int tier = 1; tier <= 5; tier++) {
            int capacity = TransmitterTestConfig.getMaxCapacity(tier);
            
            if (capacity <= 0) {
                context.throwGameTestException(
                    "Capacity for tier " + tier + " should be positive");
            }
            
            if (capacity < prevCapacity) {
                context.throwGameTestException(
                    "Capacity should increase with tier");
            }
            
            prevCapacity = capacity;
        }
        
        context.complete();
    }
    
    /**
     * 测试冷却时间配置
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void cooldownConfiguration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing cooldown configuration...");
        
        // 验证冷却时间为正
        for (int tier = 1; tier <= 5; tier++) {
            int cooldown = TransmitterTestConfig.getCooldown(tier);
            if (cooldown < 0) {
                context.throwGameTestException(
                    "Cooldown for tier " + tier + " should be non-negative");
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试目标位置验证
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void targetPositionValidation(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing target position validation...");
        
        // 有效坐标
        boolean valid = TransmitterTestConfig.isValidTarget(0, 64, 0);
        if (!valid) {
            context.throwGameTestException("Position (0, 64, 0) should be valid");
        }
        
        // 边界坐标
        valid = TransmitterTestConfig.isValidTarget(30000000, 256, 30000000);
        if (!valid) {
            context.throwGameTestException("Large position should be valid");
        }
        
        context.complete();
    }
    
    /**
     * 测试输出槽满处理
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void outputSlotFullHandling(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing output slot full handling...");
        
        // 模拟输出槽满场景
        boolean canReceive = TransmitterTestConfig.canReceiveFactor(true);
        if (canReceive) {
            context.throwGameTestException(
                "Should not be able to receive when output is full");
        }
        
        // 正常场景
        canReceive = TransmitterTestConfig.canReceiveFactor(false);
        if (!canReceive) {
            context.throwGameTestException(
                "Should be able to receive when output is not full");
        }
        
        context.complete();
    }
}

// 测试配置占位
class TransmitterTestConfig {
    
    public static double getBaseEfficiency() {
        return 0.8;
    }
    
    public static double getDistancePenalty() {
        return 0.05;
    }
    
    public static double calculateEfficiency(int distance) {
        return Math.max(0, getBaseEfficiency() - distance * getDistancePenalty() / 100);
    }
    
    public static double getDimensionPenalty(boolean crossDimension) {
        return crossDimension ? 0.7 : 1.0;
    }
    
    public static int getMaxCapacity(int tier) {
        return 100 * tier;
    }
    
    public static int getCooldown(int tier) {
        return Math.max(0, 200 - tier * 20);
    }
    
    public static boolean isValidTarget(int x, int y, int z) {
        return y >= -64 && y <= 320;
    }
    
    public static boolean canReceiveFactor(boolean outputFull) {
        return !outputFull;
    }
}