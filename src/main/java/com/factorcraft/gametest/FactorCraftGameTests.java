package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

/**
 * Factor Craft Game Test 入口
 * 
 * 运行方式:
 * 1. ./gradlew runGameTest - 自动运行所有测试
 * 2. 游戏内执行 /test run <test_name>
 * 
 * 测试覆盖：
 * - 方块/物品注册
 * - 提取系统 (ExtractorGameTests)
 * - 合成系统 (SynthesizerGameTests)
 * - 传递系统 (TransmitterGameTests)
 * - 消耗系统 (ConsumerGameTests)
 */
public class FactorCraftGameTests {
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void blockRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing block registration...");
        
        // 核心方块
        String[] blocks = {
            "factor_ore",
            "deepslate_factor_ore", 
            "raw_factor_block",
            "factor_block",
            "resonance_cluster",
            "factor_node"
        };
        
        for (String blockId : blocks) {
            assertBlockRegistered(context, blockId);
        }
        
        // 机器方块
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_extractor_core_t" + tier);
            assertBlockRegistered(context, "factor_machine_synthesizer_core_t" + tier);
            assertBlockRegistered(context, "factor_machine_transmitter_core_t" + tier);
            assertBlockRegistered(context, "factor_machine_consumer_t" + tier);
            assertBlockRegistered(context, "factor_machine_cultivator_core_t" + tier);
            assertBlockRegistered(context, "factor_machine_breeder_core_t" + tier);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void itemRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing item registration...");
        
        // 核心物品
        String[] items = {
            "raw_factor",
            "factor_ingot",
            "factor_nugget",
            "factor_dust",
            "resonance_dust",
            "stardust_ingot",
            "void_essence",
            "nature_essence",
            "fire_essence",
            "water_essence"
        };
        
        for (String itemId : items) {
            assertItemRegistered(context, itemId);
        }
        
        // Factor 碎片
        for (int tier = 1; tier <= 5; tier++) {
            assertItemRegistered(context, "factor_shard_t" + tier);
        }
        
        // 武器
        String[] weapons = {
            "factor_sword",
            "factor_pickaxe",
            "factor_axe",
            "factor_shovel",
            "factor_hoe"
        };
        
        for (String weaponId : weapons) {
            assertItemRegistered(context, weaponId);
        }
        
        context.complete();
    }
    
    /**
     * 测试 Factor 系统基础功能
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void factorSystemBasics(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing factor system basics...");
        
        // 验证 Factor 服务可用
        var service = com.factorcraft.module.factor.FactorService.getInstance();
        if (service == null) {
            context.throwGameTestException("FactorService should be initialized");
        }
        
        context.complete();
    }
    
    /**
     * 测试潮汐系统
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void tideSystemBasics(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing tide system...");
        
        // 验证潮汐状态枚举
        var statuses = com.factorcraft.module.factor.TideStatus.values();
        if (statuses.length < 5) {
            context.throwGameTestException("Should have at least 5 tide statuses");
        }
        
        // 验证从浓度计算状态
        var lowStatus = com.factorcraft.module.factor.TideStatus.fromConcentration(0.1);
        if (lowStatus != com.factorcraft.module.factor.TideStatus.DEPLETED) {
            context.throwGameTestException("Low concentration should give DEPLETED status");
        }
        
        var highStatus = com.factorcraft.module.factor.TideStatus.fromConcentration(0.9);
        if (highStatus != com.factorcraft.module.factor.TideStatus.OVERLOAD) {
            context.throwGameTestException("High concentration should give OVERLOAD status");
        }
        
        context.complete();
    }
    
    /**
     * 测试配置加载
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void configLoading(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing config loading...");
        
        // 验证关键配置已加载
        var configManager = com.factorcraft.config.ConfigManager.getConfigNames();
        
        if (configManager.isEmpty()) {
            context.throwGameTestException("At least one config should be loaded");
        }
        
        context.complete();
    }
    
    private static void assertBlockRegistered(TestContext context, String blockId) {
        var block = net.minecraft.registry.Registries.BLOCK.get(Identifier.of("factorcraft", blockId));
        if (block == null || block == net.minecraft.block.Blocks.AIR) {
            throw new AssertionError("Block not registered: " + blockId);
        }
    }
    
    private static void assertItemRegistered(TestContext context, String itemId) {
        var item = net.minecraft.registry.Registries.ITEM.get(Identifier.of("factorcraft", itemId));
        if (item == null) {
            throw new AssertionError("Item not registered: " + itemId);
        }
    }
}