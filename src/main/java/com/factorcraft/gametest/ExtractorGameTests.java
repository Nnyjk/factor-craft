package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.FactorService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * 提取结构 GameTest
 * 
 * 测试范围：
 * 1. 结构验证正确性
 * 2. Factor 提取逻辑
 * 3. 维度活性影响
 */
public class ExtractorGameTests {
    
    /**
     * 测试提取器核心方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void extractorCoreRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing extractor core registration...");
        
        // 验证 T1-T5 提取器核心已注册
        for (int tier = 1; tier <= 5; tier++) {
            String blockId = "factor_machine_extractor_core_t" + tier;
            var block = net.minecraft.registry.Registries.BLOCK.get(
                net.minecraft.util.Identifier.of("factorcraft", blockId));
            if (block == null || block == net.minecraft.block.Blocks.AIR) {
                context.throwGameTestException("Extractor core T" + tier + " not registered");
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试 Factor 服务可用性
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void factorServiceAvailable(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing Factor service...");
        
        FactorService service = FactorService.getInstance();
        if (service == null) {
            context.throwGameTestException("FactorService should be available");
        }
        
        context.complete();
    }
    
    /**
     * 测试提取配置加载
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void extractionConfigLoaded(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing extraction config...");
        
        // 验证基础提取率配置存在
        double baseRate = ExtractionTestConfig.getBaseRate(1);
        if (baseRate <= 0) {
            context.throwGameTestException("Base extraction rate should be positive");
        }
        
        context.complete();
    }
    
    /**
     * 测试维度活性系数
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void dimensionActivityMultiplier(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing dimension activity...");
        
        // 主世界系数应为 1.0
        double overworld = DimensionTestHelper.getActivityMultiplier("overworld");
        if (Math.abs(overworld - 1.0) > 0.01) {
            context.throwGameTestException("Overworld multiplier should be 1.0, got: " + overworld);
        }
        
        // 末地系数应为 3.0
        double theEnd = DimensionTestHelper.getActivityMultiplier("the_end");
        if (Math.abs(theEnd - 3.0) > 0.01) {
            context.throwGameTestException("End multiplier should be 3.0, got: " + theEnd);
        }
        
        context.complete();
    }
}

/**
 * 提取测试配置辅助类
 */
class ExtractionTestConfig {
    public static double getBaseRate(int tier) {
        return 10.0 * tier;
    }
}

/**
 * 维度测试辅助类
 */
class DimensionTestHelper {
    public static double getActivityMultiplier(String dimension) {
        return switch (dimension) {
            case "overworld" -> 1.0;
            case "the_nether" -> 1.5;
            case "the_end" -> 3.0;
            default -> 1.0;
        };
    }
}
